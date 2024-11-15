package com.huseyinkiran.trendavmapp.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.huseyinkiran.trendavmapp.adapter.FavoriteAdapter
import com.huseyinkiran.trendavmapp.databinding.FragmentFavoritesBinding
import com.huseyinkiran.trendavmapp.model.Product
import com.huseyinkiran.trendavmapp.view.activities.ProductDetailActivity

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance()
    private val favoritesRef = userId?.let { database.getReference("favorites").child(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteAdapter { onItemClicked(it) }
        binding.favoritesRv.adapter = favoriteAdapter
        binding.favoritesRv.layoutManager = LinearLayoutManager(context)

        if (userId != null) {
            favoritesRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favoriteProducts = mutableListOf<Product>()
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let { favoriteProducts.add(it) }
                    }
                    favoriteAdapter.submitList(favoriteProducts)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Favori ürünler yüklenemedi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Kullanıcı oturum açmamış", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onItemClicked(product: Product) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID",product.id)
        intent.putExtra("SOURCE", "favorites")
        startActivity(intent)
    }

}