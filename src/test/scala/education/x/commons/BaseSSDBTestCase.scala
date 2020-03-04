package education.x.commons

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.funsuite.AnyFunSuite

abstract class BaseSSDBTestCase extends AnyFunSuite {

  val ssdb: SSDB = SSDBs.pool(
    "localhost",
    8888,
    5000, null)

  val impl = KVSDbImpl("test", ssdb)
  assert(ssdb.info().ok())

}
