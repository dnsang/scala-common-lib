import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.funsuite.AnyFunSuite
import xcommon.lib.{KVS, KVSDbImpl}

class SSDBImplTest extends AnyFunSuite {
  val ssdb: SSDB = SSDBs.pool(
    "localhost",
    8888,
    1000, null)

  val impl = KVSDbImpl("test", ssdb)

  test("test kvs with ssdb") {
    val kvsTestCase = new KVSTestCase {
      val ssdbClient: SSDB = ssdb
      override val kv: KVSDbImpl = impl
    }

    assert(ssdb.info().ok())

    val data = (for (i <- 0 to 100) yield (s"key$i", s"value$i")).toArray

    //Add Data
    assert(kvsTestCase.testAdd(data))
    kvsTestCase.assertSize(data.size)
    //Get Data
    assert(kvsTestCase.testGet(data))
    kvsTestCase.assertSize(data.size)
    //Delete Data
    assert(kvsTestCase.testDelete(data))
    kvsTestCase.assertSize(0)

    //MultiAdd Data
    assert(kvsTestCase.testMultiAdd(data))
    kvsTestCase.assertSize(data.size)
    //MultiGet Data
    assert(kvsTestCase.testMGet(data))
    kvsTestCase.assertSize(data.size)
    //MultiDelete Data
    assert(kvsTestCase.testMDelete(data))
    kvsTestCase.assertSize(0)

    //Assert Key Not Exist In KVS
    assert(kvsTestCase.testNotFound(data))

  }


}
