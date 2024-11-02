package com.example.final_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.final_project.data.GameModel
import com.example.final_project.data.GameStatus

class GameViewModel : ViewModel() {

    private val _gameModel = MutableLiveData(GameModel())
    val gameModel: LiveData<GameModel> = _gameModel

    fun createOfflineGame() {
        val newGameModel = GameModel(
            gameStatus = GameStatus.JOINED
        )
        _gameModel.value = newGameModel
    }

    fun startGame() {
        _gameModel.value?.let { model ->
            model.gameStatus = GameStatus.INPROGRESS
            _gameModel.value = model
        }
    }

    fun onCellClicked(index: Int) {
        _gameModel.value?.let { model ->
            if (model.gameStatus != GameStatus.INPROGRESS || model.filledPos[index].isNotEmpty()) {
                return
            }

            model.filledPos[index] = model.currentPlayer
            model.currentPlayer = if (model.currentPlayer == "X") "O" else "X"
            checkForWinner(model)
            _gameModel.value = model
        }
    }

    private fun checkForWinner(model: GameModel) {
        val winningPos = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (pos in winningPos) {
            if (model.filledPos[pos[0]] == model.filledPos[pos[1]] &&
                model.filledPos[pos[1]] == model.filledPos[pos[2]] &&
                model.filledPos[pos[0]].isNotEmpty()
            ) {
                model.gameStatus = GameStatus.FINISHED
                model.winner = model.filledPos[pos[0]]
                return
            }
        }

        if (model.filledPos.none { it.isEmpty() }) {
            model.gameStatus = GameStatus.FINISHED
        }
    }
}