package education.x.commons.list

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB

import scala.reflect.ClassTag

/**
  * @author tvc12 - thienvc
  * @since  09/03/2020
  */
case class ListDb32(dbname: String, client: SSDB)(implicit ev: ClassTag[Int])
    extends ListDbImpl[Int](dbname, client)(
      ev,
      value => value.toString.getBytes(),
      bytes => new String(bytes).toInt
    )

object ListDb32 {
  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit ev: ClassTag[Int]
  ): ListDb32 = {
    val client = SSDBs.pool(host, port, timeout, null)
    new ListDb32(dbname, client)(ev)
  }
}
