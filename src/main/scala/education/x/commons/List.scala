package education.x.commons

import education.x.ultis.Implicits._
import org.nutz.ssdb4j.spi.SSDB

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait List[T] {

  def pushFront(value: T): Future[Boolean]

  def pushBack(value: T): Future[Boolean]

  def size(): Future[Option[Int]]

  def popFront(): Future[Option[T]]

  def popBack(): Future[Option[T]]

  def clear(): Future[Boolean]

  def get(index: Int): Future[Option[T]]

  def getFront(): Future[Option[T]]

  def getBack(): Future[Option[T]]

  def get(from: Int, num: Int): Future[Option[Array[T]]]

  def set(index: Int, value: T): Future[Boolean]

  def getAll(): Future[Option[Array[T]]]
}

case class List32Impl(dbname: String, client: SSDB)(implicit ev: ClassTag[Int]) extends ListImpl[Int](dbname, client)(ev, value => value.toString.getBytes(), bytes => Integer.parseInt(new String(bytes)))

case class ListStringImpl(dbname: String, client: SSDB)(implicit ev: ClassTag[String]) extends ListImpl[String](dbname, client)(ev, value => value.toString.getBytes(), bytes => new String(bytes))