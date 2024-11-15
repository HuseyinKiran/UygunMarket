package com.huseyinkiran.trendavmapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huseyinkiran.trendavmapp.R
import com.huseyinkiran.trendavmapp.view.activities.ProductDetailActivity
import com.huseyinkiran.trendavmapp.model.Product
import java.util.Locale

class HomeAdapter(private val productList : List<Product>) : RecyclerView.Adapter<HomeAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val productTitle : TextView = itemView.findViewById(R.id.txtTitle)
        val rating : RatingBar = itemView.findViewById(R.id.ratingBar)
        val commentCount : TextView = itemView.findViewById(R.id.txtCommentCount)
        val productPrice : TextView = itemView.findViewById(R.id.txtPrice)
        val productCard : CardView = itemView.findViewById(R.id.product_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_home, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.productTitle.text = product.title

        val formattedPrice = String.format(Locale("tr", "TR"), "₺ %.2f", product.price)
        holder.productPrice.text = formattedPrice
        holder.productPrice.setTextColor(Color.RED)

        holder.commentCount.text = "(${product.rating?.count})"

        Glide.with(holder.imgProduct.context)
            .load(product.image)
            .into(holder.imgProduct)

        holder.rating.rating = product.rating?.rate!!.toFloat()
        holder.rating.setIsIndicator(true)

        val progressColor = ContextCompat.getColor(holder.rating.context, R.color.star_progress)
        holder.rating.progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_ATOP)

        holder.productCard.setOnClickListener {
            val context = holder.productCard.context
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID",product.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return productList.size
    }

}

