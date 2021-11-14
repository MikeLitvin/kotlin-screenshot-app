package com.example.screenmaker

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.Stage

class MainApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainApplication::class.java.getResource("main.fxml"))
        val scene = Scene(fxmlLoader.load())
        stage.title = "Screenshot App"
        stage.scene = scene
        stage.show()
        val exitShortcut = KeyCodeCombination(KeyCode.Q,
            KeyCombination.CONTROL_DOWN)
        val saveShortcut = KeyCodeCombination(KeyCode.S,
            KeyCombination.CONTROL_DOWN)
        val fastSaveShortcut = KeyCodeCombination(KeyCode.S,
            KeyCombination.CONTROL_DOWN,
            KeyCombination.SHIFT_DOWN)

//        stage.scene.accelerators[exitShortcut] = Runnable {
//            Platform.exit()
//        }
    }
}

fun main() {
    Application.launch(MainApplication::class.java)
}