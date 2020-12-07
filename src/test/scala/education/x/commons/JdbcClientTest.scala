package education.x.commons

import java.sql.{Date, Timestamp}

import com.zaxxer.hikari.HikariDataSource
import org.scalatest.FunSuite

class JdbcClientTest extends FunSuite {

  test("mysql client test") {
    val jdbcUrl: String = "jdbc:mysql://localhost:3306?useLegacyDatetimeCode=false&serverTimezone=Asia/Ho_Chi_Minh"
    val user: String ="root"
    val password: String = "root@222"

    val client =  NativeJdbcClient(jdbcUrl, user, password)
    singleCRUDMySqlTest(client)

  }

  test("clickhouse client test") {
    val jdbcUrl: String = "jdbc:clickhouse://127.0.0.1:9000"
    val user: String = null
    val password: String = null

    val client = NativeJdbcClient(jdbcUrl, user, password)
    singleCRUDClickhouseTest(client)
    batchInsertClickhouseTest(client)
  }

  /** *
    * Test: Create new test db, insert, and query data
    *
    * @param client
    */
  def singleCRUDMySqlTest(client: JdbcClient): Unit = {

    val dbName = "bi_service_client_test"
    val tblName = "users"

    assert(client.executeUpdate(s"drop database if exists $dbName;") >= 0)

    assert(client.executeUpdate(s"create database $dbName;") >= 0)

    assert(client.executeUpdate(
      s"""
        |create table $dbName.$tblName(
        |id INT AUTO_INCREMENT PRIMARY KEY,
        |name VARCHAR(255),
        |weight DOUBLE,
        |account LONG,
        |is_active BOOLEAN,
        |created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        |) ENGINE=INNODB;
        |""".stripMargin) >= 0)

    assert(client.executeUpdate(
      s"insert into $dbName.$tblName(id, name, weight, account, is_active, created_at) values(?, ?, ?, ?, ?, ?);",
      1, "user1", 60.5, 100, true, new Timestamp(1589446762000L)) == 1)

    assert(client.executeQuery(s"select * from $dbName.$tblName where name = ?;", "user1")(_.next()))

    assert(client.executeUpdate(s"update $dbName.$tblName set name = ? where id = ?;", "renamed", 1) == 1)

    assert(client.executeQuery(s"select * from $dbName.$tblName where name = ?", "renamed")(_.next()))

    assert(client.executeUpdate(s"delete from $dbName.$tblName where id = ?", 1) == 1)

    assert(!client.executeQuery(s"select * from $dbName.$tblName where id = ?", 1)(_.next()))

  }

  def singleCRUDClickhouseTest(client: JdbcClient): Unit = {

    val dbName = "bi_service_client_test"
    val tblName = "users"

    assert(client.executeUpdate(s"drop database if exists $dbName;") >= 0)

    assert(client.executeUpdate(s"create database $dbName;") >= 0)

    assert(client.executeUpdate(
      s"""
         |create table $dbName.$tblName(
         |created_date Date default toDate(toDateTime(timestamp)),
         |timestamp UInt64 default toUnixTimestamp(now()),
         |id Int32,
         |name String,
         |age UInt32
         |) Engine=MergeTree(created_date, (timestamp, id), 8192);
         |""".stripMargin) >= 0)

    assert(client.executeUpdate(
      s"insert into $dbName.$tblName(id, name, age, created_date) values(?, ?, ?, ?);",
      1, "user1", 1, new Date(1589446762000L)) == 1)

    assert(client.executeQuery(s"select * from $dbName.$tblName;")(_.next()))

    assert(client.executeUpdate(s"alter table $dbName.$tblName update name = ? where id = ?;", "renamed", 1) >= 0)

    // Wait a bit after update a column value
    Thread.sleep(5000)
    assert(client.executeQuery(s"select * from $dbName.$tblName where name = ?", "renamed")(_.next()))

    assert(client.executeUpdate(s"alter table $dbName.$tblName delete where id = ?;", 1) >= 0)

    assert(client.executeQuery(s"select count(*) from $dbName.$tblName;")(_.next()))

    assert(client.executeUpdate(s"drop database $dbName;") >= 0)

  }

  def batchInsertClickhouseTest(client: JdbcClient): Unit = {

    val dbName = "bi_service_client_test"
    val tblName = "users"

    assert(client.executeUpdate(s"drop database if exists $dbName;") >= 0)

    assert(client.executeUpdate(s"create database $dbName;") >= 0)

    assert(client.executeUpdate(
      s"""
         |create table $dbName.$tblName(
         |created_date Date default toDate(toDateTime(timestamp)),
         |timestamp UInt64 default toUnixTimestamp(now()),
         |id Int32,
         |name String,
         |age UInt32
         |) Engine=MergeTree(created_date, (timestamp, id), 8192);
         |""".stripMargin) >= 0)

    val records = Seq(
      Seq(Seq(1, "user1", 1, new Date(1589446762000L))),
      Seq(Seq(2, "user2", 4, new Date(1589446762000L))),
      Seq(Seq(3, "user3", 7, new Date(1589446762000L))),
      Seq(Seq(4, "user4", 5, new Date(1589446762000L))),
      Seq(Seq(5, "user5", 18, new Date(1589446762000L))),
      Seq(Seq(7, "user7", 67, new Date(1589446762000L)))
    )

    assert(client.executeBatchUpdate(s"insert into $dbName.$tblName(id, name, age, created_date) values(?, ?, ?, ?)", records) == 6)

    assert(client.executeQuery(s"select * from $dbName.$tblName;")(_.next()))

    assert(client.executeUpdate(s"drop database $dbName;") >= 0)

  }

}
