package com.example.slaughterhouse.ui

import android.app.Dialog
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

    var pendingRadioButton : RadioButton ?=null
    var holdRadioButton : RadioButton ?=null


    private val refreshHandler = Handler() // Handler to schedule tasks
    private val refreshRunnable = object : Runnable {
        override fun run() {


            callPendingTicketsApi()
            // Schedule the handler to run again after 10 minutes
            refreshHandler.postDelayed(this, 5000) // 10 minutes
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

            }
        }

        holdRadioButton?.setOnClickListener {
            if (holdRadioButton?.isChecked == true) {
                // When Hold is selected, uncheck Pending button
                pendingRadioButton?.isChecked = false
                callHoldTicketListAPi(counter, branchCode)
                binding.recyclerview.adapter = null // Clear previous adapter

            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (pendingRadioButton?.isChecked == true) {
            // When Pending is selected, uncheck Hold button
            holdRadioButton?.isChecked = false
            callPendingTicketsApi()
            refreshHandler.removeCallbacks(refreshRunnable)
            binding.recyclerview.adapter = null // Clear previous adapter
        }

    }



    override fun onPause() {
        super.onPause()

        if (pendingRadioButton?.isChecked == true) {
            // When Pending is selected, uncheck Hold button
            holdRadioButton?.isChecked = false
            callPendingTicketsApi()
            refreshHandler.removeCallbacks(refreshRunnable)
            binding.recyclerview.adapter = null // Clear previous adapter
        }
    }

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
                        binding.ticketNumber.text = ""
                        binding.queueName.text = ""

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
        holdTicketAdapter = HoldTicketAdapter(holdTicket, onItemSelected = { ticketInfo ->
            binding.ticketNumber.text = ticketInfo.ticketNo
            binding.queueName.text = ticketInfo.qServiceEn
        },
            recallTicket = { ticket ->
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
        Log.v("ref num" , ticketInfo.refNo?:"")
        Log.v("ref num" , navArgs.username)
        Log.v("ref num" , navArgs.username)
        Log.v("ref num" , navArgs.username)
        Log.v("ref num" , navArgs.username)

        observerRandomCallViewModel()
    }

    private fun observerRandomCallViewModel() {
        randomCallViewModel.result.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Success -> {
                    Log.v("random call response", "success")
                    binding.ticketNumber.text = ""
                    binding.queueName.text = ""
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
                findNavController().navigate(R.id.homeFragment)
                PreferenceManager.clearUrl(requireContext())
                PreferenceManager.setURl(requireContext(), false)
                PreferenceManager.setLoggedIn(requireContext(), false)
                PreferenceManager.clearUserInfo(requireContext())
            }

            binding.tvLogout.id -> {
                findNavController().navigate(R.id.loginFragment)
                PreferenceManager.setLoggedIn(requireContext(), false)
                PreferenceManager.clearUserInfo(requireContext())
            }

        }
    }


}