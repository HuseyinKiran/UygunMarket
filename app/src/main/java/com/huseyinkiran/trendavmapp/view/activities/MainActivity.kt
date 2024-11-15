package com.huseyinkiran.trendavmapp.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.huseyinkiran.trendavmapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var favoritesRef: DatabaseReference
    private lateinit var cartRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navController = Navigation.findNavController(this, R.id.host_fragment)
        NavigationUI.setupWithNavController(bottomNavigation,navController)

        val openCart = intent.getBooleanExtra("openCart", false)
        if (openCart) {
            bottomNavigation.selectedItemId = R.id.cartFragment
        }

        // Firebase referenceların tanımlanmaları yapıldı
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        favoritesRef = database.getReference("favorites").child(userId)
        cartRef = database.getReference("sepetim").child(userId)

        // ürün adetlerinin bottom barlara yansıtılması yapıldı
        setupBadgeListener(favoritesRef, bottomNavigation, R.id.favoritesFragment)
        setupBadgeListener(cartRef, bottomNavigation, R.id.cartFragment)

    }

    private fun setupBadgeListener(ref: DatabaseReference, bottomNavigation: BottomNavigationView, itemId: Int) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemCount = snapshot.childrenCount.toInt()

                val badge = bottomNavigation.getOrCreateBadge(itemId)
                if (itemCount > 0) {
                    badge.isVisible = true
                    badge.number = itemCount
                } else {
                    badge.isVisible = false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}