package education.x.commons

import java.math.BigInteger
import java.nio.ByteBuffer

import org.nutz.ssdb4j.spi.SSDB

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait SortedSet[Key, Value] {

  def get(key: Key): Future[Option[Value]]

  def mget(keys: Array[Key]): Future[Option[Map[Key, Value]]]

  def add(k: Key, v: Value): Future[Boolean]

  def madd(arrKeyAndValue: Array[(String, Long)]): Future[Boolean]

  def remove(key: Key): Future[Boolean]

  def mremove(keys: Array[Key]): Future[Boolean]

  /** *
    *
    * @param key
    * @param reverseOrder true: => lower score higher rank, false: higher score higher rank
    * @return
    */
  def rank(key: Key, reverseOrder: Boolean): Future[Option[Int]]

  def range(from: Int, num: Int, reverseOrder: Boolean): Future[Option[Array[(String, Long)]]]

  def clear(): Future[Boolean]

  def size(): Future[Option[Int]]

}

/** *
  *
  * @param dbName
  * @param client
  * @param ec
  */

case class SsdbSortedSet(dbName: String, client: SSDB)(implicit ec: ExecutionContext = ExecutionContext.global)
  extends SortedSet[String, Long] {
  override def get(key: String): Future[Option[Long]] = {
    Future {
      val resp = client.zget(dbName, key)

      if (resp.ok()) Some(resp.asLong())
      else None

    }
  }

  override def mget(keys: Array[String]): Future[Option[Map[String, Long]]] = {
    Future {
      val resp = client.multi_zget(dbName, keys: _*)
      if (resp.ok() && resp.datas.size() % 2 == 0) {

        var map = collection.mutable.Map[String, Long]()
        val it = resp.datas.iterator
        while (it.hasNext) {
          map.put(new String(it.next()), (new String(it.next())).toLong)
        }
        Some(map.toMap)
      }
      else None
    }
  }

  override def add(k: String, v: Long): Future[Boolean] = {
    Future {
      client.zset(dbName, k, v).ok()
    }
  }

  override def madd(arrKeyAndValue: Array[(String, Long)]): Future[Boolean] = {
    Future {
      val data: Array[Object] = arrKeyAndValue.flatMap(f => Array[Object](f._1, f._2.asInstanceOf[Object]))
      client.multi_zset(dbName, data: _*).ok()
    }
  }

  override def remove(key: String): Future[Boolean] = {
    Future {
      client.multi_zdel(dbName, key).ok()
    }
  }

  override def mremove(keys: Array[String]): Future[Boolean] = {
    Future {
      client.multi_zdel(dbName, keys: _*).ok()
    }
  }

  override def size(): Future[Option[Int]] = {
    Future {
      val resp = client.zsize(dbName)
      if (resp.ok()) Some(resp.asInt())
      else None
    }
  }

  override def clear(): Future[Boolean] = {
    Future {
      client.zclear(dbName).ok()
    }
  }

  override def rank(key: String, reverseOrder: Boolean = false): Future[Option[Int]] = {
    Future {
      val resp = if (reverseOrder) {
        client.zrank(dbName, key)
      } else {
        client.zrrank(dbName, key)
      }
      if (resp.ok()) Some(resp.asInt())
      else None
    }
  }

  override def range(from: Int, num: Int, reverseOrder: Boolean = false): Future[Option[Array[(String, Long)]]] = {
    Future {
      val resp = if (reverseOrder) {
        client.zrange(dbName, from, num)
      } else {
        client.zrrange(dbName, from, num)
      }
      import scala.collection.JavaConverters._
      if (resp.ok()) Some(resp.listString()
        .asScala.toArray
        .grouped(2).toArray
        .map(arr => (arr(0), arr(1).toLong)))
      else None
    }
  }
}



