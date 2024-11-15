package com.huseyinkiran.trendavmapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.huseyinkiran.trendavmapp.R
import com.huseyinkiran.trendavmapp.model.Product
import java.util.Locale

class FavoriteAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.txtProductCategory)
        val title: TextView = itemView.findViewById(R.id.txtProductTitle)
        val rate: TextView = itemView.findViewById(R.id.txtProductRate)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val count: TextView = itemView.findViewById(R.id.txtProductCount)
        val price: TextView = itemView.findViewById(R.id.txtProductPrice)
        val addToCart: Button = itemView.findViewById(R.id.btnAddToCart)
        val imageProduct: ImageView = itemView.findViewById(R.id.imgProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_favorite, parent, false)
        return FavoriteViewHolder(view)
    }
    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = getItem(position)

        holder.apply {
            category.text = product.category
            category.setTextColor(Color.BLACK)
            title.text = product.title
            rate.text = product.rating?.rate.toString()

            ratingBar.rating = product.rating?.rate?.toFloat() ?: 0f
            val progressColor = ContextCompat.getColor(itemView.context, R.color.star_progress)
            ratingBar.progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_ATOP)

            count.text = "(${product.rating?.count})"

            val formattedPrice = String.format(Locale("tr", "TR"), "₺ %.2f", product.price)
            price.text = formattedPrice
            price.setTextColor(Color.RED)

            Glide.with(holder.itemView.context)
                .load(product.image)
                .into(holder.imageProduct)

            addToCart.setOnClickListener {
                addToCart(product)
            }

            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    private fun addToCart(product: Product) {
        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val cartRef = database.getReference("sepetim").child(userId).child(product.id.toString())

            // Önce mevcut ürünün verilerini almak gerekiyor
            cartRef.get().addOnSuccessListener { snapshot ->
                val currentQuantity = snapshot.child("quantity").getValue(Int::class.java) ?: 0

                // Veritabanından alınan currentQuantity değeri her tıklamada artırılır ve newQuantity'e atanır
                val newQuantity = currentQuantity + 1

                // Ürünün diğer bilgileri ve yeni quantity değeri ile güncellenir
                val updatedProduct = product.copy(quantity = newQuantity)

                cartRef.setValue(updatedProduct)
                    .addOnSuccessListener {
                        Log.d("FavoriteAdapter", "Ürün sepete eklendi, quantity: $newQuantity")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FavoriteAdapter", "Ürün sepete eklenemedi: ${e.message}")
                    }
            }.addOnFailureListener { e ->
                Log.e("FavoriteAdapter", "Ürün verisi alınamadı: ${e.message}")
            }
        } else {
            Log.e("FavoriteAdapter", "Kullanıcı giriş yapmamış.")
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}

