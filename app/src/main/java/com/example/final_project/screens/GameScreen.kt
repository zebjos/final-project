package com.example.final_project.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.Button
import androidx.compose.material3.Text


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.final_project.components.TopBar
import com.example.final_project.data.GameStatus
import com.example.final_project.data.Routes
import com.example.final_project.viewmodel.GameViewModel

@Composable
fun GameScreen(navController: NavController, gameId: String, gameViewModel: GameViewModel = viewModel()) {
    // Fetch the game model only if needed
    if (gameViewModel.gameModel.value == null) {
        gameViewModel.fetchGameModel(gameId)
    }

    val gameModel = gameViewModel.gameModel.observeAsState()
    val context = LocalContext.current

    // Add a state variable to force recomposition
    val forceRecomposition = remember { mutableStateOf(0) }

    // Change this variable to force recomposition
    gameModel.value?.let {
        forceRecomposition.value += 1
    }

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (gameModel.value?.gameStatus) {
                    GameStatus.CREATED -> "Game ID: ${gameModel.value?.gameId}"
                    GameStatus.INPROGRESS -> "${gameModel.value?.currentPlayer} Turn"
                    GameStatus.FINISHED -> "" // Handle the winner separately to display it under the game board
                    else -> ""
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Game board
            Column {
                for (row in 0 until 3) {
                    Row {
                        for (col in 0 until 3) {
                            val index = row * 3 + col
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant) // To match the cards color
                                    .size(100.dp)
                                    .border(1.dp, Color.Blue)
                                    .clickable {
                                        // Check if it's your turn
                                        if (gameModel.value?.currentPlayer == gameViewModel.myID) {
                                            println("It's your turn! Making a move at index: $index")
                                            gameViewModel.onCellClicked(index)
                                            // Force a recomposition after the move
                                            forceRecomposition.value += 1
                                        } else {
                                            println("Not your turn. currentPlayer: ${gameModel.value?.currentPlayer}, myID: ${gameViewModel.myID}")
                                            Toast.makeText(
                                                context,
                                                "Not your turn",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = gameModel.value?.filledPos?.get(index) ?: "")
                            }
                        }
                    }
                }
            }

            if (gameModel.value?.gameStatus == GameStatus.FINISHED) {
                val winnerMessage = if (gameModel.value?.winner?.isNotEmpty() == true) {
                    "${gameModel.value?.winner} Won"
                } else {
                    "DRAW"
                }
                Text(text = winnerMessage)
                ElevatedButton(
                    onClick = {navController.navigate(Routes.StartScreen) },
                ) {
                    Text(
                        "Back to Start",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}



