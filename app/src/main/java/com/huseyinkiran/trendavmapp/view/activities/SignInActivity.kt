package com.huseyinkiran.trendavmapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.huseyinkiran.trendavmapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navigateToMainActivity(MainActivity::class.java)
        }

        binding.btnSignIn.setOnClickListener {

            val email = binding.edtTxtEmail.text.toString()
            val password = binding.edtTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navigateToMainActivity(MainActivity::class.java)
                        } else {
                            Toast.makeText(
                                this,
                                "Email ve şifrenizi kontrol ediniz",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("SignInActivity", "SignIn failed: ${task.exception}")
                        }
                    }
            } else {
                Toast.makeText(this, "Email ve şifre alanını doldurunuz.",
                    Toast.LENGTH_SHORT).show()
            }

        }

        binding.txtToSignUp.setOnClickListener {
            navigateToMainActivity(SignUpActivity::class.java)
        }

    }

    private fun navigateToMainActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

}

