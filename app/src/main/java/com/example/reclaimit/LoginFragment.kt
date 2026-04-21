package com.example.reclaimit

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val emailEt = view.findViewById<EditText>(R.id.emailEt)
        val passwordEt = view.findViewById<EditText>(R.id.passwordEt)
        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        val signupText = view.findViewById<TextView>(R.id.signupText)

        loginBtn.setOnClickListener {

            val email = emailEt.text.toString()
            val password = passwordEt.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Enter email and password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    Toast.makeText(
                        requireContext(),
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Move to HomeFragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        requireContext(),
                        "Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        signupText.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignupFragment())
                .commit()
        }
    }
}