object TestSyntax {


  def main(args: Array[String]): Unit = {

    val data = Array("key1", "value1", "key2", "value2")

    val groupData = data.grouped(2).map(f => f(0) -> f(1))
    println(groupData.toMap)

  }

}