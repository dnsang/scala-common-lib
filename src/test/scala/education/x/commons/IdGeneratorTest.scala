package education.x.commons

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class IdGeneratorTest extends BaseSSDBTestCase {

  test("generate Unique ID") {

    val idGenerator = I32IdGenerator("project_test", "user_id", ssdb, 1)
    idGenerator.reset()

    val nRequiredId = 1000
    val generateIds = for (i <- 1 to nRequiredId) yield idGenerator.getNextId()

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
