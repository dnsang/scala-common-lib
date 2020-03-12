package education.x.commons

import scala.concurrent.Future

/**
  * @author tvc12 - thienvc
  * @since 09/03/2020
  */
trait List[T] {

  def pushFront(value: T): Future[Boolean]

  def pushBack(value: T): Future[Boolean]

  def multiPushFront(values: Array[T]): Future[Boolean]

  def multiPushBack(values: Array[T]): Future[Boolean]

  def size(): Future[Option[Int]]

  def popFront(): Future[Option[T]]

  def popBack(): Future[Option[T]]

  def multiPopFront(size: Int): Future[Option[Array[T]]]

  def multiPopBack(size: Int): Future[Option[Array[T]]]

  def clear(): Future[Boolean]

  def get(index: Int): Future[Option[T]]

  def getFront: Future[Option[T]]

  def getBack: Future[Option[T]]

  def get(from: Int, num: Int): Future[Option[Array[T]]]

  def set(index: Int, value: T): Future[Boolean]

  def getAll: Future[Option[Array[T]]]

}
