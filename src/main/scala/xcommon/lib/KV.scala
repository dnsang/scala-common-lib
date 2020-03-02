package xcommon.lib

import org.nutz.ssdb4j.spi.SSDB

import scala.concurrent.{ExecutionContext, Future}


trait KV[Key, Value] {

  def get(key: Key): Future[Option[Value]]

  def mget(keys: Key*): Future[Option[Map[Key, Value]]]

  def add(k: Key, v: Value): Future[Boolean]

  def madd(arrKeyAndValue: Any*): Future[Boolean]

  def remove(key: Key): Future[Boolean]

  def mremove(keys: Key*): Future[Boolean]

  def size(): Future[Option[Int]]

  def clear(): Future[Boolean]


}


case class KVDbImpl[Key, Value](dbName: String, client: SSDB)(implicit ec: ExecutionContext = ExecutionContext.global) extends KV[Key, Value] {

  override def get(key: Key): Future[Option[Value]] = {
    Future {
      val resp = client.hget(dbName, key)
      if (resp.ok()) {
        Some(resp.asInstanceOf[Value])
      }
      None
    }
  }

  override def mget(keys: Key*): Future[Option[Map[Key, Value]]] = {
    Future {
      val resp = client.multi_hget(dbName, keys)
      if (resp.ok()) Some(resp.asInstanceOf[Map[Key, Value]])
      None
    }
  }

  override def add(k: Key, v: Value): Future[Boolean] = {
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
  override def madd(arrKeyAndValue: Any*): Future[Boolean] = {
    Future {
      client.multi_hset(dbName, arrKeyAndValue).ok()
    }
  }

  override def remove(key: Key): Future[Boolean] = {
    Future {
      client.multi_hdel(dbName, key).ok()
    }
  }

  override def mremove(keys: Key*): Future[Boolean] = {
    Future {
      client.multi_hdel(dbName, keys).ok()
    }
  }

  override def size(): Future[Int] = {
    Future {
      client.hsize(dbName).asInt()
    }
  }

  override def clear(): Future[Boolean] = {
    Future {
      client.hclear(dbName).ok()
    }
  }
}