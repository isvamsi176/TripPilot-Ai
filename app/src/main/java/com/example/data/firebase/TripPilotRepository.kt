package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TripPilotRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth get() = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing TripPilot Firestore.")
    }

    fun currentUserIdOrNull(): String? = auth.currentUser?.uid

    // Profile Management
    suspend fun saveUserProfile(displayName: String, email: String, linkedIrctc: String, frequentFlyer: String, preferredTier: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val data = mapOf(
                    "displayName" to displayName,
                    "email" to email,
                    "linkedIrctc" to linkedIrctc,
                    "frequentFlyer" to frequentFlyer,
                    "miles" to 14250L,
                    "preferredTier" to preferredTier,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            } else {
                val data = mapOf(
                    "displayName" to displayName,
                    "email" to email,
                    "linkedIrctc" to linkedIrctc,
                    "frequentFlyer" to frequentFlyer,
                    "preferredTier" to preferredTier,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(data).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    fun observeUserProfile(): Flow<UserEntity?> = flow {
        val uid = currentUserIdOrNull() ?: return@flow emit(null)
        val docRef = db.collection("users").document(uid)
        emitAll(
            docRef.snapshots()
                .map { snapshot -> if (snapshot.exists()) snapshot.toObject(UserEntity::class.java) else null }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
                    emit(null)
                }
        )
    }

    // Trips Collection
    suspend fun saveTrip(
        origin: String,
        destination: String,
        days: Int,
        travellers: Int,
        budgetInr: Int,
        tier: String,
        totalCostInr: Int,
        preferences: List<String>
    ): String {
        val uid = requireUserId()
        val tripId = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("trips").document(tripId)
        try {
            val payload = mapOf(
                "origin" to origin,
                "destination" to destination,
                "days" to days.toLong(),
                "travellers" to travellers.toLong(),
                "budgetInr" to budgetInr.toLong(),
                "tier" to tier,
                "totalCostInr" to totalCostInr.toLong(),
                "preferences" to preferences,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            return tripId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    fun observeTrips(): Flow<List<TripEntity>> = flow {
        val uid = currentUserIdOrNull() ?: return@flow emit(emptyList())
        val collectionRef = db.collection("users").document(uid).collection("trips")
        emitAll(
            collectionRef.snapshots()
                .map { snapshot -> snapshot.toObjects(TripEntity::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, collectionRef.path)
                    emit(emptyList())
                }
        )
    }

    // Bookings Collection
    suspend fun saveBooking(
        pnr: String,
        title: String,
        type: String,
        routeOrLocation: String,
        travelDate: String,
        travellersText: String,
        totalAmountInr: Int,
        status: String,
        seatOrRoomNumber: String,
        qrPayload: String
    ): String {
        val uid = requireUserId()
        val bookingId = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("bookings").document(bookingId)
        try {
            val payload = mapOf(
                "pnr" to pnr,
                "title" to title,
                "type" to type,
                "routeOrLocation" to routeOrLocation,
                "travelDate" to travelDate,
                "travellersText" to travellersText,
                "totalAmountInr" to totalAmountInr.toLong(),
                "status" to status,
                "seatOrRoomNumber" to seatOrRoomNumber,
                "qrPayload" to qrPayload,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            return bookingId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    fun observeBookings(): Flow<List<BookingEntity>> = flow {
        val uid = currentUserIdOrNull() ?: return@flow emit(emptyList())
        val collectionRef = db.collection("users").document(uid).collection("bookings")
        emitAll(
            collectionRef.snapshots()
                .map { snapshot -> snapshot.toObjects(BookingEntity::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, collectionRef.path)
                    emit(emptyList())
                }
        )
    }

    suspend fun cancelBooking(bookingId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("bookings").document(bookingId)
        try {
            val payload = mapOf(
                "status" to "CANCELLED",
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }
}
