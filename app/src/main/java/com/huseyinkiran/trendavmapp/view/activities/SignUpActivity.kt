package com.huseyinkiran.trendavmapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.huseyinkiran.trendavmapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {

            val email = binding.edtTxtEmail.text.toString()
            val password = binding.edtTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = auth.currentUser
                            updateUI(user)
                            Toast.makeText(this,"Kullanıcı başarıyla oluşturuldu",Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("SignUpActivity", "Sign-up failed", task.exception)
                            updateUI(null)
                        }
                    }
            } else {
                Log.d("SignUpActivity", "An unexpected error occurred.")
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        navigateToSignInActivity(SignInActivity::class.java)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
           navigateToSignInActivity(SignInActivity::class.java)
        }
    }

    private fun navigateToSignInActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

}