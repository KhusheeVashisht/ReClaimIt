package com.example.reclaimit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddItemFragment : Fragment(R.layout.fragment_add_item) {

    private var imageUri: Uri? = null

    private lateinit var db: FirebaseFirestore

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            view?.findViewById<ImageView>(R.id.imagePreview)?.setImageURI(uri)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val itemNameEt = view.findViewById<EditText>(R.id.itemNameEt)
        val descEt = view.findViewById<EditText>(R.id.descEt)
        val locationEt = view.findViewById<EditText>(R.id.locationEt)
        val typeSpinner = view.findViewById<Spinner>(R.id.typeSpinner)

        val selectImageBtn = view.findViewById<Button>(R.id.selectImageBtn)
        val uploadBtn = view.findViewById<Button>(R.id.uploadBtn)

        val types = arrayOf("Lost", "Found")
        typeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        selectImageBtn.setOnClickListener {
            imagePicker.launch("image/*")
        }

        uploadBtn.setOnClickListener {

            val itemName = itemNameEt.text.toString()
            val desc = descEt.text.toString()
            val location = locationEt.text.toString()
            val type = typeSpinner.selectedItem.toString()
            val userEmail = FirebaseAuth.getInstance().currentUser?.email

            if (itemName.isEmpty() || desc.isEmpty() || location.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 DUPLICATE CHECK FIRST
            db.collection("items")
                .whereEqualTo("itemName", itemName)
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnSuccessListener { result ->

                    if (!result.isEmpty) {
                        Toast.makeText(
                            requireContext(),
                            "You already added this item",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnSuccessListener
                    }

                    // 👉 ACTUAL UPLOAD STARTS HERE

                    val imageUrl = imageUri?.toString() ?: "https://via.placeholder.com/300"

                    val item = hashMapOf(
                        "itemName" to itemName,
                        "description" to desc,
                        "location" to location,
                        "type" to type,
                        "imageUrl" to imageUrl,
                        "userEmail" to userEmail,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("items")
                        .add(item)
                        .addOnSuccessListener {

                            Toast.makeText(
                                requireContext(),
                                "Item uploaded successfully 🎉",
                                Toast.LENGTH_SHORT
                            ).show()

                            view?.postDelayed({
                                parentFragmentManager.popBackStack()
                            }, 300)
                        }
                }
        }
    }
}