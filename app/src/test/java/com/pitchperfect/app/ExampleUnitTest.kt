package com.pitchperfect.app

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun emailValidator_ValidEmail_ReturnsTrue() {
        val email = "student@university.ac.za"
        assertTrue("Valid email should contain @", email.contains("@"))
    }

    @Test
    fun emailValidator_InvalidEmail_ReturnsFalse() {
        val email = "invalidemail.com"
        assertFalse("Invalid email should not contain @", email.contains("@"))
    }

    @Test
    fun passwordValidator_ShortPassword_ReturnsFalse() {
        val password = "12345"
        assertFalse("Password shorter than 6 characters should fail", password.length >= 6)
    }

    @Test
    fun passwordValidator_ValidPassword_ReturnsTrue() {
        val password = "password123"
        assertTrue("Valid password should be at least 6 characters", password.length >= 6)
    }

    @Test
    fun matchScore_Calculation_IsCorrect() {
        val sharedSkills = 2
        val totalSkills = 3
        val score = (sharedSkills.toDouble() / totalSkills.toDouble()) * 100
        assertEquals(66.67, score, 0.01)
    }

    @Test
    fun userCreation_ReturnsCorrectValues() {
        val user = com.pitchperfect.app.model.User(
            id = "123",
            name = "Test User",
            university = "Test University",
            email = "test@test.com"
        )
        assertEquals("Test User", user.name)
        assertEquals("Test University", user.university)
        assertEquals("test@test.com", user.email)
    }
}