# 📱 PitchPerfect 2

**Student Name:** Mosima Chantel Leboho
**Student Number:** ST10456440

---

## 📱 Purpose

**PitchPerfect 2** is an Android mobile application designed to connect university students who share similar interests, skills, and entrepreneurial goals.

The application provides students with a platform where they can discover potential collaborators, explore entrepreneurial projects, and connect with other students who have complementary skills.

The main goal of PitchPerfect 2 is to make it easier for student entrepreneurs to find the right people to work with and turn their ideas into real projects.

### 🎯 Vision

To create a platform where the next generation of entrepreneurs can discover co-founders, build teams, collaborate on ideas, and develop their ventures.

---

## ✨ Features Implemented

### 🔐 Authentication & User Management

* ✅ **User Registration** — Students can create accounts using Firebase Authentication.
* ✅ **User Login** — Users can securely log into their accounts using Firebase Authentication.
* ✅ **Session Management** — Returning users can automatically access the application without logging in again.
* ✅ **Logout** — Users can securely log out and terminate their current session.

### 👥 User & Collaboration Features

* ✅ **Home Dashboard** — Displays user information and live statistics such as points, projects, matches, and badges.
* ✅ **Discover** — Allows students to browse other users and view skill-based match scores.
* ✅ **Projects Feed** — Students can browse and post entrepreneurial ideas and projects.
* ✅ **Profile Editing** — Users can update their biography and skills, with changes stored in Cloud Firestore.

### ⚙️ Technical Features

* ✅ **REST API Integration** — The application uses Retrofit to retrieve live data from a REST API and display it within the application.
* ✅ **Automated Unit Testing** — JUnit 4 tests are used to test input validation logic, with six automated tests implemented.
* ✅ **Continuous Integration** — GitHub Actions automatically builds and tests the application whenever changes are pushed to the repository.

---

## 🛠️ Technology Stack

| Layer                    | Technology                                                  |
| ------------------------ | ----------------------------------------------------------- |
| **Programming Language** | Kotlin                                                      |
| **User Interface**       | Material Design 3, ConstraintLayout, RecyclerView           |
| **Architecture**         | MVVM-inspired architecture using Fragments and a data layer |
| **Authentication**       | Firebase Authentication                                     |
| **Database**             | Cloud Firestore                                             |
| **Networking**           | Retrofit 2, OkHttp, Gson                                    |
| **Testing**              | JUnit 4                                                     |
| **CI/CD**                | GitHub Actions                                              |
| **Version Control**      | Git & GitHub                                                |

---

## 🏗️ Application Architecture

PitchPerfect 2 follows an **MVVM-inspired architecture** using Android Fragments and a dedicated data layer.

The application is divided into different components to separate the user interface, application logic, data handling, authentication, and networking.

The main technologies work together as follows:

**Android Application → Data Layer → Firebase / REST API → Data Response → User Interface**

### Firebase

Firebase Authentication is responsible for handling user registration, login, session management, and logout.

Cloud Firestore is used to store and retrieve application data such as user profiles, skills, and project information.

### REST API

The application also integrates with a REST API using **Retrofit 2**. API data is retrieved from the backend and displayed within the application.

### Networking

**OkHttp** is used as the HTTP client, while **Gson** is used to convert JSON responses into Kotlin objects that can be processed by the application.

### Testing

JUnit 4 is used to test important application logic, particularly input validation. The project currently contains **six automated unit tests**.

### Continuous Integration

GitHub Actions is used to automatically build and test the application whenever code is pushed to the GitHub repository. This helps identify build or testing issues during development.

---

## 📂 Project Repository

The complete Kotlin source code, project files, documentation, tests, and GitHub Actions workflow are available in the project's GitHub repository.

The repository can be used to review the application's implementation, run the project, and view the automated testing and CI workflow.


## 🎥 YouTube Video Presentation

A full video presentation of **PitchPerfect 2** is available below.

**YouTube Video:**
🔗 https://youtube.com/shorts/-t8TppDwr1A?si=xxlj5moPlcJZ2MD3

AI Usage Declaration
During the completion of the PitchPerfect Part 2 prototype, I used AI tools (ChatGPT) primarily for guidance, debugging, and architectural advice. AI was not used to generate the application autonomously; instead, it served as a learning assistant to help me understand technical concepts, troubleshoot errors, and structure my code following best practices.

1. Architectural Guidance

AI was used to explain industry-standard architectural patterns for Android development. Based on this guidance, I structured PitchPerfect using an MVVM-inspired approach:

Separating UI (Fragments) from business logic (AuthManager, ApiService)

Using Kotlin Coroutines for asynchronous operations

Implementing a single HomeActivity with a BottomNavigationView hosting five feature fragments

I made the final decisions on which patterns to apply and how to structure the packages.

2. Debugging Assistance

I encountered several technical errors during development and used AI to help me understand them, rather than just fix them. Cited examples include:

Gradle sync errors when migrating from the default Jetpack Compose template to XML Views. AI explained the cause (conflicting plugin references) and guided me to rewrite build.gradle.kts with explicit plugin IDs.

Firebase error [CONFIGURATION_NOT_FOUND] — AI guided me through the diagnostic process: checking the INTERNET permission in AndroidManifest.xml and enabling the Email/Password sign-in method in the Firebase Console.

Duplicate account errors — AI explained that Firebase intentionally rejects duplicate emails and helped me understand the correct testing approach.
