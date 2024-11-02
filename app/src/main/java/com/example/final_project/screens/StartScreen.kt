package com.example.final_project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.final_project.components.TopBar
import com.example.final_project.data.Player
import com.example.final_project.data.Routes
import com.example.final_project.firebase.FirebaseActions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun StartScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    val isValid = name.isNotBlank()

    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                placeholder = { Text("Enter your name!") },
                singleLine = true
            )
            FilledTonalButton(onClick = {
                if (isValid) {
                    // Sign in anonymously
                    auth.signInAnonymously()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign-in success
                                val user = auth.currentUser
                                val userId = user?.uid ?: return@addOnCompleteListener

                                // Create a Player object with uid and name
                                val player = Player(uid = userId, name = name)

                                // Proceed with adding player to the lobby
                                FirebaseActions.addPlayerToLobby(
                                    player = player,
                                    onSuccess = {
                                        navController.navigate(Routes.LobbyScreen)
                                    }
                                )
                            }
                        }
                }
            }) {
                Text("Join Lobby")
            }
        }
    }
}
