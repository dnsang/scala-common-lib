# Scala Common Lib

This project aims to provide a generic common backend interface/impl for java/scala developers.

I32IdGenerator

	val idGen = I32IdGenerator("project_a","user_id", ssdb)
	val id = idGen.getNextId()

List

    val listUserFriends = SsdbList[Int]("user_a", ssdb)

    listUserFriends.multiPushFront(Array(1,2,3,4))

    val listUserMessages = SsdbList[String]("id")

    val listTransaction = SsdbList[Transaction]("user_id")

KeyValueDB

	val kvs = SsdbKVS[String,String]("user_db",ssdb)

        kvs.add("user_id","{ json_user_value }")

	val value = kvs.get("user_id")

	val dbUserProfile = SsdbKVS[Long,UserProfile]("user_profile")

	val jackProfile = UserProfile(123,"Jack",19,"jack@x.education")

	dbUserProfile.add(123L, jackProfile)

	val profile = dbUserProfile.get(123)


SortedSet

        val leaderboard = SSdbSortedSet("game_a",ssdb)
        leaderboard.add("user_1",100)
        val rank = leaderboard.rank("user_1")
        val top10 = leaderboard.range(0,10)



JDBC Client

        val client = NativeJdbcClient("jdbc:mysql://127.0.0.1:9000", "user", "password")
        // Insert a row
        client.executeUpdate(
              s"insert into users(id, name, age, created_date) values(?, ?, ?, ?);",
              1, "user1", 1, new Date(1589446762000L))
              
        // Delete a row          
        val afftedRows = client.executeUpdate(s"delete from users where id = ?", 1)

Usage
	
	```
	<dependency>
  		<groupId>education.x</groupId>
  		<artifactId>common-backend</artifactId>
  		<version>2.3-SnapShot</version>
	</dependency>
	```
