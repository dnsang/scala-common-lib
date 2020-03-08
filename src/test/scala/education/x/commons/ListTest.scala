package education.x.commons

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * @author tvc12 - thienvc
  * @since  05/03/2020
  */
class ListTest extends BaseSSDBTestCase {

  def getRandom(): Array[Int] = {
    val num = getRandomInt() + 1
    Range(0, num).map(_ => Random.nextInt()).toArray
  }

  def getRandomArrayString(): Array[String] = {
    val num = getRandomInt() + 1
    Range(0, num).map(_ => getRandomString()).toArray
  }

  def getRandomInt(max: Int = 1000): Int = {
    Random.nextInt(max)
  }

  def getRandomString(max: Int = 20): String = {
    Random.nextString(max)
  }

  test("Test List32Impl") {
    val list32 = List32Impl("list32", ssdb)
    var data = ListBuffer.empty[Int]
    val testCase = new ListTestCase[Int]() {
      override val client: List[Int] = list32
    }
    println("Prepare SSDB")
    assert(testCase.testClear())

    //Test push back
    var dataTest: Array[Int] = getRandom()
    println(s"Test push back ${dataTest.length} length")
    assert(testCase.testPushBack(dataTest))
    data.appendAll(dataTest)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test push front
    dataTest = getRandom()
    println(s"Test push front ${dataTest.length} length")
    assert(testCase.testPushFront(dataTest))
    data.insertAll(0, dataTest.reverse)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test pop front
    var num = getRandomInt(data.length)
    println(s"Test pop front ${num} item")
    assert(testCase.testPopFront(num))
    data = data.drop(num)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test pop back
    num = getRandomInt(data.length)
    println(s"Test pop back ${num} item")
    assert(testCase.testPopBack(num))
    data = data.dropRight(num)
    assert(testCase.testValueIsCorrect(data.toArray))

    // test get size
    println("Test get size")
    assert(testCase.testGetSize())

    // test get
    println("Test get")
    assert(testCase.testGet(0))

    //test set value
    val index = getRandomInt(data.length)
    val value = getRandomInt()
    println(s"Update at $index with $value")
    assert(testCase.testSet(index, value))
    data.update(index, value)
    assert(testCase.testValueIsCorrect(data.toArray))

    // test get head
    println("Test get front")
    val head = testCase.testGetFront()
    assert(head == data.head)

    // test get tail
    println("Test get back")
    val last = testCase.testGetBack()
    assert(last == data.last)

    // test get all
    println("Test get all")
    val all = testCase.testGetAll()
    assert(all.length == data.length && data.diff(all).isEmpty)
  }

  test("Test ListStringImpl") {
    val stringImpl = ListStringImpl("list_string_impl", ssdb)
    var data = ListBuffer.empty[String]
    val testCase = new ListTestCase[String]() {
      override val client: List[String] = stringImpl
    }
    println("Prepare SSDB")
    assert(testCase.testClear())

    //Test push back
    var dataTest: Array[String] = getRandomArrayString()
    println(s"Test push back ${dataTest.length} length")
    assert(testCase.testPushBack(dataTest))
    data.appendAll(dataTest)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test push front
    dataTest = getRandomArrayString()
    println(s"Test push front ${dataTest.length} length")
    assert(testCase.testPushFront(dataTest))
    data.insertAll(0, dataTest.reverse)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test pop front
    var num = getRandomInt(data.length)
    println(s"Test pop front ${num} item")
    assert(testCase.testPopFront(num))
    data = data.drop(num)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test pop back
    num = getRandomInt(data.length)
    println(s"Test pop back ${num} item")
    assert(testCase.testPopBack(num))
    data = data.dropRight(num)
    assert(testCase.testValueIsCorrect(data.toArray))

    // test get size
    println("Test get size")
    assert(testCase.testGetSize())

    // test get
    println("Test get")
    assert(testCase.testGet(0))

    //test set value
    val index = getRandomInt(data.length)
    val value = getRandomString()
    println(s"Update at $index with $value")
    assert(testCase.testSet(index, value))
    data.update(index, value)
    assert(testCase.testValueIsCorrect(data.toArray))

    // test get head
    println("Test get front")
    val head = testCase.testGetFront()
    assert(head == data.head)

    // test get tail
    println("Test get back")
    val last = testCase.testGetBack()
    assert(last == data.last)

    // test get all
    println("Test get all")
    val all = testCase.testGetAll()
    assert(all.length == data.length && data.diff(all).isEmpty)
  }
}