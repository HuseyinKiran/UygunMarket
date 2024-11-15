package com.huseyinkiran.trendavmapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.huseyinkiran.trendavmapp.R
import com.huseyinkiran.trendavmapp.model.Product
import java.util.Locale

class CartAdapter(private val cartItems: List<Product>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val quantities = mutableMapOf<String, Int>()

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtProductTitle)
        val price: TextView = itemView.findViewById(R.id.txtProductPrice)
        val imageProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val txtQuantity: TextView = itemView.findViewById(R.id.txtQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val product = cartItems[position]
        val productId = product.id.toString()

        if (!quantities.containsKey(productId)) {
            quantities[productId] = product.quantity
        }

        holder.title.text = product.title

        val currentQuantity = quantities[productId] ?: product.quantity
        holder.txtQuantity.text = currentQuantity.toString()

        val totalPrice = product.price?.times(currentQuantity)
        val formattedPrice = String.format(Locale("tr", "TR"), "₺ %.2f", totalPrice)
        holder.price.text = formattedPrice
        holder.price.setTextColor(Color.RED)


        Glide.with(holder.itemView.context)
            .load(product.image)
            .into(holder.imageProduct)

        holder.btnIncrease.setOnClickListener {
            quantities[productId] = currentQuantity + 1
            holder.txtQuantity.text = quantities[productId].toString()
            updateFirebaseQuantity(product, quantities[productId]!!)
        }

        holder.btnDecrease.setOnClickListener {
            if (currentQuantity > 1) {
                quantities[productId] = currentQuantity - 1
                holder.txtQuantity.text = quantities[productId].toString()
                updateFirebaseQuantity(product, quantities[productId]!!)
            } else {
                quantities.remove(productId)
                removeFromFirebaseCart(product,holder.itemView.context)
            }
        }

    }

    override fun getItemCount(): Int = cartItems.size

    private fun updateFirebaseQuantity(product: Product, quantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val cartRef = FirebaseDatabase.getInstance().getReference("sepetim").child(userId).child(product.id.toString())
            cartRef.child("quantity").setValue(quantity)
        }
    }

    private fun removeFromFirebaseCart(product: Product, context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val cartRef = FirebaseDatabase.getInstance().getReference("sepetim").child(userId).child(product.id.toString())
            cartRef.removeValue().addOnSuccessListener {
                Toast.makeText(context,"Seçtiğiniz ürün sepetten kaldırıldı",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                e.message
            }
        }
    }

}


