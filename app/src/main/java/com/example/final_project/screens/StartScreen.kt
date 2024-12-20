package com.example.final_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.final_project.components.TopBar
import com.example.final_project.data.Player
import com.example.final_project.data.Routes
import com.example.final_project.firebase.FirebaseActions
import com.example.final_project.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun StartScreen(navController: NavController) {
    var gameIdInput by remember { mutableStateOf("") } // For entering GameID
    val gameViewModel: GameViewModel = viewModel()

    val buttonWidth = 130.dp

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.size(120.dp))

            Row() {
                // Button to navigate to LobbyScreen
                OutlinedButton(
                    onClick = {navController.navigate(Routes.LobbyScreen) },
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(
                        "Browse Lobbies",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                // Button to create a new lobby
                ElevatedButton(onClick = {
                    gameViewModel.createOnlineGame { gameId, myID ->
                        // Navigate to GameScreen only after the game has been created
                        navController.navigate("${Routes.GameScreen}/$gameId/$myID")
                    }
                },
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(
                        "Create Lobby",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.size(30.dp))

            // Input field for GameID
            OutlinedTextField(
                value = gameIdInput,
                onValueChange = {
                    // Only allow numbers in the input
                    if (it.all { char -> char.isDigit() }) {
                        gameIdInput = it
                    }
                },
                label = { Text("Game ID") },
                placeholder = { Text("Enter the GameID!") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.width(200.dp)
            )

            // Button to join the lobby using the entered GameID
            Button(onClick = {
                if (gameIdInput.isNotEmpty()) {
                    gameViewModel.joinOnlineGame(gameIdInput)
                    val myID = gameViewModel.myID // Get the myID from GameViewModel
                    navController.navigate("${Routes.GameScreen}/$gameIdInput/$myID")
                }
            }) {
                Text(
                    "Join Lobby",
                    style = MaterialTheme.typography.bodyLarge
                    )
            }
        }
    }
}