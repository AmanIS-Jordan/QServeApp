package com.example.slaughterhouse.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.slaughterhouse.R
import com.example.slaughterhouse.data.model.BranchesResponse
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.databinding.LoginFragmentBinding
import com.example.slaughterhouse.util.PreferenceManager
import com.example.slaughterhouse.util.Resource
import com.example.slaughterhouse.viewmodel.BranchViewModel
import com.example.slaughterhouse.viewmodel.CountersViewModel
import com.example.slaughterhouse.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var loginViewModel: LoginViewModel
    protected lateinit var branchViewModel: BranchViewModel
    protected lateinit var countersViewModel: CountersViewModel

    var counterId : String ?=null
    var username: String? = null
    var password: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIsloggedin()

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        branchViewModel = ViewModelProvider(this).get(BranchViewModel::class.java)
        countersViewModel = ViewModelProvider(this).get(CountersViewModel::class.java)

        navigationback()

        binding.btnLogin.setOnClickListener {
            username = binding.etName.text.toString()
            password = binding.etPassword.text.toString()

            if (username?.isNotEmpty() == true && password?.isNotEmpty() == true) {
                callLoginApi()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill in both username and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        observeViewModelLogin()
        observeViewModelBranches()


    }

    private fun checkIsloggedin() {
        val isLoggedin = PreferenceManager.isLoggedIn(requireContext())

        if (isLoggedin){
            val username = PreferenceManager.getUserName(requireContext())
            val counterId = PreferenceManager.getCounterId(requireContext())
            val branchCode = PreferenceManager.getSelectedBranch(requireContext())
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToDetailsFragment(counterId ?:"",branchCode?:"",username ?:""))
        }
    }

    private fun callLoginApi() {
        Log.v("username and pass", username + password)

        loginViewModel.login(username ?: "", password ?: "")
    }

    private fun callBranchApi() {
        username?.let { branchViewModel.getBranches(it) }
    }


    private fun callApiWithSelectedBranch(username: String?, branchCode: String?) {
        if (username != null) {
            if (branchCode != null) {
                countersViewModel.getCounter(username, branchCode)
            }
            Log.v("branch code", branchCode.toString())
        }
    }


    private fun observeViewModelCounters(radioGroup: RadioGroup) {
        countersViewModel.result.observe(viewLifecycleOwner) { counterInfo ->
            when (counterInfo) {
                is Resource.Success -> {
                    val counters = counterInfo.data // Replace with your actual data type
                    if (counters != null) {
                        displayCountersAsRadioButtons(counters, radioGroup)
                    }

                    Toast.makeText(requireContext(), "get counters success", Toast.LENGTH_SHORT)
                        .show()
                    Log.v("counter", counterInfo.data.toString())
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "get counters Failed", Toast.LENGTH_SHORT)
                        .show()
                    Log.v("result for the counters", "Error response:")
                }

                is Resource.Loading -> {
                    Log.v("result for the counters", "Loading response")
                }
            }

        }
    }



    private fun observeViewModelBranches() {
        branchViewModel.result.observe(viewLifecycleOwner) { branchInfo ->

            when (branchInfo) {
                is Resource.Success -> {

                    Toast.makeText(requireContext(), "get Branches success", Toast.LENGTH_SHORT)
                        .show()

                    //    branchInfo.data?.let { openBranchesPopup(it) }

                    Log.v("BranchObserver", "Branch data: ${branchInfo.data}")
                    branchInfo.data?.let { openBranchesPopup(it) }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "get Branches Failed", Toast.LENGTH_SHORT)
                        .show()
                    Log.v("result for the branch", "Error response:")
                }

                is Resource.Loading -> {
                    Log.v("result for the branch", "Loading response")
                }
            }
        }
    }


    private fun observeViewModelLogin() {
        loginViewModel.result.observe(viewLifecycleOwner) { userData ->
            when (userData) {
                is Resource.Success -> {

                    Log.v("result for the login", "Success response")

                    callBranchApi()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), userData.message, Toast.LENGTH_SHORT).show()
                    Log.v("result for the login", "Error response:")
                }

                is Resource.Loading -> {
                    Log.v("result for the login", "Loading response")
                }
            }
        }
    }




    private fun openBranchesPopup(branches: List<BranchesResponse?>) {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.branch_popup)

        var selectedBranchCode : String ?=null
        // Initialize views
        val spinner: Spinner = dialog.findViewById(R.id.spinner)
        val cancelButton: Button = dialog.findViewById(R.id.branch_cancel_btn)
        val saveButton: Button = dialog.findViewById(R.id.branch_save_btn)
        val radioGroup: RadioGroup = dialog.findViewById(R.id.counter_radio)

        // Check if branches list is not null
        branches?.let {
            val adapter = object : ArrayAdapter<BranchesResponse>(
                requireContext(),
                R.layout.spinner_item,
                R.id.text_view_spinner_item,
                it
            ) {
                // Customize how each item is displayed in the spinner
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView: TextView = view.findViewById(R.id.text_view_spinner_item)
                    textView.text = it[position]?.BranchNameAr // Set the branch name in the view
                    return view
                }

                // Customize how each item is displayed in the dropdown view
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView: TextView = view.findViewById(R.id.text_view_spinner_item)
                    textView.text =
                        it[position]?.BranchNameAr // Set the branch name in the dropdown
                    return view
                    Log.v("selected name", textView.text.toString())
                }
            }

            // Set the adapter to the spinner
            spinner.adapter = adapter


            val firstBranch = branches.firstOrNull()
            firstBranch?.let {
                Log.d("Selected Branch", "Default branch selected: ${it.BranchNameAr}")
                // Call your API with the first branch
                //    callApiWithSelectedBranch(username, it.BranchCode)

            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedBranch = branches[position]
                    Log.d("Selected Branch", "Branch selected: ${selectedBranch?.BranchNameAr}")

                    selectedBranchCode = selectedBranch?.BranchCode.toString()
                    callApiWithSelectedBranch(username, selectedBranchCode)


                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing when nothing is selected
                }
            }

            observeViewModelCounters(radioGroup)


            // Set click listener for Cancel button
            cancelButton.setOnClickListener {
                dialog.dismiss() // Dismiss the dialog when cancel button is clicked
            }

            saveButton.setOnClickListener {
                // Check if any counter is selected
                val selectedCounterId = radioGroup.checkedRadioButtonId
                if (selectedCounterId == -1) {
                    // No counter selected, show a toast message
                    Toast.makeText(requireContext(), "Please select a counter", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Counter is selected, navigate to another fragment
                    val selectedCounter = dialog.findViewById<RadioButton>(selectedCounterId)
                    val selectedCounterText = selectedCounter.text.toString()

                    // You can use selectedCounterText as needed, for example, log it
                    Log.d("Selected Counter", "Counter selected: $selectedCounterText")


                    //save user info
                   // PreferenceManager.isLoggedIn(requireContext())
                  PreferenceManager.saveUserInfo(requireContext(),username?:"", counterId?:"" , selectedBranchCode?:"", true)

                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToDetailsFragment(
                        counterId?:"",selectedBranchCode?:"" , username ?:""))

                    // Dismiss the dialog
                    dialog.dismiss()
                }

                // Show the dialog
            }
            dialog.show()

        }
    }


    private fun displayCountersAsRadioButtons(
        counters: List<CountersResponse>,
        radioGroup: RadioGroup
    ) {
        radioGroup.removeAllViews() // Clear previous radio buttons


        counters.forEach { counter ->
            val radioButton = RadioButton(requireContext()).apply {
                text = counter.CounterEn // Ensure this is not null
                counterId = counter.CounterId.toString()
                id = View.generateViewId()

            }

            // Add the RadioButton to the RadioGroup
            radioGroup?.addView(radioButton)
            Log.v("radio group", radioGroup.toString())

        }
    }


    private fun navigationback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }


}


