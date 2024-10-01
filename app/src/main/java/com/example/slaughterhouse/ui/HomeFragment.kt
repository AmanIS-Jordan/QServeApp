package com.example.slaughterhouse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkAddedURL()


        binding.btnSubmit.setOnClickListener {
            checkUrlAndNavigate()
        }

    }

    private fun checkAddedURL() {

        val isAddedUrl = PreferenceManager.isAddedURL(requireContext())

        if (isAddedUrl) {
            // Navigate to the Details screen if logged in
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }



    private fun checkUrlAndNavigate() {
        if (isEditTextEmpty(binding.etUrl)) {
            Toast.makeText(requireContext(), "please fill the URL", Toast.LENGTH_SHORT).show()
        }
        else{
            val url = binding.etUrl.text.toString()
            PreferenceManager.saveBaseUrl(requireContext(),url , true)

            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }



    private fun isEditTextEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

}