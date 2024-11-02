package com.example.final_project.firebase

import com.example.final_project.data.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseActions {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Function to add a player to the lobby using the provided name
    fun addPlayerToLobby(player: Player, onSuccess: () -> Unit) {
        val lobbyRef = database.getReference("lobby").push() // Use push() to generate a unique key

        // Add the Player object to the database
        lobbyRef.setValue(player)
            .addOnSuccessListener { onSuccess() }
    }
}