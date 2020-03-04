package education.x.commons


import scala.collection.immutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import org.scalatest.concurrent.ScalaFutures._

class SortedSetTest extends BaseSSDBTestCase {


  implicit val ec: ExecutionContext = ExecutionContext.global

  val sortedSet = SsdbSortedSet("test", ssdb)
  test("crud sortedset") {

    val nRecords = 100

    println(s"Add $nRecords to sortedset")

    val data: immutable.Seq[(String, Long)] = for (i <- 1 to nRecords) yield (s"user$i", i.toLong)

    val fAddRecords = for (record <- data) yield
      sortedSet.add(record._1, record._2)

    val addRecordResults: Future[immutable.Seq[Boolean]] = Future.sequence(fAddRecords)

    val result: Int = Await.result(addRecordResults, Duration.Inf).foldLeft(0)((sum, bool) => if (bool) sum + 1 else sum)

    println("Total Added Records: $result")

    assert(result == nRecords)

    println(s"Get Data ")
    for (record <- data)
      whenReady(sortedSet.get(record._1)) {
        r => {

          assert(r.isDefined)
          assert(r.get == record._2)
        }
      }
    println(s"Check ranking")
    for (record <- data)
      whenReady(sortedSet.rank(record._1, reverseOrder = false)) {
        r => {
          assert(r.isDefined)
          assert(r.get == record._2)
        }
      }

  }
}
