package education.x.commons.list

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB

import scala.reflect.ClassTag

/**
  * @author tvc12 - thienvc
  * @since  09/03/2020
  */
case class ListDbFloat(dbname: String, client: SSDB)(
  implicit ev: ClassTag[Float]
) extends ListDbImpl[Float](dbname, client)(
      ev,
      value => value.toString.getBytes(),
      bytes => new String(bytes).toFloat
    )

object ListDbFloat {
  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit ev: ClassTag[Float]
  ): ListDbFloat = {
    val client = SSDBs.pool(host, port, timeout, null)
    new ListDbFloat(dbname, client)(ev)
  }
}
