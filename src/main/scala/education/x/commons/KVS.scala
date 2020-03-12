package education.x.commons

import scala.concurrent.Future


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

