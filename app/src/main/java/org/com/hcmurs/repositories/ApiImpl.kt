package org.com.hcmurs.repositories

import org.com.hcmurs.model.NoteItem
import kotlinx.coroutines.delay
import javax.inject.Inject

class ApiImpl @Inject constructor() : Api {
    var notes = ArrayList<NoteItem>()
    private val registeredUsers = mutableListOf<Triple<String, String, String>>()


    init {
        notes.add(NoteItem(1, "1", "1"))
        notes.add(NoteItem(2, "2", "2"))
        notes.add(NoteItem(3, "3", "3"))
    }

    override suspend fun login(username: String, password: String): Boolean {
        delay(1000)
        if (username != "1" || password != "1") {
            throw Exception("Wrong credentials")
        }
        return true
    }

    override suspend fun register(username: String, email: String, password: String): Boolean {
        delay(1500) // Simulate network delay for registration

        // Basic validation and duplicate checks
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Username, email, and password cannot be empty.")
        }
        if (registeredUsers.any { it.first == username }) {
            throw Exception("Username already exists.")
        }
        if (registeredUsers.any { it.second == email }) {
            throw Exception("Email already registered.")
        }

        // Simulate successful registration
        registeredUsers.add(Triple(username, email, password))
        println("User registered: Username=$username, Email=$email")
        return true
    }

    override suspend fun loadNotes(): List<NoteItem> {
        delay(1000)
        return notes
    }

    override suspend fun addNote(title: String, content: String) {
        delay(1000)
        notes.add(NoteItem(System.currentTimeMillis(), title, content))
    }

    override suspend fun editNote(dt: Long, title: String, content: String) {
        delay(1000)
        for (i in notes.indices) {
            if (notes[i].dateTime == dt) {
                notes[i] = NoteItem(dt, title, content)
                break
            }
        }
    }

    override suspend fun deleteNote(dt: Long) {
        delay(1000)
        for (i in notes.indices) {
            if (notes[i].dateTime == dt) {
                notes.removeAt(i)
                break
            }
        }
    }
}