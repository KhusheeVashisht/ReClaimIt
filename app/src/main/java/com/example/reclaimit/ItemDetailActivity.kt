package com.example.reclaimit

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val imageView = findViewById<ImageView>(R.id.itemImage)
        val nameTv = findViewById<TextView>(R.id.itemName)
        val descTv = findViewById<TextView>(R.id.itemDesc)
        val locationTv = findViewById<TextView>(R.id.itemLocation)
        val typeTv = findViewById<TextView>(R.id.itemType)

        // Getting data from Intent
        val name = intent.getStringExtra("itemName")
        val desc = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val type = intent.getStringExtra("type")
        val imageUrl = intent.getStringExtra("imageUrl")

        nameTv.text = name
        descTv.text = "Description: $desc"
        locationTv.text = "Location: $location"
        typeTv.text = "Type: $type"

        // Load image (Glide = smooth image loading)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }
    }
}