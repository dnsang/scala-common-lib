# Scala Common Lib

This project aims to provide a generic common backend interface/impl for java/scala developers.

I32IdGenerator

	val idGen = I32IdGenerator("project_a","user_id", ssdb)
	val id = idGen.getNextId()

KeyValueDB
	val kvs = KVSDbImpl("user_db",ssdb)
        kvs.add("user_id","{ json_user_value }")
	val value = kvs.get("user_id")

SortedSet
        val leaderboard = SSdbSortedSet("game_a",ssdb)
        leaderboard.add("user_1",100)
        val rank = leaderboard.rank("user_1")
        val top10 = leaderboard.range(0,10)


