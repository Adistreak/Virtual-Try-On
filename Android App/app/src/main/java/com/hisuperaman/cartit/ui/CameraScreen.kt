package com.hisuperaman.cartit.ui

import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.hisuperaman.cartit.R
import com.hisuperaman.cartit.ui.components.BackButton
import com.hisuperaman.cartit.ui.components.CameraToggleButton
import com.hisuperaman.cartit.ui.components.ShirtSelector

private var poseLandmarker: PoseLandmarker? = null
private var isUsingFrontCamera = false // Track which camera is being used

private var currentShirtIndex = 0

@Composable
fun CameraScreen(
    shirtImages: List<Int>,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    if (shirtImages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No clothes available to try on, please add clothes to cart first.", modifier = Modifier.padding(16.dp)) // Message when no shirts are available
        }
        return // Exit the composable early
    }

    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isUsingFrontCameraUI by remember { mutableStateOf(false) }

    var currentShirtIndexUI by remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
        setupPoseLandmarker(context) // Initialize only once
        setupCamera(context, lifecycleOwner.value, cameraExecutor, shirtImages, onHandGestureDetect = {newCurrentShirtIndex -> currentShirtIndexUI = newCurrentShirtIndex}) { bitmap ->
            processedBitmap = bitmap
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        processedBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Text("Waiting for frames...", modifier = Modifier.align(Alignment.Center))

        // Camera flip button
        CameraToggleButton(isUsingFrontCameraUI = isUsingFrontCameraUI) {
            isUsingFrontCamera = !isUsingFrontCamera // Toggle camera state
            isUsingFrontCameraUI = !isUsingFrontCameraUI
            setupCamera(context, lifecycleOwner.value, cameraExecutor, shirtImages, onHandGestureDetect = {newCurrentShirtIndex -> currentShirtIndexUI = newCurrentShirtIndex}) { bitmap ->
                processedBitmap = bitmap
            }
        }

        if(showBackButton) {
            BackButton {
                onBackClick()
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ShirtSelector(shirtImages = shirtImages, currentShirtIndex = currentShirtIndexUI) { selectedShirtIndex ->
                Log.d("shirt", "$selectedShirtIndex")
                currentShirtIndexUI = selectedShirtIndex
                currentShirtIndex = selectedShirtIndex
            }
        }
    }
}

private fun setupPoseLandmarker(context: Context) {
    poseLandmarker = PoseLandmarker.createFromFile(context, "pose_landmarker_lite.task").apply {
        run { RunningMode.LIVE_STREAM }
    }
}

private fun setupCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: ExecutorService,
    shirtImages: List<Int>,
    onHandGestureDetect: (Int) -> Unit,
    onFrameProcessed: (Bitmap) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = if (isUsingFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    try {
                        val processedBitmap = processFrame(context, imageProxy, shirtImages, onHandGestureDetect)
                        onFrameProcessed(processedBitmap)
                    } catch (e: Exception) {
                        Log.e("CameraX", "Frame processing error", e)
                    } finally {
                        imageProxy.close()
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)
        } catch (exc: Exception) {
            Log.e("CameraX", "Binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun processFrame(context: Context, imageProxy: ImageProxy, shirtImages: List<Int>, onHandGestureDetect: (Int) -> Unit): Bitmap {
    val bitmap = imageProxyToBitmap(imageProxy)
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees

    // Check if using front camera to determine mirroring
    val isFrontCamera = isUsingFrontCamera  // Reference to the current camera state
    val transformedBitmap = rotateAndMirrorBitmap(bitmap, rotationDegrees, isFrontCamera)

    val inputImage = BitmapImageBuilder(transformedBitmap).build()
    val results = poseLandmarker?.detect(inputImage) ?: return transformedBitmap

    val landmarks = results.landmarks().firstOrNull() ?: return transformedBitmap
    drawHandAndShoulderCircles(transformedBitmap, landmarks)
    drawShirtOverlay(context, transformedBitmap, landmarks, shirtImages)
//    detectGestureForShirtChange(landmarks, transformedBitmap, shirtImages) { newCurrentShirtIdx ->
//        onHandGestureDetect(newCurrentShirtIdx)
//    }

    return transformedBitmap
}


private fun rotateAndMirrorBitmap(bitmap: Bitmap, degrees: Int, mirror: Boolean): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees.toFloat())
        if (mirror) {
            postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) // Mirror horizontally if using front camera
        }
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun drawHandAndShoulderCircles(bitmap: Bitmap, landmarks: List<NormalizedLandmark>) {
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 5f
    }

    val width = bitmap.width.toFloat()
    val height = bitmap.height.toFloat()

    // Draw shoulder circles
    val leftShoulder = landmarks[11]
    val rightShoulder = landmarks[12]
    canvas.drawCircle(leftShoulder.x() * width, leftShoulder.y() * height, 15f, paint)
    canvas.drawCircle(rightShoulder.x() * width, rightShoulder.y() * height, 15f, paint)

    // Draw hand circles
//    val leftHand = landmarks[15]
//    val rightHand = landmarks[16]
//    canvas.drawCircle(leftHand.x() * width, leftHand.y() * height, 10f, paint)
//    canvas.drawCircle(rightHand.x() * width, rightHand.y() * height, 10f, paint)
}



@OptIn(ExperimentalGetImage::class)
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val image = imageProxy.image ?: throw IllegalStateException("ImageProxy has no image")

    // Create an empty RGB bitmap
    val rgbBitmap = Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)

    // Convert YUV to RGB using ImageReader
    val yuvBytes = yuv420ToNv21(image)
    val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, image.width, image.height, null)

    val outputStream = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, outputStream)

    val jpegBytes = outputStream.toByteArray()
    val bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)

    // Copy to ensure proper ARGB_8888 format
    val finalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    return finalBitmap
}

private fun yuv420ToNv21(image: Image): ByteArray {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}

//val fixedRatio = 262 / 190
//val shirtRatioHeightWidth = 581 / 440
private fun drawShirtOverlay(context: Context, bitmap: Bitmap, landmarks: List<NormalizedLandmark>?, shirtImages: List<Int>) {
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    if (landmarks==null) return
    val leftShoulder = landmarks[11]
    val rightShoulder = landmarks[12]

    val width = bitmap.width.toFloat()
    val height = bitmap.height.toFloat()

    // Convert relative positions to pixel coordinates
    val x1 = leftShoulder.x() * width
    val y1 = leftShoulder.y() * height
    val x2 = rightShoulder.x() * width
    val y2 = rightShoulder.y() * height

    // Calculate shoulder width and midpoint
    val shirtRatioHeightWidth = 1.5
    val fixedRatio = 1.4
    val shoulderWidth = (x2 - x1) * fixedRatio
    val midX = (x1 + x2) / 2
    val midY = (y1 + y2) / 2

    val shoulderHeight = (shoulderWidth.toInt() * shirtRatioHeightWidth).toInt()

    // Load shirt bitmap
    val shirtBitmap = BitmapFactory.decodeResource(context.resources, shirtImages[currentShirtIndex])
    val scaledShirt = Bitmap.createScaledBitmap(shirtBitmap, shoulderWidth.toInt(), shoulderHeight, true)

    // Flip the shirt both horizontally and vertically
    val matrix = Matrix().apply {
        postScale(-1f, -1f, scaledShirt.width / 2f, scaledShirt.height / 2f)
    }
    val mirroredFlippedShirt = Bitmap.createBitmap(scaledShirt, 0, 0, scaledShirt.width, scaledShirt.height, matrix, true)

    // Position the shirt centered on shoulders
    val shirtX = midX - mirroredFlippedShirt.width / 2
    val shirtY = midY - mirroredFlippedShirt.height / 7  // Adjust slightly above shoulders

    // Draw mirrored and flipped shirt on the image
    canvas.drawBitmap(mirroredFlippedShirt, shirtX, shirtY, paint)
}

//
//private var counterRight = 0
//private var counterLeft = 0
//private const val SELECTION_SPEED = 5
//
//fun detectGestureForShirtChange(landmarks: List<NormalizedLandmark>, bitmap: Bitmap, shirtImages: List<Int>, onHandGestureDetect: (Int) -> Unit) {
//    val rightHandX = landmarks[16].x()
//    val leftHandX = landmarks[15].x()
//
//    val imageWidth = bitmap.width
//    if (rightHandX < imageWidth * 0.2) { // Right hand gesture (move left → previous)
//        counterRight++
//        if (counterRight * SELECTION_SPEED > 360) {
//            counterRight = 0
//            if (currentShirtIndex > 0) { // Change to previous
//                currentShirtIndex--
//                onHandGestureDetect(currentShirtIndex)
//            }
//        }
//    } else if (leftHandX > imageWidth * 0.8) { // Left hand gesture (move right → next)
//        counterLeft++
//        if (counterLeft * SELECTION_SPEED > 360) {
//            counterLeft = 0
//            if (currentShirtIndex < shirtImages.size - 1) { // Change to next
//                currentShirtIndex++
//                onHandGestureDetect(currentShirtIndex)
//            }
//        }
//    }
//
// else {
//        counterRight = 0
//        counterLeft = 0
//    }
//}
