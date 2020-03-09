package education.x.commons

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.util.Random

/**
  * @author tvc12 - thienvc
  * @since  05/03/2020
  */
class ListTest extends BaseSSDBTestCase {

  def getArrays[T: ClassTag](randomItem: () => T): Array[T] = {
    val num = getRandomInt() + 1
    Range(0, num).map(_ => randomItem()).toArray
  }

  def getRandomInt(size: Int): Int = {
    Random.nextInt(size)
  }

  def getRandomInt(): Int = {
    Random.nextInt(5000)
  }

  def getRandomString(): String = {
    Random.nextString(50)
  }

  test("Test List32Impl") {
    val list32 = List32Impl("list32", ssdb)
    runTestCase[Int](list32, getRandomInt)
  }

  test("Test List64Impl") {
    val list64 = List64Impl("list64", ssdb)
    runTestCase[Long](list64, () => Random.nextLong())
  }

  test("Test ListStringImpl") {
    val stringImpl = ListStringImpl("list_string_impl", ssdb)
    runTestCase[String](stringImpl, getRandomString)
  }


  def runTestCase[T: ClassTag](listImpl: List[T], getDataTest: () => T): Unit = {
    var data = ListBuffer.empty[T]
    val testCase = new ListTestCase[T]() {
      override val client: List[T] = listImpl
    }

    println("Prepare SSDB")
    assert(testCase.testClear())

    //Test push back
    var dataTest: Array[T] = getArrays(getDataTest)
    println(s"Test push back ${dataTest.length} length")
    assert(testCase.testPushBack(dataTest))
    data.appendAll(dataTest)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test push front
    dataTest = getArrays(getDataTest)
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

    // test multi push front
    dataTest = getArrays(getDataTest)
    println(s"Test multi push front ${dataTest.length} length")
    assert(testCase.testMultiPushFront(dataTest))
    data.insertAll(0, dataTest.reverse)
    assert(testCase.testValueIsCorrect(data.toArray))

    // test multi push back
    dataTest = getArrays(getDataTest)
    println(s"Test multi push back ${dataTest.length} length")
    assert(testCase.testMultiPushBack(dataTest))
    data.appendAll(dataTest)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test multi pop front
    num = getRandomInt(data.length)
    println(s"Test pop front ${num} item")
    assert(testCase.testMultiPopFront(num))
    data = data.drop(num)
    assert(testCase.testValueIsCorrect(data.toArray))

    // Test multi pop back
    num = getRandomInt(data.length)
    println(s"Test pop back ${num} item")
    assert(testCase.testMultiPopBack(num))
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
    val value = getDataTest()
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

    // clear data
    println("Clear data")
    assert(testCase.testClear())

    println("Done!")
  }
}
