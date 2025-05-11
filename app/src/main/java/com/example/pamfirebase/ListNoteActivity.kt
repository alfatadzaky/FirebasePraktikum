package com.example.pamfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListNoteActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteList: MutableList<Pair<String, Note>>
    private lateinit var adapter: NoteAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_note)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        recyclerView = findViewById(R.id.rv_notes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        noteList = mutableListOf()
        adapter = NoteAdapter(noteList, dbRef)
        recyclerView.adapter = adapter

        fetchNotes()
    }

    private fun fetchNotes() {
        val uid = auth.uid ?: return
        dbRef.child("notes").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                for (data in snapshot.children) {
                    val note = data.getValue(Note::class.java)
                    if (note != null) {
                        noteList.add(Pair(data.key!!, note))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListNoteActivity, "Failed to read data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
