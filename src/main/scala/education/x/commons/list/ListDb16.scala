package education.x.commons.list

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB

import scala.reflect.ClassTag

/**
  * @author tvc12 - thienvc
  * @since  09/03/2020
  */
case class ListDb16(dbname: String, client: SSDB)(implicit ev: ClassTag[Short])
    extends ListDbImpl[Short](dbname, client)(
      ev,
      value => value.toString.getBytes(),
      bytes => new String(bytes).toShort
    )

object ListDb16 {
  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit ev: ClassTag[Short]
  ): ListDb16 = {
    val client = SSDBs.pool(host, port, timeout, null)
    new ListDb16(dbname, client)(ev)
  }
}
