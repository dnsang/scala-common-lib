package education.x.commons


import scala.collection.immutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{Millis, Seconds, Span}

class SortedSetTest extends BaseSSDBTestCase {

  val sortedSet = SsdbSortedSet("test_sortedset", ssdb)

  test("crud sortedset") {

    println(("Clear Data"))
    whenReady(sortedSet.clear())(r => assert(r))

    val nRecords = 100

    println(s"Add $nRecords toByte sortedset")

    val data: immutable.Seq[(String, Long)] = for (i <- 1 to nRecords) yield (s"user$i", i.toLong)

    val fAddRecords = for (record <- data) yield
      sortedSet.add(record._1, record._2)

    val addRecordResults: Future[immutable.Seq[Boolean]] = Future.sequence(fAddRecords)

    val result: Int = Await.result(addRecordResults, Duration.Inf).foldLeft(0)((sum, bool) => if (bool) sum + 1 else sum)

    println(s"Total Added Records: $result")

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
          assert(r.get == nRecords - record._2)
        }
      }

    println(s"Check ranking reverseOrder")

    for (record <- data)
      whenReady(sortedSet.rank(record._1, reverseOrder = true)) {
        r => {
          assert(r.isDefined)
          assert(r.get == record._2 - 1)
        }
      }

    println("Get Top 10 ")

    whenReady(sortedSet.range(0, 10)) {
      r => {
        var lastId = nRecords
        r.get.foreach(f => {
          println(f)
          assert(f._2 == lastId)
          lastId = lastId - 1
        })
      }
    }

    println("Get Top 10 ReverseOrder ")

    whenReady(sortedSet.range(0, 10, reverseOrder = true)) {
      r => {
        var lastId = 1
        r.get.foreach(f => {
          println(f)
          assert(f._2 == lastId)
          lastId = lastId + 1
        })
      }

    }

    println("Test Remove First Record")
    whenReady(sortedSet.remove(data.head._1)) {
      r => assert(r)
    }
    whenReady(sortedSet.get(data.head._1)) {
      r => assert(r.isEmpty)
    }


  }


  test("Test Multi Function On SortedSet") {

    println("Clear Data")
    whenReady(sortedSet.clear())(r => assert(r))

    val nRecords = 100

    println(s"MAdd $nRecords toByte Set")

    val data: Array[(String, Long)] = (for (i <- 1 to nRecords) yield (s"muser$i", i.toLong)).toArray

    whenReady(sortedSet.madd(data)) {
      r => assert(r)
    }


    println(s"MGet Data ")

    whenReady(sortedSet.mget(data.map(f => f._1))) {
      r => {
        assert(r.isDefined)
        val result = r.get
        data.foreach(record => {
          assert(result.contains(record._1))
          assert(result(record._1) == record._2)
        })
      }
    }
    println(s"Check ranking")
    for (record <- data)
      whenReady(sortedSet.rank(record._1)) {
        r => {
          assert(r.isDefined)
          assert(r.get == nRecords - record._2)
          //          println(s"Normal Order: ${record._1},${record._2} rank=${r.get}")
        }
      }

    println(s"Check ranking reverseOrder")

    for (record <- data)
      whenReady(sortedSet.rank(record._1, reverseOrder = true)) {
        r => {
          assert(r.isDefined)
          assert(r.get == record._2 - 1)
          //          println(s"Reverse Order: ${record._1},${record._2} rank=${r.get}")
        }
      }

    println("Get Top 10 ")

    whenReady(sortedSet.range(0, 10)) {
      r => {
        var lastId = nRecords
        r.get.foreach(f => {
          println(f)
          assert(f._2 == lastId)
          lastId = lastId - 1
        })
      }
    }

    println("Get Top 10 ReverseOrder ")

    whenReady(sortedSet.range(0, 10, reverseOrder = true)) {
      r => {
        var lastId = 1
        r.get.foreach(f => {
          println(f)
          assert(f._2 == lastId)
          lastId = lastId + 1
        })
      }
    }


    println("Test MRemove 10 Record")
    val top10 = data.slice(0, 10).map(f => f._1)

    whenReady(sortedSet.mremove(top10)) {
      r => assert(r)
    }
    whenReady(sortedSet.mget(top10)) {
      r => assert(r.get.isEmpty)
    }
  }
}
