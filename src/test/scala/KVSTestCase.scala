import org.scalatest.Matchers
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}
import org.scalatest.time.Millisecond
import xcommon.lib.KVS

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

trait KVSTestCase {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val kv: KVS[String, String]

  def testInsert(): Boolean = {
    var result = true
    for (i <- 0 to 100) {
      val resp = kv.add(s"test$i", s"value$i")
      resp.onComplete(f => {
        result = result & f.isSuccess
        println(s"Insert test$i status: ${f.isSuccess}")
        assert(f.isSuccess)

      })
      Await.result(resp, 1000 millis)
    }

    result
  }

  def testRetrieve(): Unit = {

    for (i <- 0 to 100) {
      val resp = kv.get(s"test$i")
      resp.onComplete(f => {
        assert(f.get.isDefined)
        println(f.get.get.toString)
        assert(f.get.get.equals(s"value$i"))
      })
      Await.result(resp, 1000 millis)
    }
  }
}

