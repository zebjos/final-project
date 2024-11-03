package com.example.final_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.final_project.data.GameModel
import com.example.final_project.data.GameStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random
import kotlin.random.nextInt

class GameViewModel : ViewModel() {

    private val _gameModel = MutableLiveData<GameModel?>()
    val gameModel: LiveData<GameModel?> = _gameModel

    var myID: String = "" // This will hold "X" or "O" to identify the player

    // Initialize Firebase Realtime Database
    private val database = FirebaseDatabase.getInstance()

    // LiveData for the list of games with status CREATED
    private val _createdGames = MutableLiveData<List<GameModel>>()
    val createdGames: LiveData<List<GameModel>> = _createdGames

    // Function to create a new online game
    // In GameViewModel
    fun createOnlineGame(onGameCreated: (String, String) -> Unit) {
        myID = "X" // The creator of the game will be "X"

        // Generate a unique game ID
        val newGameId = Random.nextInt(1000..9999).toString()

        // Create a new game model
        val newGameModel = GameModel(
            gameStatus = GameStatus.CREATED,
            gameId = newGameId,
            currentPlayer = "X" // The creator starts as "X"
        )

        // Save the game model to Realtime Database
        database.getReference("games")
            .child(newGameId)
            .setValue(newGameModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _gameModel.postValue(newGameModel) // Update the LiveData
                    println("Game model saved successfully with ID: $newGameId")

                    // Invoke the callback with the gameId and myID
                    onGameCreated(newGameId, myID)
                } else {
                    println("Error saving game model: ${task.exception?.message}")
                }
            }
    }

    // Function to join an existing online game
    fun joinOnlineGame(gameId: String) {
        myID = "O" // The joining player will be "O"

        println("Player joined the game. myID is: $myID") // Trying to debug myID

        // Fetch the game data from Realtime Database
        database.getReference("games")
            .child(gameId)
            .get()
            .addOnSuccessListener { snapshot ->
                val model = snapshot.getValue(GameModel::class.java)
                if (model != null) {
                    model.gameStatus = GameStatus.JOINED
                    saveGameModel(model) // Save the updated game model

                    // Automatically start the game if both players are ready
                    if (model.gameStatus == GameStatus.JOINED) {
                        startGame()
                    }

                    println("Joined game successfully with ID: $gameId")
                } else {
                    println("Game ID not found: $gameId")
                }
            }
            .addOnFailureListener { e ->
                println("Error joining game: ${e.message}")
            }
    }

    // Function to save the game model to Realtime Database
    fun saveGameModel(model: GameModel) {
        _gameModel.value = model
        if (model.gameId != "-1") {
            database.getReference("games")
                .child(model.gameId)
                .setValue(model)
                .addOnSuccessListener {
                    println("Game model updated successfully with ID: ${model.gameId}")
                }
                .addOnFailureListener { e ->
                    println("Error updating game model: ${e.message}")
                }
        }
    }

    // Function to fetch the game model from Realtime Database
    fun fetchGameModel(gameId: String) {
        if (gameId != "-1") {
            FirebaseDatabase.getInstance().getReference("games")
                .child(gameId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val model = snapshot.getValue(GameModel::class.java)
                        if (model != null) {
                            _gameModel.postValue(model) // Update the game model with real-time data
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Error fetching game model: ${error.message}")
                    }
                })
        }
    }

    // Function to start the game (set status to INPROGRESS)
    fun startGame() {
        _gameModel.value?.let { model ->
            model.gameStatus = GameStatus.INPROGRESS
            saveGameModel(model)
            println("Game started with ID: ${model.gameId}")
        }
    }

    // Function to handle cell clicks in the game
    fun onCellClicked(index: Int) {
        _gameModel.value?.let { model ->
            // Check if the game is in progress
            if (model.gameStatus != GameStatus.INPROGRESS) return

            // Check if it's the current player's turn
            if (model.gameId != "-1" && model.currentPlayer != myID) return

            // Make the move only if the cell is empty
            if (model.filledPos[index].isEmpty()) {
                model.filledPos[index] = model.currentPlayer

                // Optimistically switch turns locally
                model.currentPlayer = if (model.currentPlayer == "X") "O" else "X"

                // Save the updated game state to Firebase
                saveGameModel(model)

                // Check for a winner or a draw after making a move
                checkForWinner(model)
            }
        }
    }

    private fun checkForWinner(model: GameModel) {
        val winningPositions = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )

        for (positions in winningPositions) {
            if (model.filledPos[positions[0]] == model.filledPos[positions[1]] &&
                model.filledPos[positions[1]] == model.filledPos[positions[2]] &&
                model.filledPos[positions[0]].isNotEmpty()
            ) {
                model.gameStatus = GameStatus.FINISHED
                model.winner = model.filledPos[positions[0]]
                saveGameModel(model) // Save the winning state immediately
                return
            }
        }

        if (model.filledPos.none { it.isEmpty() }) {
            model.gameStatus = GameStatus.FINISHED
            saveGameModel(model) // Save the draw state immediately
        }
    }

    // Function to fetch games with status CREATED to be displayed in the LobbyScreen
    fun fetchCreatedGames() {
        database.getReference("games")
            .orderByChild("gameStatus")
            .equalTo(GameStatus.CREATED.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val games = mutableListOf<GameModel>()
                    snapshot.children.forEach { child ->
                        val game = child.getValue(GameModel::class.java)
                        if (game != null) {
                            games.add(game)
                        }
                    }
                    _createdGames.postValue(games)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error fetching created games: ${error.message}")
                }
            })
    }
}
