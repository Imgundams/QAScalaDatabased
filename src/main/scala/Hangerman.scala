
import java.sql.{Connection, DriverManager}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
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
	private var randomWordSelectedLength: Int = 0
	var randomValueSelected: Int = 0
	private var loseCounter: Int = 0
	private var game: Boolean = true
	private var hiddenWord = ArrayBuffer[String]()
	private var hiddenWordhidden = ArrayBuffer[String]()
	private var usedWords = ArrayBuffer[String]()

	def applyTheWords(): Unit = {
		loseCounter = 0
		val filename = "C:\\Users\\Administrator\\IdeaProjects\\Hanger\\src\\main\\otherFiles\\allWords.txt" // or allWords take too long
		for (line <- Source.fromFile(filename).getLines) {
			listOfWords += line
		}
		wordAmount = listOfWords.length
	}

	def wordSelection(): Unit = {
		val randomValueSelected = randomValue.randomInt
		randomWordSelected = listOfWords(randomValueSelected)
		randomWordSelectedLength = randomWordSelected.length
		randomWordSelected.foreach(char => hiddenWord += char.toString)
		hiddenWord.foreach(char => hiddenWordhidden += "_")
		println(wordAmount + " here be the selected word \"" + randomWordSelected + "\" it's index is " + randomValueSelected)

	}

	def showMeTheHangman(): Unit = {
		loseCounter match {
			case 0 => println("\n\n\n\n\n\n\n       _______")
			case 1 => println("\n\n\n\n\n\n\n       ___|___")
			case 2 => println("\n\n\n\n\n          |\n          |\n       ___|___")
			case 3 => println("\n\n\n\n          |\n          |\n          |\n       ___|___")
			case 4 => println("           ___\n          |/\n          |\n          |\n          |\n          |       \n          | \n       ___|___")
			case 5 => println("           _______\n          |/\n          |\n          |\n          |\n          |\n          |\n       ___|___ ")
			case 6 => println("           _______\n          |/      |\n          |      \n          |      \n          |       \n          |      \n          |\n       ___|___")
			case 7 => println("           _______\n          |/      |\n          |      ( )\n          |      \n          |       \n          | \n          |\n       ___|___    _")
			case 8 => println("           _______\n          |/      |\n          |      ( )\n          |      /|\\\n          |       |\n          |      / \\\n          |\n       ___|___  _____")
			case 10 => println("\\ /\n |\n/o\\\nYou have escaped the noose and freed yourself.")
			case _ => println("Better luck next time.")
		}
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

		randomValueSelected = randomValue.randomInt
		resultsSets = statement.executeQuery("SELECT word FROM hangman_data WHERE id=" + randomValueSelected)
		resultsSets.next()
		randomWordSelected = resultsSets.getString("word")
		randomWordSelectedLength = randomWordSelected.length
		randomWordSelected.foreach(char => hiddenWord += char.toString)
		hiddenWord.foreach(char => hiddenWordhidden += "_")
		println("There are " + wordAmount + " words in the list, the selected word is \"" + randomWordSelected + "\" it's index is " + randomValueSelected)

		connection.close()
	}

	def applyDifficultySQL(difficulty: Int): Unit = {
		Class.forName(driver)
		connection = DriverManager.getConnection(url, username, password)
		val statement = connection.createStatement()
		var resultsSets = statement.executeQuery("SELECT COUNT(*)AS totalrows FROM hangman_words")
		var correctDifficulty: Boolean = false
		resultsSets.next()
		wordAmount = resultsSets.getInt("totalrows")
		do {
			randomValueSelected = randomValue.randomInt
			resultsSets = statement.executeQuery("SELECT word FROM hangman_words WHERE id=" + randomValueSelected)
			resultsSets.next()
			randomWordSelected = resultsSets.getString("word")
			randomWordSelectedLength = randomWordSelected.length
			difficulty match {
				case 1 if randomWordSelectedLength < 4 => correctDifficulty = true
				case 2 if randomWordSelectedLength < 5 => correctDifficulty = true
				case 3 if randomWordSelectedLength < 6 => correctDifficulty = true
				case _ if randomWordSelectedLength >= 6 => correctDifficulty = true
			}
		} while (!correctDifficulty)
		randomWordSelected.foreach(char => hiddenWord += char.toString)
		hiddenWord.foreach(char => hiddenWordhidden += "_")
		println("There are " + wordAmount + " words in the list, the selected word is \"" + randomWordSelected + "\" it's index is " + randomValueSelected)

		connection.close()
	}

	def hiddenToShownWord(char: Char): Unit = {
		for (i <- hiddenWord.indices) {
			if (hiddenWord(i) == char.toString) hiddenWordhidden(i) = char.toString
		}
	}

	def difficultySelection(): Int = {
		println("Please select a difficulty you wish to play hangman with.\nPress 1: Casual mode\nPress 2: Easy mode\nPress 3: Normal mode\nPress 4: Hard mode")
		try {
			var selectedDifficulty = scala.io.StdIn.readLine().toInt
			selectedDifficulty match {
				case 1 => 1
				case 2 => 2
				case 3 => 3
				case _ if selectedDifficulty < 1 => 1
				case _ if selectedDifficulty > 3 => 4
				case _ => 4
			}
		} catch {
			case _: java.lang.NumberFormatException => println("Error input, try again.")
				difficultySelection()
		}
	}

	def theActualIntroGame(): Unit = { // Not need for testing
		var difficulty = difficultySelection()
		print("The game is being prepared! Please enter any key or type \"go\" to run with a text file or type \"sql\" for loading with sql database:")
		loseCounter = 0
		game = true
		var scanner = scala.io.StdIn.readLine()
		scanner match {
			case "go" => println("Boogie Time!")
				applyTheWords()
				wordSelection()

			case "sql" => println("SQL Time!")
				println("Reload the data in the database? y\\n?")
				scanner = scala.io.StdIn.readLine()
				if (scanner == "y") applyWordFromSQL()
				else {
					applyTheWords()
					applyTheSQL()
					applyWordFromSQL()
				}

			case _ if difficulty == 1 => applyWordFromSQL()
			case _ => applyDifficultySQL(difficulty)
		}
	}

	def processTheCharEntered(char: Char): Unit = {
		char match {
			case _ if randomWordSelected.contains(char) => println("you live")
				hiddenToShownWord(char)
				if (!hiddenWordhidden.contains("_")) {
					game = false
					loseCounter = 10
					showMeTheHangman()
					print("You win! The correct word was indeed \"" + randomWordSelected + "\".")
				}
			case _ => if (usedWords.contains(char.toString)) println("You already guessed the character " + char)
			else loseCounter += 1

				if (loseCounter == 8) {
					println("You lose, you get nothing! The word was \"" + randomWordSelected + "\".")
					showMeTheHangman()
					game = false
				}
		}
	}


	def theActualGame(): Unit = {
		while (game) {
			showMeTheHangman()
			println(hiddenWordhidden.mkString(" "))
			print("Game is on!\nWords entered so far are " + usedWords.mkString(", ") + "\nPlease enter a character! :")
			var scanner = scala.io.StdIn.readLine().charAt(0)
			if (!usedWords.contains(scanner.toString)) {
				processTheCharEntered(scanner)
				usedWords += scanner.toString
			}
		}
	}

	theActualIntroGame()
	theActualGame()

	println("Game Over")

	object randomValue {
		private val random = scala.util.Random

		def randomBoolean: Boolean = random.nextBoolean()

		def randomInt: Int = random.nextInt(wordAmount + 1)
	}

}