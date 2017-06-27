
import java.sql.{Connection, DriverManager}

import scala.collection.mutable
import scala.io.Source


object Hangerman extends App {
	val url = "jdbc:mysql://localhost:3306/scalafun"
	val driver = "com.mysql.jdbc.Driver"
	val username = "root"
	val password = "password"
	val tableDrop = "DROP TABLE IF EXISTS hangman_data"
	val createTable = "CREATE TABLE hangman_data (id int auto_increment primary key, word varchar(64) NOT NULL)"
	var connection: Connection = _
	var listOfWords: mutable.MutableList[String] = mutable.MutableList()
	var wordAmount: Int = 0
	var randomWordSelected :String = ""

	def applyTheWords(): Unit = {
		var counter = 0
		val filename = "C:\\Users\\Administrator\\IdeaProjects\\Hanger\\src\\main\\otherFiles\\someWords.txt" // or allWords take too long
		for (line <- Source.fromFile(filename).getLines) {
			listOfWords += line
		}
		wordAmount = listOfWords.length
		val randomValueSelected= randomValue.randomInt
		randomWordSelected = listOfWords(randomValueSelected)
		println(wordAmount + " here be the random number " + randomWordSelected + " "+ randomValueSelected)
	}

	def showMeTheHangman(): Unit = {
		println("           _______\n          |/      |\n          |      ( )\n          |      /|\\\n          |       |")
		println("          |      / \\\n          |\n       ___|___  _____")
	}

	def applyTheSQL(): Unit = {
		// only need to run once
		try {
			Class.forName(driver)
			connection = DriverManager.getConnection(url, username, password)

			val statement = connection.createStatement()
			statement.executeUpdate(tableDrop) //drops any existing tables off the building
			statement.executeUpdate(createTable) // builds table
			for (i <- 0 until wordAmount) {
				//			val resultSets = statement.executeUpdate("INSERT INTO hangman_data VALUES (1,'newWord')")
				//			val resultSets = statement.executeQuery("SELECT * FROM hangman_data")
				//			val resultSets = statement.executeUpdate("INSERT INTO hangman_data VALUES("+(i+1)+",'" + listOfWords(i) +"')")
				val resultSets = statement.executeUpdate("INSERT INTO hangman_data(word) VALUES('" + listOfWords(i) + "')") // puts words int SQLDB
			}
			//			while (resultSets.next) {println(resultSets.getString("word"))}
		} catch {
			case e: Exception => println("You goofed! No stack trace for you." + e.printStackTrace())
				connection.rollback()
		}
		connection.close()
	}

	applyTheWords()
	applyTheSQL()
	println("No problems here.")

	object randomValue {
		private val random = scala.util.Random

		def randomBoolean: Boolean = random.nextBoolean()

		def randomInt: Int = random.nextInt(wordAmount + 1)
	}

}