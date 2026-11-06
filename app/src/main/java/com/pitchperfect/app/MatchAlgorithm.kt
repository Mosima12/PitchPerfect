package com.pitchperfect.app

import kotlin.math.roundToInt

object MatchAlgorithm {
    /**
     * Calculates a match percentage between a user's skills and a project's required skills.
     * Returns a score between 0 and 100.
     */
    fun calculateMatchScore(userSkills: List<String>, requiredSkills: List<String>): Int {
        // If the project has no required skills, everyone is a 50% match
        if (requiredSkills.isEmpty()) return 50

        // Convert to lowercase to avoid case-sensitivity issues
        val userSkillsLower = userSkills.map { it.lowercase().trim() }
        val requiredSkillsLower = requiredSkills.map { it.lowercase().trim() }

        // Count how many required skills the user has
        var matches = 0
        for (skill in requiredSkillsLower) {
            if (userSkillsLower.contains(skill)) {
                matches++
            }
        }

        // Calculate percentage (Matches / Total Required) * 100
        val percentage = (matches.toDouble() / requiredSkillsLower.size) * 100

        // Add a base score so it looks realistic (e.g., minimum 40% match for same university)
        val finalScore = 40 + (percentage * 0.6)

        return finalScore.roundToInt().coerceIn(0, 100)
    }
}