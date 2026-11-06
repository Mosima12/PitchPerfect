package com.pitchperfect.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvRecommendations = view.findViewById<RecyclerView>(R.id.rvRecommendations)
        rvRecommendations.layoutManager = LinearLayoutManager(context)
        val adapter = IdeaAdapter()
        rvRecommendations.adapter = adapter

        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            // 1. Fetch current user's skills
            db.collection("users").document(currentUserId).get()
                .addOnSuccessListener { userDoc ->
                    // Get skills, if empty, assume empty list
                    val mySkills = userDoc.get("skills") as? List<String> ?: emptyList()

                    // 2. Fetch all ideas
                    db.collection("ideas").get().addOnSuccessListener { result ->
                        val ideaList = mutableListOf<BusinessIdea>()
                        for (document in result) {
                            val idea = document.toObject(BusinessIdea::class.java)
                            ideaList.add(idea)
                        }

                        // 3. Show dummy data if empty
                        if (ideaList.isEmpty()) {
                            ideaList.add(BusinessIdea(title = "Student Marketplace", problem = "Students struggle to buy and sell second-hand items affordably within their campus community.", solution = "A mobile-first marketplace exclusive to verified students, enabling secure local trades with built-in escrow.", category = "Development", requiredSkills = listOf("Web", "Marketing", "UI/UX")))
                            ideaList.add(BusinessIdea(title = "Campus EcoTracker", problem = "Track and reduce campus carbon footprint.", solution = "Gamified app for tracking eco-friendly habits.", category = "Prototype", requiredSkills = listOf("Android", "Python", "UI/UX")))
                            ideaList.add(BusinessIdea(title = "StudyBuddy AI", problem = "Students need personalized study help.", solution = "An AI tutor that adapts to your learning style.", category = "Research", requiredSkills = listOf("Kotlin", "AI", "Data")))
                        }

                        // 4. Submit data AND skills to the adapter
                        adapter.submitList(ideaList, mySkills)
                    }
                }
        } else {
            // Fallback if user is somehow not logged in
            adapter.submitList(emptyList(), emptyList())
        }
    }

    // Inner Adapter Class
    inner class IdeaAdapter : RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder>() {
        private var ideas = listOf<BusinessIdea>()
        private var userSkills = listOf<String>()

        fun submitList(newIdeas: List<BusinessIdea>, mySkills: List<String>) {
            ideas = newIdeas
            userSkills = mySkills
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discover_card, parent, false)
            return IdeaViewHolder(view)
        }

        override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
            val idea = ideas[position]
            holder.bind(idea, userSkills)
        }

        override fun getItemCount() = ideas.size

        inner class IdeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(idea: BusinessIdea, mySkills: List<String>) {
                itemView.findViewById<TextView>(R.id.tvTitle).text = idea.title
                itemView.findViewById<TextView>(R.id.tvSubtitle).text = idea.category
                itemView.findViewById<TextView>(R.id.tvDescription).text = idea.problem

                // --- USER DEFINED FEATURE: REAL ALGORITHM ---
                val score = MatchAlgorithm.calculateMatchScore(mySkills, idea.requiredSkills)
                itemView.findViewById<TextView>(R.id.tvMatchScore).text = "$score% MATCH"
                // ---------------------------------------------

                val chipGroup = itemView.findViewById<ChipGroup>(R.id.chipGroup)
                chipGroup.removeAllViews()
                for (skill in idea.requiredSkills) {
                    val chip = Chip(itemView.context)
                    chip.text = skill
                    chipGroup.addView(chip)
                }

                itemView.findViewById<View>(R.id.btnViewIdea).setOnClickListener {
                    val intent = Intent(itemView.context, IdeaDetailsActivity::class.java)
                    intent.putExtra("title", idea.title)
                    intent.putExtra("problem", idea.problem)
                    intent.putExtra("solution", idea.solution)
                    intent.putExtra("creator", "Chantel Diamini")
                    intent.putStringArrayListExtra("skills", ArrayList(idea.requiredSkills))
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}