package com.pitchperfect.app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class IdeaDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_details)

        // Handle back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Get data from the Intent
        val title = intent.getStringExtra("title") ?: "No Title"
        val problem = intent.getStringExtra("problem") ?: "No Problem Stated"
        val solution = intent.getStringExtra("solution") ?: "No Solution Stated"
        val creator = intent.getStringExtra("creator") ?: "Unknown Creator"
        val skills = intent.getStringArrayListExtra("skills") ?: arrayListOf()

        // Bind views
        findViewById<TextView>(R.id.tvIdeaTitle).text = title
        findViewById<TextView>(R.id.tvCreatorInfo).text = "Created by $creator"
        findViewById<TextView>(R.id.tvProblem).text = problem
        findViewById<TextView>(R.id.tvSolution).text = solution

        // Populate skills chips
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupSkills)
        for (skill in skills) {
            val chip = Chip(this)
            chip.text = skill
            chipGroup.addView(chip)
        }
    }
}