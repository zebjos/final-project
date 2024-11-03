package com.example.final_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.final_project.components.GameItem
import com.example.final_project.components.TopBar
import com.example.final_project.data.Routes
import com.example.final_project.viewmodel.GameViewModel

@Composable
fun LobbyScreen(navController: NavController) {
    val gameViewModel: GameViewModel = viewModel()
    val createdGames by gameViewModel.createdGames.observeAsState(emptyList())

    // Fetch the created games when the screen is first displayed
    LaunchedEffect(Unit) {
        gameViewModel.fetchCreatedGames()
    }

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(createdGames) { game ->
                GameItem(game) {
                    // When a game is clicked, join the game and navigate to GameScreen
                    gameViewModel.joinOnlineGame(game.gameId)
                    val myID = gameViewModel.myID // Get the myID from GameViewModel
                    navController.navigate("${Routes.GameScreen}/${game.gameId}/$myID")
                }
            }
        }
    }
}