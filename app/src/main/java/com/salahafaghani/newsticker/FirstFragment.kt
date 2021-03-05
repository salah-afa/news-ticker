package com.salahafaghani.newsticker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.salahafaghani.newsticker.databinding.Fragment1Binding

class FirstFragment: Fragment (R.layout.fragment_1) {
    private lateinit var binding: Fragment1Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = Fragment1Binding.bind(view)

        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.buttonFragment2.setOnClickListener {
            findNavController().navigate(
                FirstFragmentDirections.actionFirstFragmentToSecondFragment()
            )
        }

        binding.buttonFragment3.setOnClickListener {
            findNavController().navigate(
                FirstFragmentDirections.actionFirstFragmentToThirdFragment()
            )
        }

        binding.buttonAddNews.setOnClickListener {
            binding.editNews.hideKeyboard()
            if (binding.editNews.text.isNullOrBlank()) {
                Snackbar.make(it, "Can not add empty news.", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.addNews(binding.editNews.text.toString())
                binding.editNews.apply {
                    setText("")
                }
                Snackbar.make(it, "News has added successfully.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.buttonDeleteNews.setOnClickListener {
            if (viewModel.getNumberOfNews() == 0) {
                Snackbar.make(it, "No news to delete.", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.deleteFirstNews()
                Snackbar.make(it, "News has deleted and remains ${viewModel.getNumberOfNews()}.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}