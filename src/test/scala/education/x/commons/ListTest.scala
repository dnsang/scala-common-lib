package education.x.commons

import education.x.commons.list._

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.util.Random

/**
  * @author tvc12 - thienvc
  * @since 05/03/2020
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

  test("constructor") {

    val xlist = new ListDbImpl[Int]("test", ssdb)
    xlist.pushFront(1)


  }

  test("Test List Boolean") {
    val listBool = ListDbBoolean("list_bool", ssdb)
    runTestCase[Boolean](listBool, () => Random.nextBoolean())
  }

  test("Test List16") {
    val list16 = ListDb16("list16", ssdb)
    runTestCase[Short](list16, () => Random.nextInt().toShort)
  }

  test("Test List32") {
    val list32 = ListDb32("list32", ssdb)
    runTestCase[Int](list32, getRandomInt)
  }

  test("Test List64") {
    val list64 = ListDb64("list64", ssdb)
    runTestCase[Long](list64, () => Random.nextLong())
  }

  test("Test List Float") {
    val listDbFloat = ListDbFloat("list_float", ssdb)
    runTestCase[Float](listDbFloat, () => Random.nextFloat())
  }

  test("Test List Double") {
    val listDbDouble = ListDbDouble("list_long", ssdb)
    runTestCase[Double](listDbDouble, () => Random.nextDouble())
  }

  test("Test ListStringImpl") {
    val stringImpl = ListDbString("list_string_impl", ssdb)
    runTestCase[String](stringImpl, getRandomString)
  }


  def runTestCase[T: ClassTag](listImpl: list.List[T], getDataTest: () => T): Unit = {
    var data = ListBuffer.empty[T]
    val testCase = new ListTestCase[T]() {
      override val client: list.List[T] = listImpl
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
