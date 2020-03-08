package education.x.commons

import org.nutz.ssdb4j.impl.DefaultObjectConv
import org.nutz.ssdb4j.spi.{Cmd, ObjectConv, Response, SSDB}

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.reflect.ClassTag

abstract class ListImpl[T: ClassTag](dbname: String, client: SSDB)(
  implicit toByte: T => Array[Byte],
  fromByte: Array[Byte] => T,
  ec: ExecutionContext = ExecutionContext.global
) extends List[T] {

  override def pushFront(value: T): Future[Boolean] = Future {
    client.qpush_front(dbname, toByte(value)).ok()
  }

  override def pushBack(value: T): Future[Boolean] = Future {
    client.qpush_back(dbname, toByte(value)).ok()
  }

  override def size(): Future[Option[Int]] = Future {
    val r = client.qsize(dbname)
    if (r.ok()) Some(r.asInt()) else None
  }

  override def popFront(): Future[Option[T]] = Future {
    val r = client.qpop_front(dbname)
    if (r.ok() && r.datas.size() == 1) Some(fromByte(r.datas.get(0))) else None
  }

  override def popBack(): Future[Option[T]] = Future {
    val r = client.qpop_back(dbname)
    if (r.ok() && r.datas.size() == 1) Some(fromByte(r.datas.get(0))) else None
  }

  override def clear(): Future[Boolean] = Future {
    client.qclear(dbname).ok()
  }

  override def get(index: Int): Future[Option[T]] = Future {
    val r = client.qget(dbname, index)
    if (r.ok() && r.datas.size() == 1) Some(fromByte(r.datas.get(0))) else None
  }

  override def get(from: Int, num: Int): Future[Option[Array[T]]] = Future {
    val r = client.qrange(dbname, from, num)
    if (r.ok()) Some(r.datas.asScala.map(f => fromByte(f)).toArray) else None
  }

  override def set(index: Int, value: T): Future[Boolean] = Future {
    client.qset(dbname, index, toByte(value)).ok()
  }


  override def getFront(): Future[Option[T]] = Future {
    val r = client.qfront(dbname)
    if (r.ok() && r.datas.size() == 1) Some(fromByte(r.datas.get(0))) else None
  }

  override def getBack(): Future[Option[T]] = Future {
    val r = client.qback(dbname)
    if (r.ok() && r.datas.size() == 1) Some(fromByte(r.datas.get(0))) else None
  }

  override def getAll(): Future[Option[Array[T]]] = Future {
    val r = client.qsize(dbname)
    if (r.ok()) {
      val r2 = client.qrange(dbname, 0, r.asInt())
      if (r2.ok()) Some(r2.datas.asScala.map(f => fromByte(f)).toArray) else None
    } else {
      None
    }
  }

  implicit class ImplicitSSDB(client: SSDB) {
    private val cmdSet = new Cmd("qset", false, true)
    private val converter: ObjectConv = new DefaultObjectConv()

    private def bytes(obj: Any): Array[Byte] = converter.bytes(obj)

    def qset(key: Any, index: Int, value: Any): Response = {
      val keyAsBytes = bytes(key)
      val indexAsBytes = String.valueOf(index).getBytes()
      val valueAsBytes = bytes(value)
      client.req(cmdSet, keyAsBytes, indexAsBytes, valueAsBytes)
    }
  }

}

case class List32Impl(dbname: String, client: SSDB)(implicit ev: ClassTag[Int]) extends ListImpl[Int](dbname, client)(ev, value => value.toString.getBytes(), bytes => Integer.parseInt(new String(bytes)))

case class ListStringImpl(dbname: String, client: SSDB)(implicit ev: ClassTag[String]) extends ListImpl[String](dbname, client)(ev, value => value.toString.getBytes(), bytes => new String(bytes))