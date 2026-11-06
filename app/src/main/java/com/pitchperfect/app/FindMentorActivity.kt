package com.pitchperfect.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FindMentorActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var adapter: MentorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_mentor)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val rvMentors = findViewById<RecyclerView>(R.id.rvMentors)
        rvMentors.layoutManager = LinearLayoutManager(this)
        adapter = MentorAdapter()
        rvMentors.adapter = adapter

        // Fetch mentors from Firestore
        db.collection("mentors").get().addOnSuccessListener { result ->
            val mentorList = mutableListOf<Mentor>()
            for (doc in result) {
                val mentor = doc.toObject(Mentor::class.java)
                mentorList.add(mentor)
            }

            // Populate dummy data if empty (for the video demo)
            if (mentorList.isEmpty()) {
                mentorList.add(Mentor(name = "Dr Mokoena", role = "Entrepreneurship Mentor", expertise = "Business Strategy, Startup Development", yearsExperience = 15))
                mentorList.add(Mentor(name = "Prof. Nkosi", role = "Technology & Innovation", expertise = "AI, Software Development", yearsExperience = 12))
                mentorList.add(Mentor(name = "Ms. van der Berg", role = "Marketing Expert", expertise = "Marketing, Branding", yearsExperience = 8, availability = "Busy"))

                // Save them to DB so they persist
                for (m in mentorList) {
                    db.collection("mentors").add(m)
                }
            }
            adapter.submitList(mentorList)
        }
    }

    inner class MentorAdapter : RecyclerView.Adapter<MentorAdapter.MentorViewHolder>() {
        private var mentors = listOf<Mentor>()

        fun submitList(newMentors: List<Mentor>) {
            mentors = newMentors
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorViewHolder {
            // Inflate a simple card layout here (or use item_mentor_card.xml if you create it)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mentor_card, parent, false)
            return MentorViewHolder(view)
        }

        override fun onBindViewHolder(holder: MentorViewHolder, position: Int) {
            val mentor = mentors[position]
            holder.bind(mentor)
        }

        override fun getItemCount() = mentors.size

        inner class MentorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(mentor: Mentor) {
                // Assuming you made an item_mentor_card.xml with these IDs
                itemView.findViewById<TextView>(R.id.tvMentorName).text = mentor.name
                itemView.findViewById<TextView>(R.id.tvMentorRole).text = mentor.role
                itemView.findViewById<TextView>(R.id.tvMentorExpertise).text = "Expertise: ${mentor.expertise}"

                val btnRequest = itemView.findViewById<Button>(R.id.btnRequestMentorship)
                if (mentor.availability == "Busy") {
                    btnRequest.isEnabled = false
                    btnRequest.text = "Busy"
                } else {
                    btnRequest.isEnabled = true
                    btnRequest.text = "Request Mentorship"
                }

                btnRequest.setOnClickListener {
                    Toast.makeText(itemView.context, "Mentorship request sent to ${mentor.name}!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}