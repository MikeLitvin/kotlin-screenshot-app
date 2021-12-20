package com.example.screenmaker

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
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
import javafx.scene.SnapshotParameters
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.stage.DirectoryChooser

class MainController {

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
    var stage: Stage? = null
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
            openImage()
        }
        saveAsMI.onAction = EventHandler {
            stage?.let { it1 -> saveImage(false, img, canvas, it1) }
        }
        saveMI.onAction = EventHandler {
            stage?.let { it1 -> saveImage(true, img, canvas, it1) }
        }
        closeMI.onAction = EventHandler {
            close()
        }
        canvas.onMousePressed = pressDrawHandler
        canvas.onMouseDragged = dragDrawHandler
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

        ImageIO.write(screenFullImage, "jpg", File(fileName))

        val newImg = SwingFXUtils.toFXImage(ImageIO.read(File(fileName)), null)
        print("screenshot \n")
        setupImageView(newImg)
        stage?.isIconified = false
    }

    @FXML
    fun saveImage(isQuick: Boolean, imgC: ImageView, drawC: Canvas, stage: Stage) {
        val fileName =  "screanshot" + LocalDateTime.now().toString() + ".jpg"
        val file: File
        if (isQuick) {
            file = File("/Users/a1/Desktop/screenshots/screenshotapp" + fileName)
        } else {
            val directoryChooser = DirectoryChooser()
            val dir = directoryChooser.showDialog(stage) ?: return
            file = File(dir.toString() + fileName)
        }
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        val snapImg = imgC.snapshot(params, null)
        val snapDraw = drawC.snapshot(params, null)
        val result = Canvas(drawC.width, drawC.height)
        val resultCtx = result.graphicsContext2D
        resultCtx.drawImage(snapImg, 0.0, 0.0)
        resultCtx.drawImage(snapDraw, 0.0, 0.0)
        ImageIO.write(SwingFXUtils.fromFXImage(result.snapshot(params, null), null), "png", file)
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

    fun saveAs(){
        stage?.let { it1 -> saveImage(false, img, canvas, it1) }
    }

    fun fastSave(){
        stage?.let { it1 -> saveImage(true, img, canvas, it1) }
    }

    fun close(){
        Platform.exit()
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
        gc.clearRect(event.x - brushSize / 2, event.y - brushSize / 2,
            brushSize, brushSize)
    }

}