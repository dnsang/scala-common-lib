package xcommon.lib

import scala.concurrent.Future

trait List[T] {

  def pushFront(value: T): Future[Boolean]

  def pushBack(value: T): Future[Boolean]

  def size(): Future[Option[Int]]

  def popFront(): Future[Option[T]]

  def popBack(): Future[Option[T]]

  def clear(): Future[Boolean]

  def get(index: Int): Future[Option[T]]

  def get(from: Int, num: Int): Future[Option[Array[T]]]

  def set(index: Int, value: T): Future[Boolean]
}

class List32Impl extends List[Int] {

  override def pushFront(value: Int): Future[Boolean] = ???

  override def pushBack(value: Int): Future[Boolean] = ???

  override def size(): Future[Option[Int]] = ???

  override def popFront(): Future[Option[Int]] = ???

  override def popBack(): Future[Option[Int]] = ???

  override def clear(): Future[Boolean] = ???

  override def get(index: Int): Future[Option[Int]] = ???

  override def get(from: Int, num: Int): Future[Option[Array[Int]]] = ???

  override def set(index: Int, value: Int): Future[Boolean] = ???
}


class ListStringImpl extends List[String] {
  override def pushFront(value: String): Future[Boolean] = ???

  override def pushBack(value: String): Future[Boolean] = ???

  override def size(): Future[Option[Int]] = ???

  override def popFront(): Future[Option[String]] = ???

  override def popBack(): Future[Option[String]] = ???

  override def clear(): Future[Boolean] = ???

  override def get(index: Int): Future[Option[String]] = ???

  override def get(from: Int, num: Int): Future[Option[Array[String]]] = ???

  override def set(index: Int, value: String): Future[Boolean] = ???
}

