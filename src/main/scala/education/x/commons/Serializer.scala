package education.x.commons

trait Serializer[T] {

  def fromByte(bytes: Array[Byte]): T

  def toByte(value: T): Array[Byte]


}

object Serializer {

  implicit object ShortSerializer extends Serializer[Short] {

    override def fromByte(bytes: Array[Byte]): Short = new String(bytes).toShort

    override def toByte(value: Short): Array[Byte] = value.toString.getBytes()
  }

  implicit object IntSerializer extends Serializer[Int] {

    override def fromByte(bytes: Array[Byte]): Int = new String(bytes).toInt

    override def toByte(value: Int): Array[Byte] = value.toString.getBytes()
  }

  implicit object LongSerializer extends Serializer[Long] {

    override def fromByte(bytes: Array[Byte]): Long = new String(bytes).toLong

    override def toByte(value: Long): Array[Byte] = value.toString.getBytes()
  }

  implicit object DoubleSerializer extends Serializer[Double] {

    override def fromByte(bytes: Array[Byte]): Double = new String(bytes).toDouble

    override def toByte(value: Double): Array[Byte] = value.toString.getBytes()
  }

  implicit object StringSerializer extends Serializer[String] {

    override def fromByte(bytes: Array[Byte]): String = new String(bytes)

    override def toByte(value: String): Array[Byte] = value.getBytes()
  }

  implicit object BoolSerializer extends Serializer[Boolean] {

    override def fromByte(bytes: Array[Byte]): Boolean = new String(bytes).toBoolean

    override def toByte(value: Boolean): Array[Byte] = value.toString.getBytes()
  }

  implicit object FloatSerializer extends Serializer[Float] {

    override def fromByte(bytes: Array[Byte]): Float = new String(bytes).toFloat

    override def toByte(value: Float): Array[Byte] = value.toString.getBytes()
  }

}
