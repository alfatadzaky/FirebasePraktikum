package com.example.pamfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditNoteActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDesc: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private val database = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_note) // bisa reuse layout

        etTitle = findViewById(R.id.et_title)
        etDesc = findViewById(R.id.et_description)
        btnUpdate = findViewById(R.id.btn_submit)
        btnDelete = findViewById(R.id.btn_keluar) //  untuk delete

        noteId = intent.getStringExtra("NOTE_ID")
        etTitle.setText(intent.getStringExtra("NOTE_TITLE"))
        etDesc.setText(intent.getStringExtra("NOTE_DESC"))

        btnUpdate.text = "Update"
        btnDelete.text = "Delete"

        btnUpdate.setOnClickListener { updateNote() }
        btnDelete.setOnClickListener { deleteNote() }
    }

    private fun updateNote() {
        val title = etTitle.text.toString()
        val desc = etDesc.text.toString()
        val note = Note(title, desc)
        database.child("notes").child(userId!!).child(noteId!!).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun deleteNote() {
        database.child("notes").child(userId!!).child(noteId!!).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}

