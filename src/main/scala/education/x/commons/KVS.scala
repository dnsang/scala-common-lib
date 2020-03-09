package education.x.commons

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.{Response, SSDB}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait KVS[Key, Value] {

  def get(key: Key): Future[Option[Value]]

  def multiGet(keys: Array[Key]): Future[Option[Map[Key, Value]]]

  def add(k: Key, v: Value): Future[Boolean]

  def multiAdd(arrKeyAndValue: Array[(Key, Value)]): Future[Boolean]

  def remove(key: Key): Future[Boolean]

  def multiRemove(keys: Array[Key]): Future[Boolean]

  def size(): Future[Option[Int]]

  def clear(): Future[Boolean]

}

abstract class KVSImpl[Key: ClassTag, Value: ClassTag](dbName: String,
                                                       client: SSDB)(
  implicit keyToByte: Key => Array[Byte],
  byteToKey: Array[Byte] => Key,
  valueToByte: Value => Array[Byte],
  byteToValue: Array[Byte] => Value,
  ec: ExecutionContext = ExecutionContext.global
) extends KVS[Key, Value] {
  private def getValueAsOption(r: Response): Option[Value] = {
    if (r.ok() && r.datas.size() == 1) {
      Some(byteToValue(r.datas.get(0)))
    } else None
  }

  private def getMapKeyValueAsOption(r: Response): Option[Map[Key, Value]] = {
    if (r.ok() && r.datas.size() % 2 == 0) {
      val keyValues = r.datas.asScala.grouped(2)
      val map = keyValues
        .map(grouped => {
          val key = byteToKey(grouped.head)
          val value = byteToValue(grouped.last)
          key -> value
        })
        .toMap
      Some(map)
    } else None
  }

  override def get(key: Key): Future[Option[Value]] = Future {
    val resp = client.hget(dbName, keyToByte(key))
    getValueAsOption(resp)
  }

  override def multiGet(keys: Array[Key]): Future[Option[Map[Key, Value]]] =
    Future {
      val keysAsBytes = keys.map(keyToByte)
      val resp = client.multi_hget(dbName, keysAsBytes: _*)
      getMapKeyValueAsOption(resp)
    }

  override def add(key: Key, value: Value): Future[Boolean] = Future {
    client.hset(dbName, keyToByte(key), valueToByte(value)).ok()
  }

  override def multiAdd(arrKeyAndValue: Array[(Key, Value)]): Future[Boolean] =
    Future {
      val arrKV = arrKeyAndValue.flatMap(
        item => Array(keyToByte(item._1), valueToByte(item._2))
      )
      client.multi_hset(dbName, arrKV: _*).ok()
    }

  override def remove(key: Key): Future[Boolean] = Future {
    client.multi_hdel(dbName, keyToByte(key)).ok()
  }

  override def multiRemove(keys: Array[Key]): Future[Boolean] = Future {
    val keysAsBytes = keys.map(keyToByte)
    client.multi_hdel(dbName, keysAsBytes: _*).ok()
  }

  override def size(): Future[Option[Int]] = Future {
    val resp = client.hsize(dbName)
    if (resp.ok()) Some(resp.asInt())
    else None
  }

  override def clear(): Future[Boolean] = Future {
    client.hclear(dbName).ok()
  }
}

object KVSDBImpl {
  private val charset = SSDBs.DEFAULT_CHARSET
  def stringToBytes(value: String): Array[Byte] = value.getBytes()

  def bytesToString(bytes: Array[Byte]): String = new String(bytes, charset)

  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit evKey: ClassTag[String],
    evValue: ClassTag[String]
  ): KVSDBImpl = {
    val ssdb = SSDBs.pool(host, port, timeout, null)
    new KVSDBImpl(dbname, ssdb)(evKey, evValue)
  }
}

case class KVSDBImpl(dbname: String, client: SSDB)(
  implicit evKey: ClassTag[String],
  evValue: ClassTag[String]
) extends KVSImpl[String, String](dbname, client)(
      evKey,
      evValue,
      keyToByte = KVSDBImpl.stringToBytes,
      valueToByte = KVSDBImpl.stringToBytes,
      byteToValue = KVSDBImpl.bytesToString,
      byteToKey = KVSDBImpl.bytesToString
    )

object KVIntDBImpl {
  def intToByte(value: Int): Array[Byte] = String.valueOf(value).getBytes()
  def byteToInt(bytes: Array[Byte]): Int = new String(bytes).toInt

  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
          implicit evKey: ClassTag[String],
          evValue: ClassTag[Int]
  ): KVIntDBImpl = {
    val ssdb = SSDBs.pool(host, port, timeout, null)
    new KVIntDBImpl(dbname, ssdb)(evKey, evValue)
  }
}

case class KVIntDBImpl(dbname: String, client: SSDB)(
        implicit evKey: ClassTag[String],
        evValue: ClassTag[Int]
) extends KVSImpl[String, Int](dbname, client)(
  evKey,
  evValue,
  keyToByte = KVSDBImpl.stringToBytes,
  valueToByte = KVIntDBImpl.intToByte,
  byteToValue = KVIntDBImpl.byteToInt,
  byteToKey = KVSDBImpl.bytesToString
)