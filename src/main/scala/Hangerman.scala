
import java.sql.{Connection, DriverManager}

import scala.collection.mutable
import scala.io.Source


object Hangerman extends App {
	val url = "jdbc:mysql://localhost:3306/scalafun"
	val driver = "com.mysql.jdbc.Driver"
	val username = "root"
	val password = "password"
	var connection: Connection = _
	var listOfWords: mutable.MutableList[String] = mutable.MutableList()
	var wordAmount: Int = 0

	def applyTheWords(): Unit = {
		var counter = 0
		randomValue.randomInt
		val filename = "C:\\Users\\Administrator\\IdeaProjects\\Hanger\\src\\main\\otherFiles\\someWords.txt"
		for (line <- Source.fromFile(filename).getLines) {
			listOfWords += line
		}
		wordAmount = listOfWords.length

		println(wordAmount + " here be the random number " + randomValue.randomInt)
	}

	def showMeTheHangman(): Unit = {
		println("           _______\n          |/      |\n          |      ( )\n          |      /|\\\n          |       |")
		println("          |      / \\\n          |\n       ___|___  _____")
	}

	def applyTheSQL(): Unit = {
		try {
			Class.forName(driver)
			connection = DriverManager.getConnection(url, username, password)

				val statement = connection.createStatement()
				val resultSets = statement.executeQuery("SELECT * FROM hangman_data")
				//val resultSets = statement.execute("INSERT INTO hangman_data (\"" + word + "\")")
				while (resultSets.next) {
					println(resultSets.getString("word"))
				}
		} catch {
			case e: Exception => println("You goofed! No stack trace for you." + e.printStackTrace())
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