package education.x.commons


import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.{Response, SSDB}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

case class SsdbKVS[Key: ClassTag, Value: ClassTag](name: String,
                                                   client: SSDB)(
                                                    implicit keySerializer: Serializer[Key],
                                                    valueSerializer: Serializer[Value],
                                                    ec: ExecutionContext = ExecutionContext.global
                                                  ) extends KVS[Key, Value] {
  private def getValueAsOption(r: Response): Option[Value] = {
    if (r.ok() && r.datas.size() == 1) {
      Some(valueSerializer.fromByte(r.datas.get(0)))
    } else None
  }

  private def getMapKeyValueAsOption(r: Response): Option[Map[Key, Value]] = {
    if (r.ok() && r.datas.size() % 2 == 0) {
      val keyValues = r.datas.asScala.grouped(2)
      val map = keyValues
        .map(grouped => {
          val key = keySerializer.fromByte(grouped.head)
          val value = valueSerializer.fromByte(grouped.last)
          key -> value
        })
        .toMap
      Some(map)
    } else None
  }

  override def get(key: Key): Future[Option[Value]] = Future {
    val resp = client.hget(name, keySerializer.toByte(key))
    getValueAsOption(resp)
  }

  override def multiGet(keys: Array[Key]): Future[Option[Map[Key, Value]]] =
    Future {
      val keysAsBytes = keys.map(keySerializer.toByte)
      val resp = client.multi_hget(name, keysAsBytes: _*)
      getMapKeyValueAsOption(resp)
    }

  override def add(key: Key, value: Value): Future[Boolean] = Future {
    client.hset(name, keySerializer.toByte(key), valueSerializer.toByte(value)).ok()
  }

  override def multiAdd(arrKeyAndValue: Array[(Key, Value)]): Future[Boolean] =
    Future {
      val arrKV = arrKeyAndValue.flatMap(
        item => Array(keySerializer.toByte(item._1), valueSerializer.toByte(item._2))
      )
      client.multi_hset(name, arrKV: _*).ok()
    }

  override def remove(key: Key): Future[Boolean] = Future {
    client.multi_hdel(name, keySerializer.toByte(key)).ok()
  }

  override def multiRemove(keys: Array[Key]): Future[Boolean] = Future {
    val keysAsBytes = keys.map(keySerializer.toByte)
    client.multi_hdel(name, keysAsBytes: _*).ok()
  }

  override def size(): Future[Option[Int]] = Future {
    val resp = client.hsize(name)
    if (resp.ok()) Some(resp.asInt())
    else None
  }

  override def clear(): Future[Boolean] = Future {
    client.hclear(name).ok()
  }
}

object SsdbKVS {


  def apply[Key: ClassTag, Value: ClassTag](name: String)
                                           (
                                             implicit keySerializer: Serializer[Key],
                                             valueSerializer: Serializer[Value],
                                             ec: ExecutionContext
                                           ): SsdbKVS[Key, Value] = SsdbKVS[Key, Value](name, SSDBs.DEFAULT_HOST, SSDBs.DEFAULT_PORT)

  def apply[Key: ClassTag, Value: ClassTag](name: String,
                                            host: String,
                                            port: Int)(
                                             implicit keySerializer: Serializer[Key],
                                             valueSerializer: Serializer[Value],
                                             ec: ExecutionContext
                                           ): SsdbKVS[Key, Value] = SsdbKVS[Key, Value](name, host, port, SSDBs.DEFAULT_TIMEOUT)

  def apply[Key: ClassTag, Value: ClassTag](name: String,
                                            host: String,
                                            port: Int,
                                            timeout: Int)(
                                             implicit keySerializer: Serializer[Key],
                                             valueSerializer: Serializer[Value],
                                             ec: ExecutionContext
                                           ): SsdbKVS[Key, Value] = SsdbKVS[Key, Value](name, host, port, timeout, null)

  def apply[Key: ClassTag, Value: ClassTag](name: String,
                                            host: String,
                                            port: Int,
                                            timeout: Int,
                                            config: AnyRef)(
                                             implicit keySerializer: Serializer[Key],
                                             valueSerializer: Serializer[Value],
                                             ec: ExecutionContext
                                           ): SsdbKVS[Key, Value] = SsdbKVS[Key, Value](name, host, port, timeout, null, null)

  def apply[Key: ClassTag, Value: ClassTag](name: String,
                                            host: String,
                                            port: Int,
                                            timeout: Int,
                                            config: AnyRef,
                                            auth: Array[Byte])(
                                             implicit keySerializer: Serializer[Key],
                                             valueSerializer: Serializer[Value],
                                             ec: ExecutionContext
                                           ): SsdbKVS[Key, Value] = {
    val client = SSDBs.pool(host, port, timeout, config, auth)
    SsdbKVS[Key, Value](name, client)
  }
}
