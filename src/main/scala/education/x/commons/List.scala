package education.x.commons

import java.nio.charset.Charset

import com.sun.org.apache.xpath.internal.functions.FuncTrue
import education.x.commons.Implicits.{ImplicitSSBDResponse, ImplicitSSDB}
import org.nutz.ssdb4j.impl.DefaultObjectConv
import org.nutz.ssdb4j.spi.{Cmd, ObjectConv, Response, SSDB}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.concurrent.{ExecutionContext, Future}

trait List[T] {

  def pushFront(value: T): Future[Boolean]

  def pushBack(value: T): Future[Boolean]

  def size(): Future[Option[Int]]

  def popFront(): Future[Option[T]]

  def popBack(): Future[Option[T]]

  def clear(): Future[Boolean]

  def get(index: Int): Future[Option[T]]

  def head(): Future[Option[T]]

  def last(): Future[Option[T]]

  def get(from: Int, num: Int): Future[Option[Array[T]]]

  def set(index: Int, value: T): Future[Boolean]

  def getAll(): Future[Option[Array[T]]]
}

object Implicits {
  implicit class ImplicitSSBDResponse(r: Response) {
    def getIntAsOption(): Option[Int] = {
      if (r.ok())
        Some(r.asInt())
      else
        None
    }

    def getArrayIntAsOption(): Option[Array[Int]] = {
      if (r.ok()) {
        val items = for (value <- r.listString().asScala) yield {
          Integer.parseInt(value)
        }
        Some(items.toArray)
      } else None
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


case class List32Impl(dbname: String, client: SSDB)(implicit ec: ExecutionContext = ExecutionContext.global) extends List[Int] {
  override def pushFront(value: Int): Future[Boolean] = Future {
    client.qpush_front(dbname, value).ok()
  }

  override def pushBack(value: Int): Future[Boolean] = Future {
    client.qpush_back(dbname, value).ok()
  }

  override def size(): Future[Option[Int]] = Future {
    val r = client.qsize(dbname)
    r.getIntAsOption()
  }

  override def popFront(): Future[Option[Int]] = Future {
    val r = client.qpop_front(dbname)
    r.getIntAsOption()
  }

  override def popBack(): Future[Option[Int]] = Future {
    val r = client.qpop_back(dbname)
    r.getIntAsOption()
  }

  override def clear(): Future[Boolean] = Future {
    client.qclear(dbname).ok()
  }

  override def get(index: Int): Future[Option[Int]] = Future {
    val r = client.qget(dbname, index)
    r.getIntAsOption()
  }

  override def get(from: Int, num: Int): Future[Option[Array[Int]]] = Future {
    val r = client.qrange(dbname, from, num)
    r.getArrayIntAsOption()
  }

  override def set(index: Int, value: Int): Future[Boolean] = Future {
    client.qset(dbname, index, value).ok()
  }


  override def head(): Future[Option[Int]] = Future {
    val r = client.qget(dbname, 0)
    r.getIntAsOption()
  }

  override def last(): Future[Option[Int]] = Future {
    val r = client.qsize(dbname)
    if (r.ok())
      client.qget(dbname, r.asInt() - 1).getIntAsOption()
    else
      None
  }

  override def getAll(): Future[Option[Array[Int]]] = Future {
    val r = client.qsize(dbname)
    if (r.ok())
      client.qrange(dbname, 0, r.asInt()).getArrayIntAsOption()
    else
      None
  }
}


case class ListStringImpl(dbname: String, client: SSDB)(implicit ec: ExecutionContext = ExecutionContext.global) extends List[String] {
  override def pushFront(value: String): Future[Boolean] = Future {
    client.qpush_front(dbname, value).ok()
  }

  override def pushBack(value: String): Future[Boolean] = Future {
    client.qpush_back(dbname, value).ok()
  }

  override def size(): Future[Option[Int]] = Future {
    val r = client.qsize(dbname)
    r.getIntAsOption()
  }

  override def popFront(): Future[Option[String]] = {

  }

  override def popBack(): Future[Option[String]] = ???

  override def clear(): Future[Boolean] = ???

  override def get(index: Int): Future[Option[String]] = ???

  override def get(from: Int, num: Int): Future[Option[Array[String]]] = ???

  override def set(index: Int, value: String): Future[Boolean] = ???

  override def head(): Future[Option[String]] = ???

  override def last(): Future[Option[String]] = ???

  override def getAll(): Future[Option[Array[String]]] = ???
}

