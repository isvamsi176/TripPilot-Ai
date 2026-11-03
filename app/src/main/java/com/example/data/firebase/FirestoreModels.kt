package com.example.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserEntity(
    @DocumentId val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val linkedIrctc: String = "",
    val frequentFlyer: String = "",
    val miles: Long = 0L,
    val preferredTier: String = "BALANCED",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TripEntity(
    @DocumentId val id: String = "",
    val origin: String = "",
    val destination: String = "",
    val days: Long = 0L,
    val travellers: Long = 0L,
    val budgetInr: Long = 0L,
    val tier: String = "BALANCED",
    val totalCostInr: Long = 0L,
    val preferences: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BookingEntity(
    @DocumentId val id: String = "",
    val pnr: String = "",
    val title: String = "",
    val type: String = "",
    val routeOrLocation: String = "",
    val travelDate: String = "",
    val travellersText: String = "",
    val totalAmountInr: Long = 0L,
    val status: String = "CONFIRMED",
    val seatOrRoomNumber: String = "",
    val qrPayload: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
