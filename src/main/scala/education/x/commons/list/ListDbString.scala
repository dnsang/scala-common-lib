package education.x.commons.list

import java.nio.charset.Charset

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB

import scala.reflect.ClassTag

/**
  * @author tvc12 - thienvc
  * @since  09/03/2020
  */
case class ListDbString(dbname: String, client: SSDB)(
  implicit ev: ClassTag[String]
) extends ListDbImpl[String](dbname, client)(
      ev,
      value => value.toString.getBytes(ListDbString.charset),
      bytes => new String(bytes, ListDbString.charset)
    )

object ListDbString {
    private val charset: Charset = SSDBs.DEFAULT_CHARSET

    def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit ev: ClassTag[String]
  ): ListDbString = {
    val client = SSDBs.pool(host, port, timeout, null)
    new ListDbString(dbname, client)(ev)
  }
}
