package education.x.ultis

import org.nutz.ssdb4j.impl.DefaultObjectConv
import org.nutz.ssdb4j.spi.{Cmd, ObjectConv, Response, SSDB}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

/**
  * @author tvc12 - thienvc
  * @since  05/03/2020
  */
object Implicits {
  implicit class ImplicitSSBDResponse(r: Response) {
    def getIntAsOption(): Option[Int] = {
      if (r.ok())
        Some(r.asInt())
      else
        None
    }

    def getArrayIntAsOption(): Option[Array[Int]] = {
      if (r.ok()) {
        val items = for (value <- r.listString().asScala) yield {
          Integer.parseInt(value)
        }
        Some(items.toArray)
      } else None
    }

    def getStringAsOption(): Option[String] = {
      if (r.ok())
        Some(r.asString())
      else
        None
    }

    def getArrayStringAsOption(): Option[Array[String]] = {
      if (r.ok())
        Some(r.listString().asScala.toArray)
      else
        None
    }
  }

  implicit class ImplicitSSDB(client: SSDB) {
    private val cmdSet = new Cmd("qset", false, true)
    private val converter: ObjectConv = new DefaultObjectConv()

    private def bytes(obj: Any): Array[Byte] = converter.bytes(obj)

    def qset(key: Any, index: Int, value: Any): Response = {
      val keyAsBytes = bytes(key)
      val indexAsBytes = String.valueOf(index).getBytes()
      val valueAsBytes = bytes(value)
      client.req(cmdSet, keyAsBytes, indexAsBytes, valueAsBytes)
    }
  }
}
