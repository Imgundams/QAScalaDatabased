
import java.sql.{Connection, DriverManager}

object Hangerman extends App {
	val url = "jdbc:mysql://localhost:3306/scalafun"
	val driver = "com.mysql.jdbc.Driver"
	val username = "root"
	val password = "password"
	var connection: Connection = _
	def printNicer():Unit = {

	}

	try {
		Class.forName(driver)
		connection = DriverManager.getConnection(url, username, password)
		val statement = connection.createStatement()
		val resultSets = statement.executeQuery("SELECT * FROM hangman_data")

		while (resultSets.next) {
			println(resultSets.getString("word"))
		}
	} catch {
		case e: Exception => println("You goofed")//;e.printStackTrace()
	}
	connection.close()
}
