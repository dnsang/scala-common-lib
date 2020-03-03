object TestSyntax {


  def main(args: Array[String]): Unit = {


    val data = Array(Array(1, 2), Array(3, 4))

    val flatten = data.flatten
    flatten.foreach(print(_))


    val data2 = Array((1, 2), (3, 4))

    val flatten2 = data2.flatMap(f => Array(f._1,f._2))
    flatten2.foreach(print(_))

  }

}