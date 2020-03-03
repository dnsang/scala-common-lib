package education.x.common

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

trait KVSTestCase {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val kv: KVS[String, String]
  val timeout = Duration.Inf

  def testAdd(data: Array[(String, String)]): Boolean = {
    var result = true
    for (pair <- data) {
      val resp = kv.add(pair._1, pair._2)
      resp.onComplete(f => {
        result = result & f.isSuccess
        assert(f.isSuccess)

      })
      Await.result(resp, timeout)
    }
    assert(result)
    result
  }

  def testMultiAdd(data: Array[(String, String)]): Boolean = {

    val resp = kv.madd(data)
    resp.onComplete(f => {
      assert(f.isSuccess)
    })
    Await.result(resp, timeout)
    resp.value.get.get
  }

  def testGet(data: Array[(String, String)]): Boolean = {

    var result = true
    for (pair <- data) {
      val resp = kv.get(pair._1)
      resp.onComplete(f => {
        result &= f.isSuccess
        assert(f.get.get.equals(pair._2))
      })
      Await.result(resp, timeout)
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
      Await.result(resp, timeout)
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
    Await.result(resp, timeout)
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
      Await.result(resp, timeout)
    }
    result
  }

  def testMGet(data: Array[(String, String)]): Boolean = {
    var result = false

    val keys = data.map(f => f._1)
    val resp = kv.mget(keys)
    resp.onComplete(f => {
      result |= f.isSuccess
      val retrieveData = f.get.get
      for (kv <- data) {
        result &= retrieveData.contains(kv._1)
        result &= retrieveData(kv._1).equals(kv._2)
      }
    })

    Await.result(resp, timeout)

    result
  }

  def assertSize(size: Int): Boolean = {
    var result = false
    val resp = kv.size()
    resp.onComplete(f => {
      assert(f.get.isDefined)
      val retrieveSize = f.get.get
      println(s"assertSize retrieveSize=$retrieveSize")
      result = retrieveSize == size
      assert(retrieveSize == size)
    })

    Await.result(resp, timeout)
    result
  }


}
