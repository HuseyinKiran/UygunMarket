package com.huseyinkiran.trendavmapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.huseyinkiran.trendavmapp.adapter.HomeAdapter
import com.huseyinkiran.trendavmapp.databinding.FragmentHomeBinding
import com.huseyinkiran.trendavmapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.homeRv

        val displayMetrics = requireActivity().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        val itemWidth = resources.getDimensionPixelSize(com.huseyinkiran.trendavmapp.R.dimen.cell_home_width)
        val spanCount = screenWidth / itemWidth

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        binding.progressBar.visibility = View.VISIBLE

        viewModel.products.observe(viewLifecycleOwner, Observer { productList ->
            val adapter = HomeAdapter(productList)
            recyclerView.adapter = adapter
            binding.progressBar.visibility = View.GONE
        })

        viewModel.fetchProducts()

    }
}
