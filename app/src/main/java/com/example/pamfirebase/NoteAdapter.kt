package com.example.pamfirebase

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class NoteAdapter(
    private val noteList: MutableList<Pair<String, Note>>,
    private val dbRef: DatabaseReference
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDesc: TextView = view.findViewById(R.id.tv_desc)
        val btnDelete: Button = view.findViewById(R.id.btn_delete)
        val btnEdit: Button = view.findViewById(R.id.btn_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = noteList.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val (key, note) = noteList[position]
        holder.tvTitle.text = note.title
        holder.tvDesc.text = note.description

        holder.btnDelete.setOnClickListener {
            dbRef.child("notes").child(FirebaseAuth.getInstance().uid!!).child(key).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
                    noteList.removeAt(position)
                    notifyItemRemoved(position)
                }
        }

        holder.btnEdit.setOnClickListener {
            val inputTitle = EditText(holder.itemView.context)
            inputTitle.setText(note.title)

            val inputDesc = EditText(holder.itemView.context)
            inputDesc.setText(note.description)

            val layout = LinearLayout(holder.itemView.context)
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(inputTitle)
            layout.addView(inputDesc)

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Edit Note")
                .setView(layout)
                .setPositiveButton("Update") { _, _ ->
                    val updated = Note(inputTitle.text.toString(), inputDesc.text.toString())
                    dbRef.child("notes").child(FirebaseAuth.getInstance().uid!!).child(key)
                        .setValue(updated)
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Updated", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
