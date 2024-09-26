//package com.example.slaughterhouse.ui
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.slaughterhouse.databinding.HomeFragmentBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class HomeFragment : Fragment() {
//
//    private lateinit var binding : HomeFragmentBinding
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ItemAdapter
//    private lateinit var editText: EditText
//
//    private val items = listOf(
//        Item("Apple"),
//        Item("Banana"),
//        Item("Orange"),
//        Item("Mango"),
//        Item("Grapes"),
//        Item("Watermelon")
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = HomeFragmentBinding.inflate(inflater,container,false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        recyclerView = binding.recyclerView
//        editText = binding.editText
//
//        adapter = ItemAdapter(items)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//
//        // Add TextWatcher to EditText to update RecyclerView when the user types
//        editText.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Update the highlighted text based on input
//                adapter.updateHighlightedText(s.toString())
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun afterTextChanged(s: Editable?) {}
//        })
//
//    }
//
//
//
//
//}