package education.x.commons

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

abstract class BaseSSDBTestCase extends AnyFunSuite {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val patienceConfig: ScalaFutures.PatienceConfig =
    PatienceConfig(timeout = scaled(Span(2, Seconds)), interval = scaled(Span(5, Millis)))

  val ssdb: SSDB = SSDBs.pool(
    "localhost",
    8888,
    5000, null)

  assert(ssdb.info().ok())

}
