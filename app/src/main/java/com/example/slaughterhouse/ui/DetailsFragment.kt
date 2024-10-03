package com.example.slaughterhouse.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slaughterhouse.R
import com.example.slaughterhouse.data.model.HoldTicketsList
import com.example.slaughterhouse.data.model.PendingTicketsResponse
import com.example.slaughterhouse.databinding.DetailsFragmentBinding
import com.example.slaughterhouse.util.PreferenceManager
import com.example.slaughterhouse.util.Resource
import com.example.slaughterhouse.viewmodel.HoldCountViewModel
import com.example.slaughterhouse.viewmodel.HoldTicketViewModel
import com.example.slaughterhouse.viewmodel.HoldTicketsListViewModel
import com.example.slaughterhouse.viewmodel.PendingTicketsViewModel
import com.example.slaughterhouse.viewmodel.ProceedTicketViewModel
import com.example.slaughterhouse.viewmodel.RandomCallViewModel
import com.example.slaughterhouse.viewmodel.RecallTicketViewModel
import com.example.slaughterhouse.viewmodel.SelectedTicketViewModel
import com.example.slaughterhouse.viewmodel.SkipTicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Scanner

@AndroidEntryPoint
class DetailsFragment : Fragment(), OnClickListener {

    private lateinit var binding: DetailsFragmentBinding

    private val holdCountViewModel: HoldCountViewModel by viewModels()
    private val pendingTicketsViewModel: PendingTicketsViewModel by viewModels()
    private val holdTicketList: HoldTicketsListViewModel by viewModels()
    private val recallTicketViewModel: RecallTicketViewModel by viewModels()
    private val proceedTicketViewModel: ProceedTicketViewModel by viewModels()
    private val skipTicketViewModel: SkipTicketViewModel by viewModels()
    private val holdTicketViewModel: HoldTicketViewModel by viewModels()
    private val getSelectedTicketViewModel: SelectedTicketViewModel by viewModels()
    private val randomCallViewModel: RandomCallViewModel by viewModels()

    var pendingRadioButton: RadioButton? = null
    var holdRadioButton: RadioButton? = null

    private lateinit var scanner: Scanner

    private lateinit var barcodeTextView: TextView


    private val refreshHandler = Handler() // Handler to schedule tasks
    private val refreshRunnable = object : Runnable {
        override fun run() {
            callPendingTicketsApi()
            // Schedule the handler to run again after 10 minutes
            refreshHandler.postDelayed(this, 5000) // 5 seconds
        }
    }


    var holdTicketAdapter: HoldTicketAdapter? = null
    var pendingTicketadapter: TicketAdapter? = null

    private val navArgs by navArgs<DetailsFragmentArgs>()










    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailsFragmentBinding.inflate(inflater, container, false)

        barcodeTextView = binding.scanText

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val counter = navArgs.counter
        val branchCode = navArgs.branchCode



        init()
        callHoldCountAPi()
        callPendingTicketsApi()
        observerHoldCountViewModel()
        observerPendingTicketsViewModel()
        observerHoldTicketListViewModel()
        observerGetSelectedTicketApi()


        val radioGroup = binding.filterRadioGroup
        pendingRadioButton = binding.pendingRadioBtn
        holdRadioButton = binding.holdRadioBtn

        pendingRadioButton?.buttonTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        holdRadioButton?.buttonTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))


        pendingRadioButton?.setOnClickListener {
            if (pendingRadioButton?.isChecked == true) {
                // When Pending is selected, uncheck Hold button
                holdRadioButton?.isChecked = false
                callPendingTicketsApi()
                binding.recyclerview.adapter = null // Clear previous adapter
                refreshHandler.postDelayed(refreshRunnable, 5000)

            }
        }

        holdRadioButton?.setOnClickListener {
            if (holdRadioButton?.isChecked == true) {
                // When Hold is selected, uncheck Pending button
                pendingRadioButton?.isChecked = false
                callHoldTicketListAPi(counter, branchCode)
                binding.recyclerview.adapter = null // Clear previous adapter
                refreshHandler.removeCallbacks(refreshRunnable)

            }
        }

    }










//    override fun onResume() {
//        super.onResume()
//        if (pendingRadioButton?.isChecked == true) {
//            refreshHandler.postDelayed(refreshRunnable, 5000)
//        }
//    }
//
//    fun checkIfAnyTicketIsSelected(): Boolean {
//        return PreferenceManager.isSelectedTicket(requireContext())
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//
//        refreshHandler.removeCallbacks(refreshRunnable) // Stop refreshing when fragment is paused
//
//    }

    private fun observerGetSelectedTicketApi() {
        getSelectedTicketViewModel.result.observe(viewLifecycleOwner) { selectedTicket ->
            when (selectedTicket) {
                is Resource.Success -> {
                    Log.v("selectedTicket", selectedTicket.data.toString())


                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "get counters Failed", Toast.LENGTH_SHORT)
                        .show()
                    Log.v("result for the counters", "Error response:")
                }

                is Resource.Loading -> {
                    Log.v("result for the counters", "Loading response")
                }

                else -> {}
            }
        }
    }

    private fun callSelectedTicketApi() {
        getSelectedTicketViewModel.getSelectedTicket(navArgs.counter, navArgs.branchCode)
        Log.v("selectedTicket", navArgs.counter + navArgs.branchCode)

    }

    private fun observerRecallTicketViewModel(ticket: HoldTicketsList) {
        recallTicketViewModel.result.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.v("lists hold recall", "success")
                    val recalledTicketId = ticket.id
                    if (recalledTicketId != null) {
                        holdTicketAdapter?.removeItemById(recalledTicketId)

                        binding.rectangle3.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        binding.statusDescription.text =
                            "Ticket Number ${ticket.ticketNo} has been Recalled"

                        pendingRadioButton?.isChecked = true
                        holdRadioButton?.isChecked = false

                        callPendingTicketsApi()
                        callHoldCountAPi()
//                        binding.ticketNumber.text = ""
//                        binding.queueName.text = ""

                    }
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

    fun clearSelectedTicket() {
        PreferenceManager.clearSelectedTicket(requireContext())
        PreferenceManager.setSelectedTicket(requireContext(), false)

    }


    private fun observerHoldTicketListViewModel() {
        holdTicketList.result.observe(viewLifecycleOwner) { holdList ->

            when (holdList) {
                is Resource.Success -> {
                    Log.v("lists hold", holdList.data.toString())
                    holdList.data?.let { holdTicketAdapter(it.toMutableList()) }

                    binding.ticketNumber.text = ""
                    binding.queueName.text = ""


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

    private fun callHoldTicketListAPi(counter: String, branchCode: String) {
        holdTicketList.getHoldTickets(counter, branchCode)
    }


    private fun observerPendingTicketsViewModel() {
        pendingTicketsViewModel.result.observe(viewLifecycleOwner) { ticketList ->

            when (ticketList) {
                is Resource.Success -> {
                    Log.v("lists", ticketList.data.toString())
                    ticketList.data?.let { pendingTicketAdapter(it.toMutableList()) }



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

    private fun holdTicketAdapter(holdTicket: MutableList<HoldTicketsList>) {
        holdTicketAdapter = HoldTicketAdapter(holdTicket
//         ,   onItemSelected = { ticketInfo ->
//            binding.ticketNumber.text = ticketInfo.ticketNo
//            binding.queueName.text = ticketInfo.qServiceEn
//
//            PreferenceManager.saveSelectedTicket(requireContext(),ticketInfo.ticketNo,true)
//
//
//            callPendingTicketsApi()
//        }
            , recallTicket = { ticket ->

                val isAnyTicketSelected = PreferenceManager.isSelectedTicket(requireContext())

                if (isAnyTicketSelected) {
                    Toast.makeText(
                        requireContext(),
                        "There is another ticket in progress",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "recall", Toast.LENGTH_SHORT).show()

                    recallTicketViewModel.recallTicket(
                        navArgs.counter,
                        navArgs.username,
                        ticket.ticketNo,
                        ticket.id,
                        ticket.ticketReferenceNo
                    )

                    observerRecallTicketViewModel(ticket)
                    Log.v("refer num", ticket.ticketReferenceNo)

                    callPendingTicketsApi()
                    PreferenceManager.saveSelectedTicket(requireContext(), ticket.ticketNo, true)

                    binding.ticketNumber.text = ticket.ticketNo
                    binding.queueName.text = ticket.qServiceEn



                }


            }
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = holdTicketAdapter
    }

    private fun pendingTicketAdapter(pendingTicket: MutableList<PendingTicketsResponse>) {
        pendingTicketadapter = TicketAdapter(pendingTicket,

            onItemSelected = { ticketInfo ->

                binding.ticketNumber.text = ticketInfo.ticketNO
                binding.queueName.text = ticketInfo.qServiceEn
                callRandomCallApi(ticketInfo)

                PreferenceManager.saveSelectedTicket(requireContext(), ticketInfo.ticketNO, true)


            },
            proceedTicket = { ticket ->
                proceedTicketViewModel.proceedTicket(
                    navArgs.counter,
                    navArgs.branchCode,
                    ticket.ticketNO ?: "",
                    navArgs.username,
                    ticket.id.toString()
                )
                Log.v("hold request", ticket.ticketNO ?: "")


                observerProceedTicketViewModel(ticket)
            },

            holdTicket = { holdTicket ->
                holdTicket.ticketNO?.let {
                    holdTicketViewModel.holdTicket(
                        navArgs.counter,
                        navArgs.branchCode,
                        it,
                        navArgs.username,
                        holdTicket.id.toString()
                    )
                }
                Log.v("hold request", holdTicket.ticketNO ?: "")
                observerHoldTicketViewModel(holdTicket)

            },
            rejectTicket = { rejectTicket ->
                showConfirmationPopup(rejectTicket)
            })
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = pendingTicketadapter
    }

    private fun callRandomCallApi(ticketInfo: PendingTicketsResponse) {
        randomCallViewModel.randomCall(
            navArgs.counter,
            navArgs.username,
            ticketInfo.ticketNO ?: "",
            ticketInfo.id.toString(),
            ticketInfo.refNo ?: "",
            navArgs.branchCode
        )
        Log.v("ref num", ticketInfo.refNo ?: "")
        Log.v("ref num", navArgs.username)
        Log.v("ref num", navArgs.username)
        Log.v("ref num", navArgs.username)
        Log.v("ref num", navArgs.username)

        observerRandomCallViewModel()
    }

    private fun observerRandomCallViewModel() {
        randomCallViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Log.v("random call response", "success")
//                    binding.ticketNumber.text = ""
//                    binding.queueName.text = ""
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "get counters Failed", Toast.LENGTH_SHORT)
                        .show()
                    Log.v("random call response", "Error response:")
                }

                is Resource.Loading -> {
                    Log.v("random call response", "Loading response")
                }
            }

        }
    }

    private fun observerHoldTicketViewModel(holdTicket: PendingTicketsResponse) {
        holdTicketViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Log.v("hold ticked response", "success")
                    val recalledTicketId = holdTicket.id
                    if (recalledTicketId != null) {
                        pendingTicketadapter?.removeItemById(recalledTicketId.toString())
                        callHoldCountAPi()

                    }
                    binding.rectangle3.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.yellow
                        )
                    )
                    binding.statusDescription.text =
                        "Ticket Number ${holdTicket.ticketNO} has been Holded"

                    binding.ticketNumber.text = ""
                    binding.queueName.text = ""

                    clearSelectedTicket()
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

    private fun showConfirmationPopup(rejectTicket: PendingTicketsResponse) {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.confirmation_popup)

        val cancelButton: Button = dialog.findViewById(R.id.btn_cancel)
        val saveButton: Button = dialog.findViewById(R.id.btn_save)


        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            skipTicketViewModel.skipTicket(
                navArgs.counter,
                navArgs.branchCode,
                rejectTicket.ticketNO ?: "",
                navArgs.username,
                rejectTicket.id.toString()
            )


            // Dismiss the dialog
            dialog.dismiss()
        }

        // Show the dialog

        dialog.show()
        observerSkipTicketViewModel(rejectTicket)


    }

    private fun observerSkipTicketViewModel(rejectTicket: PendingTicketsResponse) {
        skipTicketViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Log.v("lists skip", "success")
                    val recalledTicketId = rejectTicket.id
                    if (recalledTicketId != null) {
                        pendingTicketadapter?.removeItemById(recalledTicketId.toString())

                    }

                    binding.rectangle3.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    binding.statusDescription.text =
                        "Ticket Number ${rejectTicket.ticketNO} has been Rejected"
                    binding.ticketNumber.text = ""
                    binding.queueName.text = ""

                    clearSelectedTicket()
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


    private fun observerProceedTicketViewModel(ticket: PendingTicketsResponse) {
        proceedTicketViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Log.v("lists proceed recall", "success")
                    val recalledTicketId = ticket.id
                    if (recalledTicketId != null) {
                        pendingTicketadapter?.removeItemById(recalledTicketId.toString())
                    }

                    binding.rectangle3.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                    binding.statusDescription.text =
                        "Ticket Number ${ticket.ticketNO} has been Proceed"

                    binding.ticketNumber.text = ""
                    binding.queueName.text = ""

                    clearSelectedTicket()


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


    private fun callPendingTicketsApi() {
        val counter = navArgs.counter
        val branchCode = navArgs.branchCode
        pendingTicketsViewModel.getPendingTickets(counter, branchCode)
    }

    private fun observerHoldCountViewModel() {
        holdCountViewModel.result.observe(viewLifecycleOwner) { holdCountReponse ->

            when (holdCountReponse) {
                is Resource.Success -> {
                    binding.onHold.text = holdCountReponse.data?.CountHold
                    holdCountReponse.data?.CountHold?.let { Log.v("hold count", it) }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), holdCountReponse.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {
                    Log.v("result for the login", "Loading response")
                }
            }

        }
    }

    private fun init() {
        binding.counterNumber.text = navArgs.counter
        binding.setting.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
        binding.tvName.text = navArgs.username
    }

    private fun callHoldCountAPi() {
        val counter = navArgs.counter
        val branchCode = navArgs.branchCode
        holdCountViewModel.getHoldCount(counter, branchCode)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.setting.id -> {
                val isAnyTicketSelected = PreferenceManager.isSelectedTicket(requireContext())

                if (isAnyTicketSelected) {
                    Toast.makeText(
                        requireContext(),
                        "Sorry, please complete the selected ticked first",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    PreferenceManager.clearUrl(requireContext())
                    PreferenceManager.setURl(requireContext(), false)
                    val isadded = PreferenceManager.isAddedURL(requireContext())
                    Log.v("is added url", isadded.toString())
                    PreferenceManager.setLoggedIn(requireContext(), false)
                    PreferenceManager.clearUserInfo(requireContext())
//                    findNavController().navigate(R.id.homeFragment)

                    AlertDialog.Builder(requireContext())
                        .setTitle("Clear Setting")
                        .setMessage("Are you sure you want to reset the setting?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // Restart the app
                            restartApp()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss() // Do nothing, just close the dialog
                        }
                        .show()
                }
            }

            binding.tvLogout.id -> {
                val isAnyTicketSelected = PreferenceManager.isSelectedTicket(requireContext())

                if (isAnyTicketSelected) {
                    Toast.makeText(
                        requireContext(),
                        "Sorry, please complete the selected ticket to be able to log out.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    findNavController().navigate(R.id.loginFragment)
                    PreferenceManager.setLoggedIn(requireContext(), false)
                    PreferenceManager.clearUserInfo(requireContext())
                }

            }

        }
    }
    private fun restartApp() {
        val intent = requireActivity().baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent != null) {
            startActivity(intent)
        }
        requireActivity().finish()
        Runtime.getRuntime().exit(0)
    }


}