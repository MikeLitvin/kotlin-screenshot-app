package com.example.screenmaker

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.Stage


class MainApplication : Application() {

    private lateinit var scene: Scene
    private lateinit var mainController: MainController

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainApplication::class.java.getResource("main.fxml"))
        scene = Scene(fxmlLoader.load())
        stage.title = "Screenshot App"
        stage.scene = scene
        mainController = fxmlLoader.getController()
        mainController.stage = stage
        stage.show()
        val exitShortcut = KeyCodeCombination(KeyCode.Q,
            KeyCombination.CONTROL_DOWN)
        val saveShortcut = KeyCodeCombination(KeyCode.S,
            KeyCombination.CONTROL_DOWN)
        val fastSaveShortcut = KeyCodeCombination(KeyCode.S,
            KeyCombination.CONTROL_DOWN,
            KeyCombination.SHIFT_DOWN)
        val openShortcut = KeyCodeCombination(KeyCode.O,
            KeyCombination.CONTROL_DOWN)

        stage.scene.accelerators[exitShortcut] = Runnable{
            fxmlLoader.getController<MainController>().close()
        }

        stage.scene.accelerators[openShortcut] = Runnable {
            fxmlLoader.getController<MainController>().openImage()
        }

        stage.scene.accelerators[saveShortcut] = Runnable {
            fxmlLoader.getController<MainController>().saveAs()
        }

        stage.scene.accelerators[fastSaveShortcut] = Runnable {
            fxmlLoader.getController<MainController>().fastSave()
        }
    }
}

fun main() {
    Application.launch(MainApplication::class.java)
}