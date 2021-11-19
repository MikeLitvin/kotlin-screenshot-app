package com.example.screenmaker

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.shape.ArcType

class MainController {

    @FXML
    private lateinit var main: BorderPane

    @FXML
    private lateinit var menuBar: MenuBar

    @FXML
    private lateinit var openMI: MenuItem

    @FXML
    private lateinit var saveMI: MenuItem

    @FXML
    private lateinit var saveAsMI: MenuItem

    @FXML
    private lateinit var closeMI: MenuItem

    @FXML
    private lateinit var delaySlider: Slider

    @FXML
    private lateinit var minimizeCheckBox: CheckBox

    @FXML
    private lateinit var screenshotButton: Button

    @FXML
    private lateinit var colorPicker: ColorPicker

    @FXML
    private lateinit var eraserCheckBox: CheckBox

    @FXML
    private lateinit var brushSizeSlider: Slider

    @FXML
    private lateinit var scrollImage: ScrollPane

    @FXML
    private lateinit var imgContainer: StackPane

    @FXML
    private lateinit var img: ImageView

    @FXML
    private lateinit var canvas: Canvas

    private lateinit var gc: GraphicsContext
    private var stage: Stage? = null
    private var isMinimize: Boolean = false
    private val delayDuration get() = delaySlider.value.toLong() * 1000L + 300
    private val brushSize get() = brushSizeSlider.value

    fun initialize() {
        imgContainer.prefWidthProperty().bind(scrollImage.widthProperty())
        imgContainer.prefHeightProperty().bind(scrollImage.heightProperty())
        gc = canvas.graphicsContext2D
        gc.fill = colorPicker.value
        screenshotButton.onAction = EventHandler { onScreenshotButtonClicked() }
        openMI.onAction = EventHandler {
            open()
        }
        canvas.onMousePressed = pressDrawHandler
        canvas.onMouseDragged = dragDrawHandler
    }

    private fun open() {
        openImage()
    }

    // Screenshot tools
    @FXML
    private fun minimizeCheckboxSelected() {
        isMinimize = minimizeCheckBox.isSelected
    }

    @FXML
    private fun onScreenshotButtonClicked() {
        if (isMinimize){
            print("minimize flag  \n")
            stage?.isIconified = true
        }
        print("Delay duration: $delayDuration \n")
        Thread.sleep(delayDuration)
        print("Delay ends \n")
        val robot = Robot()
        val fileName = "/Users/a1/Desktop/screenshots/screenshotapp" + LocalDateTime.now().toString() + ".jpg"

        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val captureRect = Rectangle(0, 0, screenSize.width, screenSize.height)
        val screenFullImage = robot.createScreenCapture(captureRect)

        ImageIO.write(screenFullImage, "png", File(fileName))

        val newImg = SwingFXUtils.toFXImage(ImageIO.read(File(fileName)), null)
        print("screenshot \n")
        setupImageView(newImg)
    }

    private fun setupImageView(image: WritableImage) {
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        canvas.width = 0.0
        canvas.height = 0.0
        img.image = null
        canvas.width = image.width
        canvas.height = image.height
        img.image = image
        imgContainer.maxWidth = image.width
        imgContainer.maxHeight = image.height
        print("Image set up\n")
    }

    fun openImage() {
        val fileChooser = FileChooser()
        val extensionFilters = listOf(
            FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"),
            FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg"),
            FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.jpeg")
        )
        fileChooser.extensionFilters.addAll(extensionFilters)

        val file = fileChooser.showOpenDialog(stage)
        val image =  SwingFXUtils.toFXImage(ImageIO.read(file.inputStream()), null)
        setupImageView(image)
    }

// Paint tools
    @FXML
    fun eraserCheckBoxTouched() {
        if (eraserCheckBox.isSelected) {
            canvas.onMousePressed = pressEraserHandler
            canvas.onMouseDragged = dragEraserHandler
        } else {
            canvas.onMousePressed = pressDrawHandler
            canvas.onMouseDragged = dragDrawHandler
        }
    }

    private var pressDrawHandler = EventHandler { event: MouseEvent ->
        gc.stroke = colorPicker.value
        val x = event.x
        val y = event.y
        canvas.graphicsContext2D.fillArc(x - brushSize / 2, y - brushSize / 2,
            brushSize, brushSize, 0.0, 360.0, ArcType.OPEN)
        gc.moveTo(event.x, event.y)
        gc.beginPath()
    }

    private var dragDrawHandler = EventHandler { event: MouseEvent ->
        gc.stroke = colorPicker.value
        gc.lineWidth = brushSize
        gc.lineTo(event.x, event.y)
        gc.stroke()
    }

    private var pressEraserHandler = EventHandler { event: MouseEvent ->
        gc.clearRect(event.x - brushSize / 2, event.y - brushSize / 2,
            brushSize, brushSize)
    }

    private var dragEraserHandler = EventHandler { event: MouseEvent ->
        gc.clearRect(event.x - brushSize / 2,
            event.y - brushSize / 2,
            brushSize, brushSize)
    }

}