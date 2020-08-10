package education.x.util

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/**
 * @author andy
 * @since 7/31/20
 **/

object DataSourceBuilder {
  def hikari() = HikariDataSourceBuilder()
}

trait DataSourceBuilder[DS <: DataSource] {
  def build(): DS
}

case class HikariDataSourceBuilder() extends DataSourceBuilder[HikariDataSource] {
  private var driver: String = null
  private var jdbcUrl: String = null
  private var username: String = null
  private var password: String = null
  private var isAutoCommit: Boolean = true

  def driver(className: String): HikariDataSourceBuilder = {
    this.driver = className
    this
  }

  def jdbcUrl(jdbcUrl: String): HikariDataSourceBuilder = {
    this.jdbcUrl = jdbcUrl
    this
  }

  def username(username: String): HikariDataSourceBuilder = {
    this.username = username
    this
  }

  def password(password: String): HikariDataSourceBuilder = {
    this.password = password
    this
  }

  def autoCommit(isAutoCommit: Boolean): HikariDataSourceBuilder = {
    this.isAutoCommit = isAutoCommit
    this
  }

  override def build(): HikariDataSource = {
    validateConfig()

    val ds = new HikariDataSource
    ds.setDriverClassName(driver)
    ds.setJdbcUrl(jdbcUrl)
    if (username != null) {
      ds.setUsername(username)
      ds.setPassword(password)
    }
    ds.setAutoCommit(isAutoCommit)
    ds
  }

  private def validateConfig(): Unit = {
    if (driver == null || driver.isEmpty)
      throw new IllegalArgumentException("the driver class can't be null or empty.")
    if (jdbcUrl == null || jdbcUrl.isEmpty)
      throw new IllegalArgumentException("the jdbc url can't be null or empty.")
  }
}


