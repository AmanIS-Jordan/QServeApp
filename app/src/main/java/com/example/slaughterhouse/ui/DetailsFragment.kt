package com.example.slaughterhouse.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.media.Image
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slaughterhouse.R
import com.example.slaughterhouse.data.model.BranchesResponse
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

    private var selectedTicketInfo: PendingTicketsResponse? = null
    private var dialog: Dialog? = null
    private var isApiInProgress = false
    private var lastSelectedTicketId: String? = null
    private val calledTicketIds = mutableSetOf<String>()

    private val holdCountViewModel: HoldCountViewModel by viewModels()
    private val pendingTicketsViewModel: PendingTicketsViewModel by viewModels()
    private val holdTicketList: HoldTicketsListViewModel by viewModels()
    private val recallTicketViewModel: RecallTicketViewModel by viewModels()
    private val proceedTicketViewModel: ProceedTicketViewModel by viewModels()
    private val skipTicketViewModel: SkipTicketViewModel by viewModels()
    private val holdTicketViewModel: HoldTicketViewModel by viewModels()
    private val getSelectedTicketViewModel: SelectedTicketViewModel by viewModels()
    private val randomCallViewModel: RandomCallViewModel by viewModels()

    private var showProceed : String ?=null
    private var showHold : String ?=null
    private var showReject : String ?=null

    private var lastProcessedBarcode: String? = null
    private var lastScanTime: Long = 0
    private val SCAN_COOLDOWN = 500 // Time in milliseconds to ignore duplicate scans


    var pendingRadioButton: RadioButton? = null
    var holdRadioButton: RadioButton? = null


    private lateinit var barcodeTextView: TextView


    private val refreshHandler = Handler(Looper.getMainLooper()) // Ensure this runs on the main thread
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

    private  var mediaPlayer: MediaPlayer ?=null

    private val SCAN_DELAY = 1500L
    private var scanHandler: Handler = Handler(Looper.getMainLooper())
    private var scanRunnable: Runnable? = null


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

        barcodeTextView = binding.scanText
        barcodeTextView.inputType = InputType.TYPE_NULL

        val counter = navArgs.counter
        val branchCode = navArgs.branchCode



        init()
        callHoldCountAPi()
        callPendingTicketsApi()
        observerHoldCountViewModel()
        observerPendingTicketsViewModel()
        observerHoldTicketListViewModel()
        observerGetSelectedTicketApi()

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


        setFocusViews()
        // Variable to store the last processed barcode
// Variable to store the last processed barcode and the timestamp of the last scan


        barcodeTextView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear any pending runnable to allow the full barcode to complete
                scanRunnable?.let { scanHandler.removeCallbacks(it) }
            }

            override fun afterTextChanged(s: Editable?) {
                // Play sound when new data starts entering
                if (s?.isNotEmpty() == true) {
                    mediaPlayer = MediaPlayer.create(requireContext(), R.raw.beep) // Load sound file
                    playScanSound()
                }

                // Set up a delayed runnable to detect the end of scan input
                scanRunnable = Runnable {
                    s?.let {
                        if (it.isNotEmpty()) {
                            val scannedBarcode = it.toString()

                            // Get the current time
                            val currentTime = System.currentTimeMillis()

                            // Check if the current barcode is the same as the last processed one
                            // and if the cooldown period has not passed
                            if (scannedBarcode != lastProcessedBarcode || (currentTime - lastScanTime) > SCAN_COOLDOWN) {
                                processScanedText(scannedBarcode)

                                // Update last processed barcode and timestamp
                                lastProcessedBarcode = scannedBarcode
                                lastScanTime = currentTime

                                // Temporarily remove the TextWatcher before clearing the text
                                barcodeTextView.removeTextChangedListener(this)
                                barcodeTextView.setText("") // Clear the input
                                barcodeTextView.addTextChangedListener(this)

                                barcodeTextView.requestFocus()

                            }
                        }
                    }
                }
                // Delay to ensure we capture the full scan input before processing
                scanRunnable?.let { runnable ->
                    scanHandler.postDelayed(runnable, SCAN_DELAY)
                }
            }
        })




    }

    private fun setFocusViews() {
        barcodeTextView.requestFocus()

        // Add a listener to restore focus if lost
        barcodeTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                barcodeTextView.post {
                    barcodeTextView.requestFocus()
                }
            }
        }

        // Use ViewTreeObserver to ensure focus stays on barcodeTextView
        view?.viewTreeObserver?.addOnGlobalLayoutListener {
            barcodeTextView.post {
                barcodeTextView.requestFocus()
            }
        }

        // Set other buttons to not receive focus
        binding.tvLogout.isFocusable = false
        binding.tvLogout.isFocusableInTouchMode = false
        binding.setting.isFocusable = false
        binding.setting.isFocusableInTouchMode = false
    }


    private fun processScanedText(scannedData: String) {
        // Format the scanned data based on the specified rules
        val formattedData = when {
            scannedData.length > 1 && scannedData[0].isLetter() -> {
                val firstChar = scannedData[0]
                // Remove all zeros following the first character until the first non-zero number
                val remainingData = scannedData.substring(1).dropWhile { it == '0' }
                "$firstChar$remainingData"
            }
            else -> {
                // No changes needed for other cases
                scannedData
            }
        }

        val isAnyTicketSelected = PreferenceManager.isSelectedTicket(requireContext())

        if (isAnyTicketSelected) {
            val selectedTicket = PreferenceManager.getSelectedTicket(requireContext())
            if (formattedData == selectedTicket) {
                Toast.makeText(
                    requireContext(),
                    "Ticket $formattedData is already selected",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "There is another ticket selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val position = pendingTicketadapter?.ticketList?.indexOfFirst { it.ticketNO == formattedData }
            if (position != null && position != -1) {

                // Select the ticket by calling the adapter method, which handles locking and selection
                pendingTicketadapter?.selectTicketByNumber(formattedData)


                Toast.makeText(
                    requireContext(),
                    "Ticket number $formattedData has been selected",
                    Toast.LENGTH_SHORT
                ).show()

                showStatusPopup(selectedTicketInfo)



            }
            else {
                // Show a message if no matching ticket is found
                Toast.makeText(
                    requireContext(),
                    "Ticket number $formattedData not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun showStatusPopup(selectedTicketInfo: PendingTicketsResponse?) {
        if (dialog?.isShowing == true) return

        dialog = Dialog(requireContext())
        dialog?.setContentView(R.layout.status_popup)


        // Prevent dialog from being dismissed when clicking outside
        dialog?.setCancelable(false)

        val completeBtn: Button? = dialog?.findViewById(R.id.btn_complete)
        val holdBtn: Button? = dialog?.findViewById(R.id.btn_hold)
        val rejectBtn: Button? = dialog?.findViewById(R.id.btn_reject)
        val title: TextView? = dialog?.findViewById(R.id.title)
        val close : ImageView? = dialog?.findViewById(R.id.close)


        completeBtn?.visibility = if (showProceed == "1") View.VISIBLE else View.GONE
        holdBtn?.visibility = if (showHold == "1") View.VISIBLE else View.GONE
        rejectBtn?.visibility = if (showReject == "1") View.VISIBLE else View.GONE

        title?.text = "The Selected Ticket: ${selectedTicketInfo?.ticketNO}"


        fun disableButtons() {
            completeBtn?.isEnabled = false
            holdBtn?.isEnabled = false
            rejectBtn?.isEnabled = false
        }


        completeBtn?.setOnClickListener {

            if (isApiInProgress) return@setOnClickListener
            isApiInProgress = true

            selectedTicketInfo?.let { ticketInfo ->
                disableButtons()  // Disable buttons immediately after the first click


                proceedTicketViewModel.proceedTicket(
                    navArgs.counter,
                    navArgs.branchCode,
                    ticketInfo.ticketNO ?: "",
                    navArgs.username,
                    ticketInfo.id.toString()
                )
                Log.v("complete request", ticketInfo.ticketNO ?: "")
                observerProceedTicketViewModel(ticketInfo)

                dialog?.dismiss()
                dialog= null  // Dismiss the popup when "Complete" is clicked

                isApiInProgress = false

            }
        }

        holdBtn?.setOnClickListener {
            if (isApiInProgress) return@setOnClickListener
            isApiInProgress = true

            selectedTicketInfo?.let { ticketInfo ->

                disableButtons()  // Disable buttons immediately after the first click

                holdTicketViewModel.holdTicket(
                    navArgs.counter,
                    navArgs.branchCode,
                    ticketInfo.ticketNO,
                    navArgs.username,
                    ticketInfo.id.toString()
                )
                Log.v("hold request", ticketInfo.ticketNO)
                observerHoldTicketViewModel(ticketInfo)

                dialog?.dismiss()
                dialog= null  // Dismiss the popup when "Hold" is clicked
                isApiInProgress = false

            }
        }

        rejectBtn?.setOnClickListener {
            selectedTicketInfo?.let { ticketInfo ->
                disableButtons()  // Disable buttons immediately after the first click

                showConfirmationPopup(ticketInfo)

            dialog?.dismiss()  // Dismiss the popup when "Reject" is clicked
        }
        }

        close?.setOnClickListener {
            dialog?.dismiss()
        }


        dialog?.show()
    }


    override fun onResume() {
        super.onResume()
        refreshHandler.post(refreshRunnable) // Start refreshing
        // Set focus to EditText and suppress the keyboard when fragment becomes visible
        binding.scanText.requestFocus()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable) // Stop refreshing
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
                    // Toast.makeText(requireContext(), "recall", Toast.LENGTH_SHORT).show()

                    recallTicketViewModel.recallTicket(
                        navArgs.counter,
                        navArgs.username,
                        ticket.ticketNo,
                        ticket.id,
                        ticket.ticketReferenceNo
                    )

                    observerRecallTicketViewModel(ticket)
                    Log.v("refer num", ticket.ticketReferenceNo)

                    // callPendingTicketsApi()
                    PreferenceManager.saveSelectedTicket(requireContext(), ticket.ticketNo, true)

                    binding.ticketNumber.text = ticket.ticketNo
                    binding.queueName.text = ticket.qServiceEn

                    refreshHandler.removeCallbacks(refreshRunnable) // Clear any existing tasks
                    refreshHandler.post(refreshRunnable) // Schedule the refresh task


                }


            }
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = holdTicketAdapter
    }

    private fun pendingTicketAdapter(pendingTicket: MutableList<PendingTicketsResponse>) {
        pendingTicketadapter = TicketAdapter(pendingTicket,

            onItemSelected = { ticketInfo ->
                Log.v("item selected","select item1")

                if (ticketInfo.id.toString() == lastSelectedTicketId) {
                    Log.v("item selected","select item2")

                    // Ignore this selection if it's the same as the last selected ticket
                    return@TicketAdapter
                }
                lastSelectedTicketId = ticketInfo.id.toString()

                binding.ticketNumber.text = ticketInfo.ticketNO
                binding.queueName.text = ticketInfo.qServiceEn
                Log.v("item selected","select item3")


                if (!calledTicketIds.contains(ticketInfo.id.toString())) {
                    callRandomCallApi(ticketInfo)
                    // Mark this ticket as having triggered the API call
                    calledTicketIds.add(ticketInfo.id.toString())
                    Log.v("item selected","select item4")

                }


                // Lock the selected item by setting its position
                pendingTicketadapter?.lockTicketById(ticketInfo.id.toString())

                // Save the selected ticket in preferences
                PreferenceManager.saveSelectedTicket(requireContext(), ticketInfo.ticketNO, true)
                selectedTicketInfo = ticketInfo
                Log.v("item selected","select item5")

                showStatusPopup(selectedTicketInfo)

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
                Log.v("item selected","select item6")


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
                Log.v("item selected","select item7")

                Log.v("hold request", holdTicket.ticketNO ?: "")
                observerHoldTicketViewModel(holdTicket)

            },
            rejectTicket = { rejectTicket ->
                showConfirmationPopup(rejectTicket)
                Log.v("item selected","select item8")

            } ,
            showProceed = showProceed?:"" ,// Pass the visibility flags
            showHold = showHold ?:"" ,
            showReject = showReject?:""

            )
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


                    showProceed = holdCountReponse.data?.AllowServeNormally ?: "0"
                    showHold = holdCountReponse.data?.AllowHold ?: "0"
                    showReject = holdCountReponse.data?.AllowReject ?: "0"

                    // Initialize adapter with the flags

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
        Log.v("counter", navArgs.counter)
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

                    AlertDialog.Builder(requireContext())
                        .setTitle("Change Setting")
                        .setMessage("Are you sure you want to edit the setting?")
                        .setPositiveButton("Yes") { dialog, _ ->

                            PreferenceManager.clearUrl(requireContext())
                            PreferenceManager.setURl(requireContext(), false)
                            val isadded = PreferenceManager.isAddedURL(requireContext())
                            Log.v("is added url", isadded.toString())
                            PreferenceManager.setLoggedIn(requireContext(), false)
                            PreferenceManager.clearUserInfo(requireContext())

                            findNavController().navigate(R.id.homeFragment)
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

    //    private fun restartApp() {
//        val intent = requireActivity().baseContext.packageManager
//            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
//        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        if (intent != null) {
//            startActivity(intent)
//        }
//        requireActivity().finish()
//        Runtime.getRuntime().exit(0)
//    }
    private fun playScanSound() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.seekTo(0) // Restart sound if it's already playing
        }
        mediaPlayer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Release MediaPlayer resources
    }

}