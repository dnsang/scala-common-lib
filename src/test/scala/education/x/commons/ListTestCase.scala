package education.x.commons

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.control.Breaks

/**
  * @author tvc12 - thienvc
  * @since  05/03/2020
  */
trait ListTestCase[T] {
  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val duration: Duration = Duration.Inf
  val client: List[T]

  def testPushFront(values: Array[T]): Boolean = {
    val r = for (value <- values) yield {
      val fn = client.pushFront(value)
      fn.onComplete(r => {
        assert(r.isSuccess)
      })
      Await.result(fn, duration)
    }
    val hasFailure = r.contains(false)
    !hasFailure
  }

  def testPushBack(values: Array[T]): Boolean = {
    val r = for (value <- values) yield {
      val fn = client.pushBack(value)
      fn.onComplete(r => {
        assert(r.isSuccess)
      })
      Await.result(fn, duration)
    }
    val hasFailure = r.contains(false)
    !hasFailure
  }

  def testMultiPushFront(values: Array[T]): Boolean = {
    val r = client.multiPushFront(values)
    Await.result(r, duration)
  }

  def testMultiPushBack(values: Array[T]): Boolean = {
    val r = client.multiPushBack(values)
    Await.result(r, duration)
  }

  def testPopFront(num: Int): Boolean = {
    val r = for (times <- 0 until num) yield {
        val fn = client.popFront()
        Await.result(fn, duration).isDefined
    }
    val hasError = r.contains(false)
    !hasError
  }

  def testMultiPopFront(num: Int): Boolean = {
    val r = client.multiPopFront(num)
    Await.result(r, duration).isDefined
  }

  def testPopBack(num: Int): Boolean = {
    val r = for (times <- 0 until num) yield {
      val fn = client.popBack()
      Await.result(fn, duration).isDefined
    }
    val hasError = r.contains(false)
    !hasError
  }

  def testMultiPopBack(num: Int): Boolean = {
    val r = client.multiPopBack(num)
    Await.result(r, duration).isDefined
  }

  def testGetSize(): Boolean = {
    val r = Await.result(client.size(), duration)
    assert(r.isDefined)
    r.isDefined
  }

  def testGet(index: Int): Boolean = {
    val fn = client.get(index)
    val r = Await.result(fn, duration)
    assert(r.isDefined)
    r.isDefined
  }

  def testGetAsArray(from: Int, num: Int): Boolean = {
    val fn = client.get(from, num)
    val r = Await.result(fn, duration)
    assert(r.isDefined)
    r.isDefined
  }

  def testSet(index: Int, value: T): Boolean = {
    val fn = client.set(index, value)
    val r = Await.result(fn, duration)
    r
  }

  def testGetFront() : T = {
    val fn = client.getFront
    val r = Await.result(fn, duration)
    assert(r.isDefined)
    r.get
  }

  def testGetBack(): T = {
    val fn = client.getBack
    val r = Await.result(fn, duration)
    assert(r.isDefined)
    r.get
  }

  def testGetAll(): Array[T] = {
    val fn = client.getAll
    val r = Await.result(fn, duration)
    assert(r.isDefined)
    r.get
  }

  def testValueIsCorrect(values: Array[T]): Boolean = {
    val r = Await.result(client.get(0, values.length), duration)
    assert(r.isDefined)
    val arrays = r.get
    if (arrays.length != values.length)
      false
    else {
      var result = true
      for (index <- arrays.indices) {
        if (!arrays(index).equals(values(index))){
          result = false
        }
      }
      result
    }
  }

  def testClear(): Boolean = {
    val fn = client.clear()
    val r = Await.result(fn, duration)
    r
  }
}
