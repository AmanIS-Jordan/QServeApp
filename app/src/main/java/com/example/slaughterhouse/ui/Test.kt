//package com.example.slaughterhouse.ui
//
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.annotation.OptIn
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ExperimentalGetImage
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.example.slaughterhouse.databinding.TestBinding
//import com.google.mlkit.vision.barcode.BarcodeScanning
//import com.google.mlkit.vision.common.InputImage
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class Test : Fragment() {
//
//    private lateinit var binding: TestBinding
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var barcodeTextView: TextView
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = TestBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Reference to the TextView where the scanned barcode text will be displayed
//        barcodeTextView = binding.barcodeTextView
//
//        // Initialize the camera executor service
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        // Start the camera without a visible preview
//        startCamera()
//    }
//
//    private fun startCamera() {
//        // Get the camera provider
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            // Configure image analysis
//            val imageAnalysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//
//            // Set the analyzer for the image analysis use case
//            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
//                processImageProxy(imageProxy)
//            }
//
//            // Select the back camera
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                // Bind the image analysis use case to the fragment lifecycle
//                cameraProvider.bindToLifecycle(
//                    viewLifecycleOwner, cameraSelector, imageAnalysis
//                )
//            } catch (exc: Exception) {
//                Log.e("CameraError", "Camera binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }
//
//    @OptIn(ExperimentalGetImage::class)
//    private fun processImageProxy(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage != null) {
//            // Create InputImage from ImageProxy
//            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//            val scanner = BarcodeScanning.getClient()
//
//            // Process the image using the barcode scanner
//            scanner.process(inputImage)
//                .addOnSuccessListener { barcodes ->
//                    if (barcodes.isEmpty()) {
//                        Log.d("BarcodeScanner", "No barcodes detected")
//                    } else {
//                        for (barcode in barcodes) {
//                            val scannedText = barcode.rawValue ?: "No Text Found"
//                            Log.d("BarcodeScanner", "Scanned Text: $scannedText")
//
//                            // Update the TextView with the scanned text on the main thread
//                            Handler(Looper.getMainLooper()).post {
//                                barcodeTextView.text = scannedText
//                            }
//                        }
//                    }
//                }
//                .addOnFailureListener {
//                    Log.e("BarcodeScanner", "Error processing barcode", it)
//                }
//                .addOnCompleteListener {
//                    imageProxy.close() // Always close the image proxy
//                }
//        } else {
//            imageProxy.close() // Close the image proxy if mediaImage is null
//            Log.d("BarcodeScanner", "No media image available")
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        // Shut down the camera executor to prevent memory leaks
//        cameraExecutor.shutdown()
//    }
//}
