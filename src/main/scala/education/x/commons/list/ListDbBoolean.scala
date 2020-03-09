package education.x.commons.list

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB

import scala.reflect.ClassTag

/**
  * @author tvc12 - thienvc
  * @since  09/03/2020
  */
case class ListDbBoolean(dbname: String, client: SSDB)(
  implicit ev: ClassTag[Boolean]
) extends ListDbImpl[Boolean](dbname, client)(
      ev,
      value => value.toString.getBytes(),
      bytes => new String(bytes).toBoolean
    )

object ListDbBoolean {
  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit ev: ClassTag[Boolean]
  ): ListDbBoolean = {
    val client = SSDBs.pool(host, port, timeout, null)
    new ListDbBoolean(dbname, client)(ev)
  }
}
