package com.example.slaughterhouse.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slaughterhouse.R
import com.example.slaughterhouse.databinding.DetailsFragmentBinding
import com.example.slaughterhouse.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment () : Fragment(){

    private lateinit var binding: DetailsFragmentBinding
    private var ticketAdapter: TicketAdapter? = null
    private lateinit var item: List<Item>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DetailsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item=createTestData()
       initAdapter(item)

        binding.setting.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
            PreferenceManager.clearUrl(requireContext())
            PreferenceManager.setLoggedIn(requireContext(), false)

        }
        val radioGroup= binding.filterRadioGroup
        val pendingRadioButton = binding.pendingRadioBtn
        val holdRadioButton = binding.holdRadioBtn

        pendingRadioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        holdRadioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))


        pendingRadioButton.setOnClickListener {
            if (pendingRadioButton.isChecked) {
                // When Pending is selected, uncheck Hold button
                holdRadioButton.isChecked = false
            }
        }

        holdRadioButton.setOnClickListener {
            if (holdRadioButton.isChecked) {
                // When Hold is selected, uncheck Pending button
                pendingRadioButton.isChecked = false
            }
        }
    }

    private fun createTestData(): List<Item> {
        // Creating a list of Names objects with default values or custom values
        val namesList1 = listOf(
            Names(name1 = "custom1", name2 = "custom2", name3 = "custom3"), // Default values: name1 = "test", name2 = "test", name3 = "test"
            Names(name1 = "custom1", name2 = "custom2", name3 = "custom3") // Custom values
        )

        val namesList2 = listOf(
            Names(name1 = "example1", name2 = "example2", name3 = "example3"),
            Names(name1 = "example4", name2 = "example5", name3 = "example6")
        )

        // Return a list of Item objects, each containing a list of Names objects
        return listOf(
            Item(namesList1),
            Item(namesList2)
        )
    }

    private fun initAdapter(items:List<Item>) {
        val namesList = items.flatMap { it.name }
        ticketAdapter = context?.let { TicketAdapter(namesList) }
        binding?.recyclerview?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerview?.adapter = ticketAdapter
    }

}