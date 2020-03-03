package xcommon.lib

import org.nutz.ssdb4j.spi.SSDB

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters.mapAsScalaMapConverter

trait KVS[Key, Value] {

  def get(key: Key): Future[Option[Value]]

  def mget(keys: Array[Key]): Future[Option[Map[Key, Value]]]

  def add(k: Key, v: Value): Future[Boolean]

  def madd(arrKeyAndValue: Array[(Key, Value)]): Future[Boolean]

  def remove(key: Key): Future[Boolean]

  def mremove(keys: Array[Key]): Future[Boolean]

  def size(): Future[Option[Int]]

  def clear(): Future[Boolean]


}


case class KVSDbImpl(dbName: String, client: SSDB)(implicit ec: ExecutionContext = ExecutionContext.global) extends KVS[String, String] {

  override def get(key: String): Future[Option[String]] = {
    Future {
      val resp = client.hget(dbName, key)
      if (resp.ok())
        Some(resp.asString())
      else
        None
    }
  }

  override def mget(keys: Array[String]): Future[Option[Map[String, String]]] = {
    Future {
      val resp = client.multi_hget(dbName, keys:_*)
      if (resp.ok()) {
        Some(resp.mapString().asScala.toMap)
      }
      else None
    }
  }

  override def add(k: String, v: String): Future[Boolean] = {
    Future {
      client.hset(dbName, k, v).ok()
    }
  }


  /**
    * ex: SSDB::madd(k1,v1,k2,v2,k3,v3)
    *
    * @param arrKeyAndValue
    * @return
    */
  override def madd(arrKeyAndValue: Array[(String, String)]): Future[Boolean] = {
    Future {
      val arrKV: Array[String] = arrKeyAndValue.flatMap(item => Array(item._1, item._2))
      client.multi_hset(dbName, arrKV: _*).ok()
    }
  }

  override def remove(key: String): Future[Boolean] = {
    Future {
      client.multi_hdel(dbName, key).ok()
    }
  }

  override def mremove(keys: Array[String]): Future[Boolean] = {
    Future {
      client.multi_hdel(dbName, keys:_*).ok()
    }
  }

  override def size(): Future[Option[Int]] = {
    Future {
      val resp = client.hsize(dbName)
      if (resp.ok()) Some(resp.asInt())
      else None

    }
  }

  override def clear(): Future[Boolean] = {
    Future {
      client.hclear(dbName).ok()
    }
  }
}