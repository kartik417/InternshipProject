package com.example.onlinevotingsystemapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private lateinit var nameET: EditText
    private lateinit var phoneET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var signupBtn: Button
    private lateinit var loginTV: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)


        auth = FirebaseAuth.getInstance()

        // Initialize views
        nameET = findViewById(R.id.et_name)
        phoneET = findViewById(R.id.et_phone)
        emailET = findViewById(R.id.et_email)
        passwordET = findViewById(R.id.et_password)
        signupBtn = findViewById(R.id.btn_signup)
        loginTV = findViewById(R.id.tv_login)



        signupBtn.setOnClickListener {
            val name = nameET.text.toString().trim()
            val phone = phoneET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Auth Signup
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("users")

                        val userMap = hashMapOf<String, Any>(
                            "name" to name,
                            "phone" to phone,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )

                        reference.child(userId!!).setValue(userMap)

                        Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        loginTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}