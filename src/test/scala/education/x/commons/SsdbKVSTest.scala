package education.x.commons

import java.util.concurrent.TimeUnit

import org.nutz.ssdb4j.spi.SSDB

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class SsdbKVSTest extends BaseSSDBTestCase {

  val impl = SsdbKVS[String, String]("test", ssdb)

  val kvsTestCase: KVSTestCase = new KVSTestCase {
    val ssdbClient: SSDB = ssdb
    override val kv: SsdbKVS[String, String] = impl
  }
  val data: Array[(String, String)] = (for (i <- 0 to 100) yield (s"key$i", s"value$i")).toArray

  test("test single action with ssdb") {

    Await.result(impl.clear(), Duration(1000, TimeUnit.MILLISECONDS))

    println(s"Test Add ${data.length} records")
    assert(kvsTestCase.testAdd(data))
    println(s"Assert Size: ${data.length} ")
    kvsTestCase.assertSize(data.length)
    //Get Data
    println(s"Assert Get Data")
    assert(kvsTestCase.testGet(data))
    kvsTestCase.assertSize(data.length)
    //Delete Data
    println(s"Assert Delete Data")
    assert(kvsTestCase.testDelete(data))
    kvsTestCase.assertSize(0)

  }

  test("test multi action with ssdb") {

    Await.result(impl.clear(), Duration(1000, TimeUnit.MILLISECONDS))

    println(s"Test M-Add ${data.length} records")
    assert(kvsTestCase.testMultiAdd(data))
    kvsTestCase.assertSize(data.length)

    println(s"Assert M-Get Data")
    kvsTestCase.testMGet(data)
    kvsTestCase.assertSize(data.length)
    //MultiDelete Data
    println(s"Assert M-Del Data")
    assert(kvsTestCase.testMDelete(data))
    kvsTestCase.assertSize(0)

    //Assert Key Not Exist In KVS
    println(s"Assert Key Not Found After Clear")
    assert(kvsTestCase.testNotFound(data))
  }


}
