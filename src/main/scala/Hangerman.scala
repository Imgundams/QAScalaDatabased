
import java.sql.{Connection, DriverManager}
import scala.io.Source



object Hangerman extends App {
	val url = "jdbc:mysql://localhost:3306/scalafun"
	val driver = "com.mysql.jdbc.Driver"
	val username = "root"
	val password = "password"
	var connection: Connection = _

	def applyTheWords():Unit= {
		val filename = "C:\\Users\\Administrator\\IdeaProjects\\Hanger\\src\\main\\otherFiles\\allWords.txt"
		for (line <- Source.fromFile(filename).getLines) {
			println(line)
		}
	}

	def applyTheSQL(): Unit = {
		try {
			Class.forName(driver)
			connection = DriverManager.getConnection(url, username, password)
			val statement = connection.createStatement()
			val resultSets = statement.executeQuery("SELECT * FROM hangman_data")

			while (resultSets.next) {
				println(resultSets.getString("word"))
			}
		} catch {
			case e: Exception => println("You goofed! No stack trace for you.") //+ e.printStackTrace()
		}
		connection.close()
	}

	println("No problems here.")
}