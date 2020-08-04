package education.x.util

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/**
 * @author andy
 * @since 7/31/20
 **/
object DataSourceBuilder {

  def buildDataSource(driverClazz: String,
                      jdbcUrl: String,
                      username: String,
                      password: String): DataSource = {
    val ds = new HikariDataSource
    ds.setDriverClassName(driverClazz)
    ds.setJdbcUrl(jdbcUrl)
    ds.setUsername(username)
    ds.setPassword(password)
    ds
  }

  def buildMySQLDataSource(host: String,
                           port: Int,
                           dbName: String,
                           username: String,
                           password: String,
                           tz: Option[String] = Some("Asia/Ho_Chi_Minh")): DataSource = {
    val ds = new HikariDataSource
    ds.setDriverClassName("com.mysql.jdbc.Driver")
    ds.setJdbcUrl(s"jdbc:mysql://$host:$port/$dbName?useLegacyDatetimeCode=false&serverTimezone=${tz.get}")
    ds.setUsername(username)
    ds.setPassword(password)
    ds
  }
}
