package education.x.commons

import education.x.commons.list.SSDBList

class SSDBListTest extends BaseSSDBTestCase {


  test("test constructor") {

    import education.x.commons.list.SSDBList.I32Serializer
    val list = SSDBList[Int]("test")

  }

}
