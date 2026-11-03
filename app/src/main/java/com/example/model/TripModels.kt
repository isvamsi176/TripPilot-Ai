package com.example.model

enum class AgentType(val displayName: String, val role: String) {
    TRAVEL_PLANNER("Travel Planner Agent", "Intent parser, preferences & constraint orchestrator"),
    DESTINATION("Destination Agent", "Seasonality, attractions, weather & local discovery"),
    FLIGHT("Flight Agent", "Aviation comparison, baggage & live airline pricing"),
    HOTEL("Hotel Agent", "Stays, boutique villas, star ratings & policies"),
    RAILWAY_BUS("Railway & Bus Agent", "IRCTC trains, sleeper buses & intercity routes"),
    BUDGET_OPTIMIZER("Budget Optimizer Agent", "Tiered plans, cost breakdown & dynamic savings"),
    ITINERARY_SUPPORT("Itinerary & Support Agent", "Day-wise scheduling & real-time travel assist")
}

enum class AgentStatus {
    IDLE, ANALYZING, SEARCHING, OPTIMIZING, COMPLETED, ALERT
}

data class AgentState(
    val type: AgentType,
    val status: AgentStatus = AgentStatus.IDLE,
    val progress: Float = 0f, // 0.0 to 1.0
    val currentTask: String = "Standby",
    val completedResultSummary: String = "",
    val activityLogs: List<String> = emptyList()
)

enum class TravelTier(val title: String, val subtitle: String) {
    ECONOMY("Economy Plan", "Best for budget travelers & smart backpackers"),
    BALANCED("Balanced Plan", "Optimal balance of comfort, time & value"),
    PREMIUM_LUXURY("Premium Luxury", "5-Star resorts, flights & VIP experiences")
}

data class TripRequest(
    val origin: String = "Hyderabad",
    val destination: String = "Goa",
    val days: Int = 5,
    val travellers: Int = 2,
    val budgetInr: Int = 25000,
    val preferences: List<String> = listOf("Beaches", "Sightseeing", "Adventure"),
    val preferredTier: TravelTier = TravelTier.BALANCED
)

data class DestinationInfo(
    val id: String,
    val name: String,
    val state: String,
    val tagLine: String,
    val description: String,
    val bestSeason: String,
    val avgTemp: String,
    val vibeTags: List<String>,
    val imageDrawableName: String,
    val topAttractions: List<String>,
    val isInternational: Boolean = false
)

data class FlightOption(
    val id: String,
    val airline: String,
    val flightNumber: String,
    val originCode: String,
    val destCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val durationHours: Double,
    val baggageAllowance: String,
    val pricePerPersonInr: Int,
    val onTimeRating: String = "92%",
    val cabinClass: String = "Economy"
)

data class TrainOption(
    val id: String,
    val trainNumber: String,
    val trainName: String,
    val fromStation: String,
    val toStation: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val durationHours: Double,
    val travelClass: String, // 1A, 2A, 3A, SL
    val fareInr: Int,
    val availabilityStatus: String, // "AVAILABLE 42", "RAC 08"
    val confirmationProbability: String = "High (94%)"
)

data class BusOption(
    val id: String,
    val operator: String,
    val busType: String,
    val boardingPoint: String,
    val droppingPoint: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val durationHours: Double,
    val fareInr: Int,
    val rating: Double,
    val seatsAvailable: Int
)

data class HotelOption(
    val id: String,
    val name: String,
    val location: String,
    val starRating: Int,
    val userRating: Double,
    val tier: TravelTier,
    val pricePerNightInr: Int,
    val totalNightsCostInr: Int,
    val amenities: List<String>,
    val cancellationPolicy: String,
    val distanceInfo: String,
    val imageDrawableName: String
)

data class DoorToDoorComparison(
    val mode: String, // "FLIGHT", "TRAIN", "BUS"
    val modeDisplayName: String,
    val iconName: String,
    val homeToStationCabInr: Int,
    val transitTicketPerPersonInr: Int,
    val stationToHotelCabInr: Int,
    val returnMultiplier: Int = 2,
    val totalDoorToDoorPerPersonInr: Int,
    val totalForTripInr: Int, // for all travellers
    val travelDurationHours: Double,
    val comfortScore: Int, // 1-5
    val proBadge: String,
    val conBadge: String
)

data class CostBreakdown(
    val transportInr: Int,
    val hotelInr: Int,
    val localTransitInr: Int,
    val foodInr: Int,
    val activitiesInr: Int,
    val miscBufferInr: Int
) {
    val totalInr: Int get() = transportInr + hotelInr + localTransitInr + foodInr + activitiesInr + miscBufferInr
}

data class TierPackagePlan(
    val tier: TravelTier,
    val transportModeDesc: String,
    val hotelTierDesc: String,
    val breakdown: CostBreakdown,
    val savingsOrExtraInr: Int,
    val savingsBadgeText: String,
    val isRecommended: Boolean = false
)

data class DayItinerary(
    val dayNumber: Int,
    val title: String,
    val morning: String,
    val afternoon: String,
    val evening: String,
    val dinnerSpot: String,
    val insiderTip: String,
    val dayExpenseEstimateInr: Int
)

data class BookingRecord(
    val id: String,
    val pnr: String,
    val title: String,
    val type: String, // Flight, Train, Bus, Hotel
    val routeOrLocation: String,
    val travelDate: String,
    val travellersText: String,
    val totalAmountInr: Int,
    val status: String, // "CONFIRMED", "PENDING", "CANCELLED"
    val seatOrRoomNumber: String,
    val qrPayload: String
)

data class TravelDisruption(
    val id: String,
    val title: String,
    val severity: String, // Alert, Notice, Safe
    val message: String,
    val recommendedAction: String,
    val timeAgo: String
)
