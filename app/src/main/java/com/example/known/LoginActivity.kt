package com.example.known

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If you've logged in before, just send you to the main feed page.
        if (FirebaseRepository.getCurrentUser() != null) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)

        val btnSignIn = findViewById<MaterialButton>(R.id.btnGoogleSignIn)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnSignIn.setOnClickListener {
            btnSignIn.isEnabled = false
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                val result = FirebaseRepository.signInWithGoogle(this@LoginActivity)
                result.onSuccess {
                    goToMain()
                }.onFailure { error ->
                    btnSignIn.isEnabled = true
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Sign-in failed: ${error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Prevents pressing Back to return to Login screen
    }
}