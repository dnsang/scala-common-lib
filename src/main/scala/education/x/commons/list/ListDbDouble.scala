package education.x.commons.list

import org.nutz.ssdb4j.SSDBs
import org.nutz.ssdb4j.spi.SSDB

import scala.reflect.ClassTag

/**
  * @author tvc12 - thienvc
  * @since  09/03/2020
  */
case class ListDbDouble(dbname: String, client: SSDB)(
  implicit ev: ClassTag[Double]
) extends ListDbImpl[Double](dbname, client)(
      ev,
      value => value.toString.getBytes(),
      bytes => new String(bytes).toDouble
    )

object ListDbDouble {
  def apply(dbname: String, host: String, port: Int, timeout: Int = 5000)(
    implicit ev: ClassTag[Double]
  ): ListDbDouble = {
    val client = SSDBs.pool(host, port, timeout, null)
    new ListDbDouble(dbname, client)(ev)
  }
}
