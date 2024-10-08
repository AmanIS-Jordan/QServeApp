package com.example.slaughterhouse.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.slaughterhouse.MainActivity
import com.example.slaughterhouse.R
import com.example.slaughterhouse.databinding.HomeFragmentBinding
import com.example.slaughterhouse.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAddedURL()


        binding.btnSubmit.setOnClickListener {
            checkUrlAndNavigate()
        }

        var storedUrl = PreferenceManager.getUrl(requireContext())
        binding.etUrl.setText(storedUrl)


        // Add touch listener to hide keyboard when clicking outside the EditText
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v = requireActivity().currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        hideKeyboard()
                    }
                }
            }
            false
        }
    }

    private fun checkAddedURL() {
        val isAddedUrl = PreferenceManager.isAddedURL(requireContext())
        Log.v("is saved", isAddedUrl.toString())

        if (isAddedUrl) {
            // Navigate to the Details screen if logged in
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }



    private fun checkUrlAndNavigate() {
        if (isEditTextEmpty(binding.etUrl)) {
            Toast.makeText(requireContext(), "Please fill the URL", Toast.LENGTH_SHORT).show()
        } else {
            val url = binding.etUrl.text.toString().trim()
            // Check if the URL starts with http:// or https:// and ends with /
            if (!(url.startsWith("http://") || url.startsWith("https://")) || !url.endsWith("/")) {
                Toast.makeText(requireContext(), "URL must start with 'http://' or 'https://' and end with '/'", Toast.LENGTH_SHORT).show()
            } else {
                PreferenceManager.saveBaseUrl(requireContext(), url, true)
                PreferenceManager.saveUrl(requireContext(),url,true)


                val isAnyUrlSaved = PreferenceManager.isSavedAnyURL(requireContext())
                if (isAnyUrlSaved) {
                     showConfirmationDialog()
                }




            }
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
     //       .setTitle("save Changes")
            .setMessage("The Added URl has been saved")
            .setPositiveButton("OK") { dialog, _ ->



                // Get the intent to restart the app (main activity)
                val intent = requireActivity().baseContext.packageManager
                    .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                // Restart the app by relaunching the main activity
                if (intent != null) {
                    startActivity(intent)
                }
                // Kill the current process to force the app to restart fresh
                android.os.Process.killProcess(android.os.Process.myPid())



                dialog.dismiss()

                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)

            }
            .show()
    }

    fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireActivity())
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }




    private fun isEditTextEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

}