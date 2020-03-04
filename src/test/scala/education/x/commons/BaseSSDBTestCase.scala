package education.x.commons

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.funsuite.AnyFunSuite

import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{Millis, Seconds, Span}
abstract class BaseSSDBTestCase extends AnyFunSuite {

  implicit val patienceConfig =
    PatienceConfig(timeout = scaled(Span(2, Seconds)), interval = scaled(Span(5, Millis)))

  val ssdb: SSDB = SSDBs.pool(
    "localhost",
    8888,
    5000, null)

  val impl = KVSDbImpl("test", ssdb)
  assert(ssdb.info().ok())

}
