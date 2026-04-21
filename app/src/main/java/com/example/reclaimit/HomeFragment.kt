package com.example.reclaimit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var db: FirebaseFirestore
    private lateinit var listView: ListView

    private lateinit var searchEt: EditText
    private lateinit var allBtn: Button
    private lateinit var weekBtn: Button
    private lateinit var todayBtn: Button

    private val fullList = mutableListOf<DocumentSnapshot>()
    private val displayList = mutableListOf<DocumentSnapshot>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        listView = view.findViewById(R.id.listView)
        searchEt = view.findViewById(R.id.searchEt)
        allBtn = view.findViewById(R.id.allBtn)
        weekBtn = view.findViewById(R.id.weekBtn)
        todayBtn = view.findViewById(R.id.todayBtn)

        val addBtn = view.findViewById<Button>(R.id.addBtn)

        // ➕ Go to Add Item
        addBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddItemFragment())
                .addToBackStack(null)
                .commit()
        }

        loadItems()

        // 📄 Open Details
        listView.setOnItemClickListener { _, _, position, _ ->
            val doc = displayList[position]

            val intent = Intent(requireContext(), ItemDetailActivity::class.java)
            intent.putExtra("itemName", doc.getString("itemName"))
            intent.putExtra("description", doc.getString("description"))
            intent.putExtra("location", doc.getString("location"))
            intent.putExtra("type", doc.getString("type"))
            intent.putExtra("imageUrl", doc.getString("imageUrl"))
            startActivity(intent)
        }

        // 🔍 SEARCH
        searchEt.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val query = s.toString().lowercase()

                displayList.clear()
                displayList.addAll(
                    fullList.filter {
                        it.getString("itemName")?.lowercase()?.contains(query) == true
                    }
                )

                updateList()
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // 🔘 FILTERS
        allBtn.setOnClickListener {
            displayList.clear()
            displayList.addAll(fullList)
            updateList()
        }

        weekBtn.setOnClickListener {
            val weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)

            displayList.clear()
            displayList.addAll(
                fullList.filter {
                    (it.getLong("timestamp") ?: 0L) >= weekAgo
                }
            )
            updateList()
        }

        todayBtn.setOnClickListener {
            val dayAgo = System.currentTimeMillis() - (24L * 60 * 60 * 1000)

            displayList.clear()
            displayList.addAll(
                fullList.filter {
                    (it.getLong("timestamp") ?: 0L) >= dayAgo
                }
            )
            updateList()
        }
    }

    // 📦 LOAD DATA
    private fun loadItems() {

        db.collection("items")
            .get()
            .addOnSuccessListener { result ->

                fullList.clear()
                displayList.clear()

                for (doc in result) {
                    fullList.add(doc)
                }

                displayList.addAll(fullList)
                updateList()
            }
    }

    // 📱 UI LIST
    private fun updateList() {

        val adapter = object : ArrayAdapter<DocumentSnapshot>(
            requireContext(),
            0,
            displayList
        ) {

            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {

                val view = layoutInflater.inflate(R.layout.item_card, parent, false)

                val doc = displayList[position]

                val titleTv = view.findViewById<TextView>(R.id.titleTv)
                val typeTv = view.findViewById<TextView>(R.id.typeTv)
                val descTv = view.findViewById<TextView>(R.id.descTv)
                val locationTv = view.findViewById<TextView>(R.id.locationTv)

                val type = doc.getString("type") ?: "Lost"

                titleTv.text = doc.getString("itemName") ?: "No Name"
                descTv.text = doc.getString("description") ?: "No Description"
                locationTv.text = "📍 " + (doc.getString("location") ?: "Unknown")

                typeTv.text = type

                if (type == "Lost") {
                    typeTv.setBackgroundColor(android.graphics.Color.parseColor("#E53935"))
                } else {
                    typeTv.setBackgroundColor(android.graphics.Color.parseColor("#43A047"))
                }

                // 🗑 DELETE ON LONG PRESS
                view.setOnLongClickListener {

                    val docId = doc.id

                    db.collection("items").document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Item deleted 🗑️", Toast.LENGTH_SHORT).show()
                            loadItems()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Delete failed 😢", Toast.LENGTH_SHORT).show()
                        }

                    true
                }

                return view
            }
        }

        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }
}