import org.scalatest.Matchers
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}
import org.scalatest.time.Millisecond
import xcommon.lib.KVS

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

trait KVSTestCase {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val kv: KVS[String, String]

  def testAdd(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.add(pair._1, pair._2)
      resp.onComplete(f => {
        result = result & f.isSuccess
        assert(f.isSuccess)

      })
      Await.result(resp, 1000 millis)
    }
    assert(result)
    result
  }

  def testMultiAdd(data: Array[(String, String)]): Boolean = {
    var result = true

    val resp = kv.madd(data)
    resp.onComplete(f => {
      result = result & f.isSuccess
      assert(f.isSuccess)
    })
    Await.result(resp, 1000 millis)

    assert(result)
    result
  }

  def testGet(data: Array[(String, String)]): Boolean = {

    var result = true
    for (pair <- data) {
      val resp = kv.get(pair._1)
      resp.onComplete(f => {
        result &= f.isSuccess
        assert(f.get.get.equals(pair._2))
      })
      Await.result(resp, 1000 millis)
    }
    result
  }

  def testDelete(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.remove(pair._1)
      resp.onComplete(f => {

        result &= f.isSuccess
        result &= f.get
      })
      Await.result(resp, 1000 millis)
    }
    result
  }

  def testMDelete(data: Array[(String, String)]): Boolean = {
    var result = true

    val resp = kv.mremove(data.map(item => item._1))
    resp.onComplete(f => {
      result &= f.isSuccess
      result &= f.get
    })
    Await.result(resp, 1000 millis)
    result
  }


  def testNotFound(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.get(pair._1)
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
    val resp = kv.mget(keys)
    resp.onComplete(f => {
      result &= f.isSuccess

      val retrieveData = f.get.get
      for (kv <- data) {
        result &= retrieveData.contains(kv._1)
        result &= retrieveData.get(kv._1).equals(kv._2)
      }
    })

    Await.result(resp, 1000 millis)

    result
  }

  def assertSize(size: Int): Boolean = {
    var result = false
    val resp = kv.size()
    resp.onComplete(f => {
      assert(f.get != None)
      val retrieveSize = f.get.get
      result = true
      assert(retrieveSize == size)
      result &= (retrieveSize == size)
      println(s"assertSize retrieveSize=$retrieveSize")
    })

    Await.result(resp, 1000 millis)
    result
  }


}

