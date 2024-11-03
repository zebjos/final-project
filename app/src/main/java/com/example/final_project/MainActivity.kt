package com.example.final_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.final_project.data.Routes
import com.example.final_project.screens.GameScreen
import com.example.final_project.screens.LobbyScreen
import com.example.final_project.screens.StartScreen
import com.example.final_project.ui.theme.AppTheme
import com.example.final_project.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.StartScreen
                ) {
                    composable(Routes.StartScreen) {
                        StartScreen(navController)
                    }

                    composable(Routes.LobbyScreen) {
                        LobbyScreen(navController)
                    }

                    // Pass gameId and myID as arguments to GameScreen
                    composable(
                        route = "${Routes.GameScreen}/{gameId}/{myID}",
                        arguments = listOf(
                            navArgument("gameId") { type = NavType.StringType },
                            navArgument("myID") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                        val myID = backStackEntry.arguments?.getString("myID") ?: ""
                        val gameViewModel: GameViewModel = viewModel()

                        // Set the myID in the ViewModel
                        gameViewModel.myID = myID

                        GameScreen(navController, gameId, gameViewModel)
                    }
                }
            }
        }
    }
}