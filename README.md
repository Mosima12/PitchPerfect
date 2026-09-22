# PitchPerfect

**Student Name:** Mosima Chantel Leboho  
**Student Number:** st10456440  


---

## 📱 Purpose

PitchPerfect is an Android mobile application that connects university students with shared interests, skills, and entrepreneurial ambitions. It allows students to discover like-minded collaborators, join projects, and build real startups together — bridging the gap between fragmented professional networking tools and the specific needs of student entrepreneurs.

**Vision:** To become the primary platform where the next generation of entrepreneurs finds their co-founders, builds their teams, and launches their ventures.

---

## ✨ Features Implemented

### Core Features
- ✅ **User Registration** — Firebase Authentication with secure password handling
- ✅ **User Login** — Encrypted credentials handled automatically by Firebase Auth
- ✅ **Session Management** — Auto-login for returning users
- ✅ **Logout** — Secure session termination

### User-Defined Features
- ✅ **Home Dashboard** — Live stats (points, projects, matches, badges)
- ✅ **Discover** — Browse other students with skill-based match scores
- ✅ **Projects Feed** — Post and browse business ideas in real time
- ✅ **Profile Editing** — Update bio and skills, saved to Firestore

### Technical Features
- ✅ **REST API Integration** — Live data fetched via Retrofit and displayed on the Home screen
- ✅ **Automated Unit Tests** — JUnit 4 tests for input validation logic (6 tests)
- ✅ **Continuous Integration** — GitHub Actions workflow builds and tests on every push

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | Material Design 3, ConstraintLayout, RecyclerView |
| **Architecture** | MVVM-inspired (Fragments + Data layer) |
| **Auth & Database** | Firebase Authentication + Cloud Firestore |
| **Networking** | Retrofit2 + OkHttp + Gson |
| **Testing** | JUnit 4 |
| **CI/CD** | GitHub Actions |
| **Version Control** | Git + GitHub |

---

## 🏗️ App Architecture
