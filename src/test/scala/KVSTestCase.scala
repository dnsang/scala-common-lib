import org.scalatest.Matchers
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}
import org.scalatest.time.Millisecond
import xcommon.lib.KVS

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

trait KVSTestCase {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val kv: KVS[String, String]

  def testInsert(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.add(pair(0), pair(1))
      resp.onComplete(f => {
        result = result & f.isSuccess
        assert(f.isSuccess)

      })
      Await.result(resp, 1000 millis)
    }
    assert(result)
    result
  }

  def testRetrieve(data: Array[(String, String)]): Boolean = {

    var result = true
    for (pair <- data) {
      val resp = kv.get(pair(0))
      resp.onComplete(f => {
        result &= f.isSuccess
        assert(f.get.get.equals(pair(1)))
      })
      Await.result(resp, 1000 millis)
    }
    result
  }

  def testDelete(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.remove(pair(0))
      resp.onComplete(f => {

        result &= f.isSuccess
        result &= f.get
      })
      Await.result(resp, 1000 millis)
    }
    result
  }


  def testNotFound(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.get(pair(0))
      resp.onComplete(f => {
        result &= f.isSuccess
        result &= f.get == None
      })
      Await.result(resp, 1000 millis)
    }
    result
  }

  def testMGet(data: Array[(String, String)]): Boolean = {
    var result = true

    val keys = data.map(f => f._1)
    kv.mget(keys).onComplete(f => {

      result &= f.isSuccess

      val retrieveData = f.get.get
      for (kv <- data) {
        result &= retrieveData.contains(kv(0))
        result &= retrieveData.get(kv(0)).equals(kv(1))
      }
    })

    result
  }
}

