package education.x.commons

import scala.concurrent.Future

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
