
import java.sql.{Connection, DriverManager}

import scala.collection.mutable
import scala.io.Source


object Hangerman extends App {
	private val url = "jdbc:mysql://localhost:3306/scalafun"
	private val driver = "com.mysql.jdbc.Driver"
	private val username = "root"
	private val password = "password"
	private val tableDrop = "DROP TABLE IF EXISTS hangman_data"
	private val createTable = "CREATE TABLE hangman_data (id int auto_increment primary key, word varchar(64) NOT NULL)"
	private var connection: Connection = _
	private var listOfWords: mutable.MutableList[String] = mutable.MutableList()
	private var wordAmount: Int = 0
	private var randomWordSelected: String = ""
	private var randomWordSelectedChars: Array[String] = Array[String]()
	private var randomWordSelectedLength: Int = 0
	private var loseCounter: Int = 0
	private var game: Boolean = true
	private var hiddenWord:Array[String] = Array[String]()
//	private var wordMaps = scala.collection.mutable.Map[Int, Boolean]()

	def applyTheWords(): Unit = {
		var counter = 0
		val filename = "C:\\Users\\Administrator\\IdeaProjects\\Hanger\\src\\main\\otherFiles\\someWords.txt" // or allWords take too long
		for (line <- Source.fromFile(filename).getLines) {
			listOfWords += line
		}
		wordAmount = listOfWords.length
	}

	def wordSelection(): Unit = {
		val randomValueSelected = randomValue.randomInt
		randomWordSelected = listOfWords(randomValueSelected)
		randomWordSelectedLength = randomWordSelected.length
		randomWordSelectedChars = randomWordSelected.split("")
		for(i<- 0 until(hiddenWord.length)){hiddenWord(i)= "_"}
		println(wordAmount + " here be the selected word \"" + randomWordSelected + "\" it's index is " + randomValueSelected)

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
				if (i % 250 == 0) println(wordAmount - i + " to go, please wait...")
				//			val resultSets = statement.executeUpdate("INSERT INTO hangman_data VALUES (1,'newWord')")
				//			val resultSets = statement.executeQuery("SELECT * FROM hangman_data")
				//			val resultSets = statement.executeUpdate("INSERT INTO hangman_data VALUES("+(i+1)+",'" + listOfWords(i) +"')")
				val resultSets = statement.executeUpdate("INSERT INTO hangman_data(word) VALUES('" + listOfWords(i) + "')") // puts words int SQLDB
			}
			//			while (resultSets.next) {println(resultSets.getString("word"))}
		} catch {
			case e: Exception => println("Something went wrong!" + e.printStackTrace())
			//connection.rollback()
		}
		connection.close()
	}

	def applyWordFromSQL(): Unit = {
		Class.forName(driver)
		connection = DriverManager.getConnection(url, username, password)
		val statement = connection.createStatement()
		var resultsSets = statement.executeQuery("SELECT COUNT(*)AS totalrows FROM hangman_data")
		resultsSets.next()
		wordAmount = resultsSets.getInt("totalrows")

		val randomValueSelected = randomValue.randomInt
		resultsSets = statement.executeQuery("SELECT word FROM hangman_data WHERE id=" + randomValueSelected)
		resultsSets.next()
		randomWordSelected = resultsSets.getString("word")
		randomWordSelectedLength = randomWordSelected.length
		randomWordSelectedChars = randomWordSelected.toCharArray
		hiddenWord = ("_" * randomWordSelectedLength).toCharArray
		println("There are " + wordAmount + " words in the list, the selected word is \"" + randomWordSelected + "\" it's index is " + randomValueSelected)

		connection.close()
	}

	def processTheCharEntered(char: Char): Unit = {
		char match {
			case _ if randomWordSelected.contains(char) => println("you live")
				hiddenToShownWord(char)
				if (randomWordSelectedChars.count(_ == "_") == randomWordSelectedLength) {game =true
				print("You win!")}
			case _ => loseCounter += 1
				if (loseCounter > 10) {
					println("you lose")
					showMeTheHangman()
					game = false
				}
		}
	}
	def hiddenToShownWord(char:Char):Unit ={

		(randomWordSelectedChars.indexWhere(chara=> chara ==char)) = char
		randomWordSelectedChars(randomWordSelected.indexWhere(chara=> chara == char)) = "_"

		randomWordSelected map(case char =>)

		randomWordSelected.replace(char:Char , '_':Char)
		if (randomWordSelectedChars.contains(char)){ hiddenToShownWord(char)}
		else
			println(hiddenWord)
	}

	def theActualIntroGame(): Unit = { // Not need for testing
		print("The game is being prepared! Please enter go to run with text or type sql for running with sql database:")
		loseCounter = 0
		game = true
		var scanner = scala.io.StdIn.readLine()
		scanner match {
			case "go" => println("Boogie Time!")
				applyTheWords()
				wordSelection()

			case "sql" => println("SQL Time!")
				println("Is the data for the words existing? y\\n?")
				scanner = scala.io.StdIn.readLine()
				if (scanner == "y") applyWordFromSQL()
				else {
					applyTheWords()
					applyTheSQL()
					applyWordFromSQL()
				}

			case _ => sys.exit()
		}
	}

	def theActualGame(): Unit = {
		while (game) {
			println(hiddenWord)
			print("Game is on! Please enter a character! :")
			var scanner = scala.io.StdIn.readLine()
			processTheCharEntered(scanner.charAt(0))
		}
	}

	theActualIntroGame()
	theActualGame()

	println("All done, no problems here.")

	object randomValue {
		private val random = scala.util.Random

		def randomBoolean: Boolean = random.nextBoolean()

		def randomInt: Int = random.nextInt(wordAmount + 1)
	}

}