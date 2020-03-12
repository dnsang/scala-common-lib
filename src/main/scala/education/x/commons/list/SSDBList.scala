package education.x.commons.list

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.impl.DefaultObjectConv
import org.nutz.ssdb4j.spi.{Cmd, ObjectConv, Response, SSDB}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag


trait Serializer[T] {

  def fromByte(bytes: Array[Byte]): T

  def toByte(value: T): Array[Byte]

}

case class SSDBList[T: ClassTag](name: String,
                                 client: SSDB)(
                                  implicit serializer: Serializer[T],
                                  ec: ExecutionContext = ExecutionContext.global
                                ) extends List[T] {


  override def pushFront(value: T): Future[Boolean] = Future {
    client.qpush_front(name, serializer.toByte(value)).ok()
  }

  override def pushBack(value: T): Future[Boolean] = Future {
    client.qpush_back(name, serializer.toByte(value)).ok()
  }

  override def multiPushFront(values: Array[T]): Future[Boolean] = Future {
    val bytes = values.map(serializer.toByte)
    client.multi_push_front(name, bytes: _*).ok()
  }

  override def multiPushBack(values: Array[T]): Future[Boolean] = Future {
    val bytes = values.map(serializer.toByte)
    client.multi_push_back(name, bytes: _*).ok()
  }

  override def size(): Future[Option[Int]] = Future {
    val r = client.qsize(name)
    if (r.ok()) Some(r.asInt()) else None
  }

  override def popFront(): Future[Option[T]] = Future {
    val r = client.qpop_front(name)
    getValueAsOption(r)
  }

  override def popBack(): Future[Option[T]] = Future {
    val r = client.qpop_back(name)
    getValueAsOption(r)
  }

  override def multiPopBack(size: Int): Future[Option[Array[T]]] = Future {
    val r = client.multi_pop_back(name, size)
    getArrayValueAsOption(r)
  }

  override def multiPopFront(size: Int): Future[Option[Array[T]]] = Future {
    val r = client.multi_pop_front(name, size)
    getArrayValueAsOption(r)
  }

  override def clear(): Future[Boolean] = Future {
    client.qclear(name).ok()
  }

  override def get(index: Int): Future[Option[T]] = Future {
    val r = client.qget(name, index)
    getValueAsOption(r)
  }

  override def get(from: Int, num: Int): Future[Option[Array[T]]] = Future {
    val r = client.qrange(name, from, num)
    getArrayValueAsOption(r)
  }

  override def set(index: Int, value: T): Future[Boolean] = Future {
    client.qset(name, index, serializer.toByte(value)).ok()
  }

  override def getFront(): Future[Option[T]] = Future {
    val r = client.qfront(name)
    getValueAsOption(r)
  }

  override def getBack(): Future[Option[T]] = Future {
    val r = client.qback(name)
    getValueAsOption(r)
  }

  override def getAll(): Future[Option[Array[T]]] = Future {
    val r = client.qsize(name)
    if (r.ok()) {
      val r2 = client.qrange(name, 0, r.asInt())
      getArrayValueAsOption(r2)
    } else {
      None
    }
  }

  private def getValueAsOption(r: Response): Option[T] = {
    if (r.ok() && r.datas.size() == 1) Some(serializer.fromByte(r.datas.get(0))) else None
  }

  private def getArrayValueAsOption(r: Response): Option[Array[T]] = {
    if (r.ok()) Some(r.datas.asScala.map(f => serializer.fromByte(f)).toArray) else None
  }

  implicit class ImplicitSSDB(client: SSDB) {
    private val cmdSet = new Cmd("qset", false, true)
    private val qpush_back = new Cmd("qpush_back", false, false)
    private val qpush_front = new Cmd("qpush_front", false, false)
    private val qpop_front = new Cmd("qpop_front", false, false)
    private val qpop_back = new Cmd("qpop_back", false, false)

    private val converter: ObjectConv = new DefaultObjectConv()

    private def bytes(obj: Any): Array[Byte] = converter.bytes(obj)

    private def bytess(obj: Object*) = converter.bytess(obj: _*)

    def qset(key: Any, index: Int, value: Any): Response = {
      val keyAsBytes = bytes(key)
      val indexAsBytes = String.valueOf(index).getBytes()
      val valueAsBytes = bytes(value)
      client.req(cmdSet, keyAsBytes, indexAsBytes, valueAsBytes)
    }

    def multi_push_front(key: Any, values: Object*): Response = {
      val keyAsBytes = bytes(key)
      val valuesAsBytes = bytess(values: _*)
      val input = keyAsBytes +: valuesAsBytes
      client.req(qpush_front, input: _*)
    }

    def multi_push_back(key: Any, values: Object*): Response = {
      val keyAsBytes = bytes(key)
      val valuesAsBytes = bytess(values: _*)
      val input = keyAsBytes +: valuesAsBytes
      client.req(qpush_back, input: _*)
    }

    def multi_pop_front(key: Any, size: Int): Response = {
      val keyAsBytes = bytes(key)
      val sizeAsBytes = size.toString.getBytes()
      client.req(qpop_front, keyAsBytes, sizeAsBytes)
    }

    def multi_pop_back(key: Any, size: Int): Response = {
      val keyAsBytes = bytes(key)
      val sizeAsBytes = size.toString.getBytes()
      client.req(qpop_back, keyAsBytes, sizeAsBytes)
    }
  }

}

object SSDBList {


  def apply[T: ClassTag](name: String
                        )(
                          implicit serializer: Serializer[T],
                          ec: ExecutionContext
                        ): SSDBList[T] = SSDBList(name, SSDBs.DEFAULT_HOST, SSDBs.DEFAULT_PORT)

  def apply[T: ClassTag](name: String,
                         host: String,
                         port: Int)(
                          implicit serializer: Serializer[T],
                          ec: ExecutionContext
                        ): SSDBList[T] = SSDBList(name, host, port, SSDBs.DEFAULT_TIMEOUT)

  def apply[T: ClassTag](name: String,
                         host: String,
                         port: Int,
                         timeout: Int)(
                          implicit serializer: Serializer[T],
                          ec: ExecutionContext
                        ): SSDBList[T] = SSDBList(name, host, port, timeout, null)

  def apply[T: ClassTag](name: String,
                         host: String,
                         port: Int,
                         timeout: Int,
                         config: AnyRef)(
                          implicit serializer: Serializer[T],
                          ec: ExecutionContext
                        ): SSDBList[T] = SSDBList(name, host, port, timeout, null, null)

  def apply[T: ClassTag](name: String,
                         host: String,
                         port: Int,
                         timeout: Int,
                         config: AnyRef,
                         auth: Array[Byte])(
                          implicit serializer: Serializer[T],
                          ec: ExecutionContext
                        ): SSDBList[T] = {
    val client = SSDBs.pool(host, port, timeout, config, auth)
    SSDBList[T](name, client)
  }


  implicit object ShortSerializer extends Serializer[Short] {

    override def fromByte(bytes: Array[Byte]): Short = new String(bytes).toShort

    override def toByte(value: Short): Array[Byte] = value.toString.getBytes()
  }


  implicit object IntSerializer extends Serializer[Int] {

    override def fromByte(bytes: Array[Byte]): Int = new String(bytes).toInt

    override def toByte(value: Int): Array[Byte] = value.toString.getBytes()
  }

  implicit object LongSerializer extends Serializer[Long] {

    override def fromByte(bytes: Array[Byte]): Long = new String(bytes).toLong

    override def toByte(value: Long): Array[Byte] = value.toString.getBytes()
  }

  implicit object DoubleSerializer extends Serializer[Double] {

    override def fromByte(bytes: Array[Byte]): Double = new String(bytes).toDouble

    override def toByte(value: Double): Array[Byte] = value.toString.getBytes()
  }

  implicit object StringSerializer extends Serializer[String] {

    override def fromByte(bytes: Array[Byte]): String = new String(bytes)

    override def toByte(value: String): Array[Byte] = value.getBytes()
  }

  implicit object BoolSerializer extends Serializer[Boolean] {

    override def fromByte(bytes: Array[Byte]): Boolean = new String(bytes).toBoolean

    override def toByte(value: Boolean): Array[Byte] = value.toString.getBytes()
  }

  implicit object FloatSerializer extends Serializer[Float] {

    override def fromByte(bytes: Array[Byte]): Float = new String(bytes).toFloat

    override def toByte(value: Float): Array[Byte] = value.toString.getBytes()
  }

}
