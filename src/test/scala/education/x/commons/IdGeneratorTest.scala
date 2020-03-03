package education.x.commons

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class IdGeneratorTest extends AnyFunSuite {
  implicit val ec = ExecutionContext.global
  val ssdb: SSDB = SSDBs.pool(
    "localhost",
    8888,
    5000, null)

  assert(ssdb.info().ok())


  val idgen = I32IdGenerator("project_test", "user_id", ssdb, 1)
  idgen.reset()

  test("generate Unique ID") {

    val nRequiredId = 1000
    val generateIds = for (i <- 1 to nRequiredId) yield idgen.getNextId()

    val ids: Future[immutable.IndexedSeq[Option[Int]]] = Future.sequence(generateIds)

    val result: immutable.Seq[Option[Int]] = Await.result(ids, Duration.Inf)

    assert(result.size == nRequiredId)
    val idSet = new scala.collection.mutable.HashSet[Int]()
    for (elem <- result) {
      assert(elem.isDefined)
      assert(idSet.add(elem.get))
    }
    assert(idSet.size == nRequiredId)
    println("Total Id Gen: " + idSet.size)

  }
}
