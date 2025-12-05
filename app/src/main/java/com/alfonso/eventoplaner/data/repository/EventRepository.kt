package com.alfonso.eventoplaner.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.alfonso.eventoplaner.data.model.Event
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = FirebaseFirestore.getInstance()
    private val eventsCollection = db.collection("events")
    private val auth = FirebaseAuth.getInstance()

    fun getEvents(): Flow<List<Event>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""

        val listener = eventsCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    Event(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        title = doc.getString("title") ?: "",
                        date = doc.getString("date") ?: "",
                        description = doc.getString("description") ?: ""
                    )
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createEvent(title: String, date: String, description: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

            val event = hashMapOf(
                "userId" to userId,
                "title" to title,
                "date" to date,
                "description" to description
            )

            eventsCollection.add(event).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: String, title: String, date: String, description: String): Result<Unit> {
        return try {
            val updates = hashMapOf(
                "title" to title,
                "date" to date,
                "description" to description
            )

            eventsCollection.document(eventId).update(updates as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection.document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}