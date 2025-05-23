package com.example.pamfirebase

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import com.example.pamfirebase.R
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import com.example.pamfirebase.MainActivity
import com.google.android.gms.tasks.OnSuccessListener
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import android.text.TextUtils
import android.view.View
import android.widget.Button
import com.example.pamfirebase.Note

class InsertNoteActivity : AppCompatActivity(), View.OnClickListener {
    private var tvEmail: TextView? = null
    private var tvUid: TextView? = null
    private lateinit var btnKeluar: Button
    private var mAuth: FirebaseAuth? = null
    private var etTitle: EditText? = null
    private var etDesc: EditText? = null
    private lateinit var btnSubmit: Button
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_note)
        tvEmail = findViewById(R.id.tv_email)
        tvUid = findViewById(R.id.tv_uid)
        btnKeluar = findViewById(R.id.btn_keluar)
        mAuth = FirebaseAuth.getInstance()
        btnKeluar.setOnClickListener(this)
        etTitle = findViewById(R.id.et_title)
        etDesc = findViewById(R.id.et_description)
        btnSubmit = findViewById(R.id.btn_submit)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.reference
        note = Note()
        btnSubmit.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            tvEmail!!.text = currentUser.email
            tvUid!!.text = currentUser.uid
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_keluar -> logOut()
            R.id.btn_submit -> submitData()
        }
    }

    fun logOut() {
        mAuth!!.signOut()
        val intent = Intent(this@InsertNoteActivity, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //makesure user cant go back
        startActivity(intent)
    }

    fun submitData() {
        if (!validateForm()) {
            return
        }
        val title = etTitle!!.text.toString()
        val desc = etDesc!!.text.toString()
        val baru = Note(title, desc)
        databaseReference!!.child("notes").child(mAuth!!.uid!!).push().setValue(baru)
            .addOnSuccessListener(this) {
                Toast.makeText(
                    this@InsertNoteActivity,
                    "Add data",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, ListNoteActivity::class.java))
            }
            .addOnFailureListener(this) {
                Toast.makeText(
                    this@InsertNoteActivity,
                    "Failed to Add data",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(etTitle!!.text.toString())) {
            etTitle!!.error = "Required"
            result = false
        } else {
            etTitle!!.error = null
        }
        if (TextUtils.isEmpty(etDesc!!.text.toString())) {
            etDesc!!.error = "Required"
            result = false
        } else {
            etDesc!!.error = null
        }
        return result
    }
}
