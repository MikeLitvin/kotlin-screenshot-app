package com.example.screenmaker

import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.math.absoluteValue
import kotlin.math.min

class AreaSelector {
    private lateinit var stage: Stage
    private lateinit var layout: Pane

    private var x1 = 0.0
    private var y1 = 0.0
    private var x2 = 0.0
    private var y2 = 0.0

    private val screen = Screen.getPrimary().bounds

    private val selectedArea = Rectangle(0.0, 0.0)
    private val overlay = Rectangle(0.0, 0.0)

    private val onMousePressedHandler = EventHandler { event: MouseEvent ->
        x1 = event.screenX
        y1 = event.screenY
    }

    private val onMouseReleasedHandler = EventHandler { event: MouseEvent ->
        x2 = event.screenX
        y2 = event.screenY
        stage.hide()
    }

    private val onMouseDraggedHandler = EventHandler { event: MouseEvent ->
        selectedArea.x = min(x1, event.screenX)
        selectedArea.y = min(y1, event.screenY)

        selectedArea.width = (x1 - event.screenX).absoluteValue
        selectedArea.height = (y1 - event.screenY).absoluteValue
    }

    private fun calcScreenshotRect(): Rectangle2D {
        val x = min(x1, x2)
        val y = min(y1, y1)
        val width = (x2 - x1).absoluteValue
        val height = (y2 - y1).absoluteValue

        return Rectangle2D(x, y, width, height)
    }

    private fun setupStage() {
        stage = Stage()
        selectedArea.fill = Color.BLACK
        selectedArea.opacity = 0.5

        stage.height = screen.height
        stage.width = screen.width
        stage.initStyle(StageStyle.UNDECORATED)
        stage.isFullScreen = true
    }

    private fun setupOverlay() {
        overlay.width = screen.width
        overlay.height = screen.height
        overlay.fill = Color.BLACK
        overlay.opacity = 0.55
    }

    private fun setupLayout(image: WritableImage) {
        layout = Pane()

        layout.children.add(ImageView(image))
        layout.children.add(selectedArea)
        layout.children.add(overlay)

        layout.prefWidth = screen.width
        layout.prefHeight = screen.height

        layout.onMousePressed = onMousePressedHandler
        layout.onMouseReleased = onMouseReleasedHandler
        layout.onMouseDragged = onMouseDraggedHandler
    }

    fun start(image: WritableImage): Rectangle2D{
        val scene = Scene(layout)

        setupStage()
        setupOverlay()
        setupLayout(image)

        stage.scene = scene
        stage.showAndWait()
        return calcScreenshotRect()
    }
}