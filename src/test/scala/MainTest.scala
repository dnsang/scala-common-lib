import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.funsuite.AnyFunSuite
import xcommon.lib.{KVS, KVSDbImpl}

class KVSTestImpl extends AnyFunSuite {
  val ssdb: SSDB = SSDBs.pool(
    "localhost",
    8888,
    1000, null)

  test("test kvs with ssdb") {
    val kvsTestCase = new KVSTestCase {
      val ssdbClient: SSDB = ssdb
      override val kv: KVSDbImpl = KVSDbImpl("test", ssdbClient)

    }
//    kvsTestCase.testInsert()
    kvsTestCase.testRetrieve()
  }


}
