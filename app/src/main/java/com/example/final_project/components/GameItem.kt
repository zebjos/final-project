package com.example.final_project.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.final_project.data.GameModel

@Composable
fun GameItem(game: GameModel, onJoinClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onJoinClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Game ID: ${game.gameId}")
            Text("Status: ${game.gameStatus}")
            Text("Current Player: ${game.currentPlayer}")
        }
    }
}