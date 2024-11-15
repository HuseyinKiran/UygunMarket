package com.huseyinkiran.trendavmapp.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.huseyinkiran.trendavmapp.R
import com.huseyinkiran.trendavmapp.databinding.ActivityProductDetailBinding
import com.huseyinkiran.trendavmapp.model.Product
import com.huseyinkiran.trendavmapp.viewmodel.HomeViewModel
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: HomeViewModel by viewModels()
    var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBarTitle = binding.title
        val source = intent.getStringExtra("SOURCE")

        toolBarTitle.text = if (source == "favorites") "Favorilerim" else "Ürünler"

        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId != -1) {
            viewModel.selectProductById(productId)
            Log.d("Product ID : ", "$productId")
        }

        buttonClicked()
        showUI()
        addFavorites()

    }

    private fun checkIfFavorite() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = userId?.let { database.getReference("favorites").child(it) }

        viewModel.selectedProduct.value?.let { product ->
            favoritesRef?.child(product.id.toString())?.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Veritabanında ürün varsa, favori durumunu güncellediğim alan
                    if (snapshot.exists()) {
                        isFavorite = true
                        binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)
                    } else {
                        isFavorite = false
                        binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Favorite Check", "Error checking favorite status: ${error.message}")
                }
            })
        } ?: Log.e("Favorite Check", "No product found.")
    }

    private fun addFavorites() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = userId?.let { database.getReference("favorites").child(it) }

        binding.btnFavorite.setOnClickListener {
            viewModel.selectedProduct.value?.let { product ->
                if (!isFavorite) {
                    binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)
                    favoritesRef?.child(product.id.toString())?.setValue(product)
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Ürün favorilere kaydedildi", Toast.LENGTH_SHORT).show()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(this, "Ürün favorilere kaydedilemedi: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    isFavorite = true
                } else {
                    binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                    favoritesRef?.child(product.id.toString())?.removeValue()
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Ürün favorilerden çıkarıldı", Toast.LENGTH_SHORT).show()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(this, "Ürün favorilerden çıkarılamadı: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    isFavorite = false
                }
            }
        }
    }


    private fun showUI() {
        viewModel.selectedProduct.observe(this) { product ->
            product?.let {
                binding.apply {
                    txtProductTitle.text = it.title

                    txtProductDesc.text = it.description

                    val formattedPrice = String.format(Locale("tr", "TR"), "₺ %.2f", it.price)
                    txtProductPrice.text = formattedPrice
                    txtProductPrice.setTextColor(Color.RED)

                    txtProductCategory.text = it.category
                    txtProductCategory.setTextColor(Color.BLUE)

                    txtProductCount.text = "(${it.rating?.count} Değerlendirme)"

                    txtProductRate.text = it.rating?.rate.toString()

                    ratingBar.rating = it.rating?.rate!!.toFloat()
                    val progressColor =
                        ContextCompat.getColor(this@ProductDetailActivity, R.color.star_progress)
                    ratingBar.progressDrawable.setColorFilter(
                        progressColor,
                        PorterDuff.Mode.SRC_ATOP
                    )

                    Glide.with(this@ProductDetailActivity)
                        .load(it.image)
                        .into(productImage)

                    checkIfFavorite()

                }
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
                        Log.d("ProductDetailActivity", "Ürün sepete eklendi, quantity: $newQuantity")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProductDetailActivity", "Ürün sepete eklenemedi: ${e.message}")
                    }
            }.addOnFailureListener { e ->
                Log.e("ProductDetailActivity", "Ürün verisi alınamadı: ${e.message}")
            }
        } else {
            Log.e("ProductDetailActivity", "Kullanıcı giriş yapmamış.")
        }
    }



    private fun buttonClicked() {

        binding.btnAddToCart.setOnClickListener {
            viewModel.selectedProduct.value?.let { product ->
                addToCart(product)
            }
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.btnCart.setOnClickListener {
            navigateToMainActivity(MainActivity::class.java, openCart = true)
        }

        binding.btnShare.setOnClickListener {
            navigateToMainActivity(MainActivity::class.java)
        }
    }

    private fun navigateToMainActivity(activityClass: Class<*>, openCart: Boolean = false) {
        val intent = Intent(this, activityClass)
        if (openCart) {
            intent.putExtra("openCart", true)
        }
        startActivity(intent)
        finish()
    }

}

