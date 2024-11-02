package com.example.final_project.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.data.GameStatus
import com.example.final_project.viewmodel.GameViewModel

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel()
) {
    val gameModel = gameViewModel.gameModel.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = when (gameModel.value?.gameStatus) {
            GameStatus.CREATED -> "Game ID: ${gameModel.value?.gameId}"
            GameStatus.JOINED -> "Click on start game"
            GameStatus.INPROGRESS -> "${gameModel.value?.currentPlayer}'s turn"
            GameStatus.FINISHED -> {
                if (gameModel.value?.winner?.isNotEmpty() == true) "${gameModel.value?.winner} Won"
                else "DRAW"
            }
            else -> ""
        })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { gameViewModel.startGame() },
            enabled = gameModel.value?.gameStatus == GameStatus.JOINED
        ) {
            Text("Start Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Game board
        Column {
            for (row in 0 until 3) {
                Row {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    gameViewModel.onCellClicked(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = gameModel.value?.filledPos?.get(index) ?: "")
                        }
                    }
                }
            }
        }
    }
}