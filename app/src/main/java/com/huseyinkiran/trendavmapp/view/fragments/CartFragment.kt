package com.huseyinkiran.trendavmapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.huseyinkiran.trendavmapp.adapter.CartAdapter
import com.huseyinkiran.trendavmapp.databinding.FragmentCartBinding
import com.huseyinkiran.trendavmapp.model.Product
import java.util.Locale

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchCartItemsFromFirebase()

        cartAdapter = CartAdapter(cartItems)
        binding.cartRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        setupSwipeToDelete()

    }

    private fun fetchCartItemsFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val cartRef = userId?.let { database.getReference("sepetim").child(it) }

        if (userId != null) {
            cartRef?.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItems.clear()
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let { cartItems.add(it) }
                    }
                    cartAdapter.notifyDataSetChanged()
                    calculateTotal()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    private fun calculateTotal() {
        var total = 0.0
        for (product in cartItems) {
            val quantity = product.quantity
            val price = product.price ?: 0.0
            total += price * quantity
        }
        binding.txtTotal.text = String.format(Locale("tr","TR"),"₺ %.2f", total)
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val product = cartItems[position]

                // Firebase'den istenilen ürünü silme işlemi
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val cartRef = userId?.let { FirebaseDatabase.getInstance().getReference("sepetim").child(it) }

                cartRef?.child(product.id.toString())?.removeValue()
                    ?.addOnSuccessListener {
                        Toast.makeText(context, "Ürün sepetten kaldırıldı", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(context, "Silme işlemi başarısız", Toast.LENGTH_SHORT).show()
                    }
                // Local listeden ürünü kaldırma ve adapter'ı güncelleme işlemleri
                cartItems.removeAt(position)
                cartAdapter.notifyItemRemoved(position)
                calculateTotal()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.cartRv)
    }

}