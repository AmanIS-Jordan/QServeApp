package com.example.slaughterhouse.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.slaughterhouse.databinding.TestFragmentBinding

class TestFragment : Fragment() {

    private lateinit var binding: TestFragmentBinding
    private lateinit var barcodeEditText: EditText
    private val scannedData = StringBuilder() // Buffer to store the entire barcode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TestFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeEditText = binding.etScan

        // Prevent the soft keyboard from appearing
        barcodeEditText.showSoftInputOnFocus = false

        // Ensure EditText is focused to capture input
        barcodeEditText.requestFocus()

        // Handle key events for barcode scanning
        barcodeEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                Log.d("TestFragment", "Key pressed: ${event.displayLabel} KeyCode: $keyCode")

                // Check for Enter key to end the scan
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Display the full scanned barcode in EditText
                    barcodeEditText.setText(scannedData.toString())
                    Log.d("TestFragment", "Final scanned barcode: ${scannedData.toString()}")

                    // Clear the buffer for the next scan
                    scannedData.clear()
                    true
                } else {
                    // Append character to buffer if it's a valid character
                    if (!event.isShiftPressed && event.unicodeChar != 0) {
                        scannedData.append(event.displayLabel)
                        Log.d("TestFragment", "Current scanned data: $scannedData")
                    }
                    true
                }
            } else {
                false
            }
        }
    }
}
