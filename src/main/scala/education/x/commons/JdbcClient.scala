package education.x.commons

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement}

import education.x.util.Using
import javax.sql.DataSource

case class DbRecord(fields: Seq[Any])

trait JdbcClient {

  def execute(query: String, values: Any*): Boolean

  def executeQuery[T](query: String, values: Any*)(implicit converter: ResultSet => T): T

  def executeUpdate(query: String, values: Any*): Int

  def executeBatchUpdate(query: String, records: Seq[DbRecord], batchSize: Int = 500): Int

}

abstract class AbstractJdbcClient extends JdbcClient {

  def getConnection(): Connection

  def execute(query: String, values: Any*): Boolean = {
    Using(getConnection()) { conn =>  {
      Using(conn.prepareStatement(query)) { statement => {
        parameterizeStatement(statement, values).execute()
      }
      }
    }
    }
  }

  /** *
   *
   * Ex: executeQuery( "Select from Users where id = ?;", 1)
   * Supported Type: Boolean, BigDecimal, Byte, Date, Float, Double, Int, Long, String, Time, Timestamp
   *
   * @param query Parameterized Query
   * @param values Value to put to parameterized query
   * @return
   */
  def executeQuery[T](query: String, values: Any*)(implicit converter: ResultSet => T): T = {
    Using(getConnection()) { conn => {
      Using(conn.prepareStatement(query)) { statement => {
        Using(parameterizeStatement(statement, values).executeQuery()) { resultSet => {
          converter(resultSet)
        }
        }
      }
      }
    }
    }
  }

  /** *
   *
   * Ex: executeUpdate( "Insert INTO Users(?,?)", 1L, "User A")
   * Supported Type: Boolean, BigDecimal, Byte, Date, Float, Double, Int, Long, String, Time, Timestamp
   *
   * @param query Parameterized Query
   * @param values Value to put to parameterized query
   * @return
   */
  def executeUpdate(query: String, values: Any*): Int = {
    Using(getConnection()) { conn => {
      Using(conn.prepareStatement(query)) { statement => {
        parameterizeStatement(statement, values).executeUpdate()
      }
      }
    }
    }
  }

  def executeBatchUpdate(query: String, records: Seq[DbRecord], batchSize: Int = 500): Int = {

    Using(getConnection()) { conn => {
      Using(conn.prepareStatement(query)) { statement => {
        executeBatchUpdate(statement, records, batchSize)
      }
      }
    }
    }
  }

  private def executeBatchUpdate(statement: PreparedStatement, records: Seq[DbRecord], batchSize: Int) : Int = {
    var resultCount = 0
    var batchCount = 0
    records.foreach(record => {
      parameterizeStatement(statement, record.fields)
      statement.addBatch()
      batchCount += 1
      if (batchCount % batchSize == 0) {
        statement.executeBatch()
        resultCount += batchCount
      }
    })
    statement.executeBatch()
    resultCount += batchCount

    resultCount
  }

  private def parameterizeStatement(statement: PreparedStatement, values: Seq[Any]): PreparedStatement = {

    values.zipWithIndex.foreach{
      case (value, index) =>
        val paramIndex  = index + 1
        value match {
        case v: java.sql.Date => statement.setDate(paramIndex, v)
        case v: java.sql.Time => statement.setTime(paramIndex, v)
        case v: java.sql.Timestamp => statement.setTimestamp(paramIndex, v)
        case v: Boolean => statement.setBoolean(paramIndex, v)
        case v: Byte => statement.setByte(paramIndex, v)
        case v: Int => statement.setInt(paramIndex, v)
        case v: Long => statement.setLong(paramIndex, v)
        case v: Float => statement.setFloat(paramIndex, v)
        case v: Double => statement.setDouble(paramIndex, v)
        case v: java.math.BigDecimal => statement.setBigDecimal(paramIndex, v)
        case v: String => statement.setString(paramIndex, v)
        case e: Any => throw new IllegalArgumentException(s"unsupported data type + $e + ${e.getClass}")
      }
    }
    statement
  }

}

case class NativeJdbcClient(jdbcUrl: String,
                            username: String,
                            password: String) extends AbstractJdbcClient {
  override def getConnection(): Connection = {
    if(username != null && username.nonEmpty)
      DriverManager.getConnection(jdbcUrl, username, password)
    else
      DriverManager.getConnection(jdbcUrl)
  }
}


case class HikariJdbcClient(ds: DataSource) extends AbstractJdbcClient {

  override def getConnection(): Connection =  ds.getConnection

}



