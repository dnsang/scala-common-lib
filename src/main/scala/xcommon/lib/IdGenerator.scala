package xcommon.lib

import org.nutz.ssdb4j.spi.SSDB

import scala.concurrent.{ExecutionContext, Future}

trait IDGenerator[T] {

  def getNextId(): Future[Option[T]]

}

case class I32IdGenerator(uniqueName: String, serviceName: String, client: SSDB, addUp: Int = 1)(implicit ec: ExecutionContext = ExecutionContext.global) extends IDGenerator[Int] {

  override def getNextId(): Future[Option[Int]] = {
    Future {
      val resp = client.hincr(uniqueName, serviceName, addUp)
      if (resp.ok())
        Some(resp.asInt())
      else None
    }
  }
}

