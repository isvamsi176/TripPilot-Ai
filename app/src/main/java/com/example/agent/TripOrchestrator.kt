package com.example.agent

import com.example.data.TravelDataSource
import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TripOrchestrator {

    private val _tripRequest = MutableStateFlow(TripRequest())
    val tripRequest: StateFlow<TripRequest> = _tripRequest.asStateFlow()

    private val _agentsState = MutableStateFlow(createInitialAgentStates())
    val agentsState: StateFlow<Map<AgentType, AgentState>> = _agentsState.asStateFlow()

    private val _isOrchestrating = MutableStateFlow(false)
    val isOrchestrating: StateFlow<Boolean> = _isOrchestrating.asStateFlow()

    private val _overallProgress = MutableStateFlow(0f)
    val overallProgress: StateFlow<Float> = _overallProgress.asStateFlow()

    // Results from Agents
    private val _destinationInfo = MutableStateFlow<DestinationInfo?>(null)
    val destinationInfo: StateFlow<DestinationInfo?> = _destinationInfo.asStateFlow()

    private val _flights = MutableStateFlow<List<FlightOption>>(emptyList())
    val flights: StateFlow<List<FlightOption>> = _flights.asStateFlow()

    private val _trains = MutableStateFlow<List<TrainOption>>(emptyList())
    val trains: StateFlow<List<TrainOption>> = _trains.asStateFlow()

    private val _buses = MutableStateFlow<List<BusOption>>(emptyList())
    val buses: StateFlow<List<BusOption>> = _buses.asStateFlow()

    private val _hotels = MutableStateFlow<List<HotelOption>>(emptyList())
    val hotels: StateFlow<List<HotelOption>> = _hotels.asStateFlow()

    private val _doorToDoorComparisons = MutableStateFlow<List<DoorToDoorComparison>>(emptyList())
    val doorToDoorComparisons: StateFlow<List<DoorToDoorComparison>> = _doorToDoorComparisons.asStateFlow()

    private val _tierPlans = MutableStateFlow<List<TierPackagePlan>>(emptyList())
    val tierPlans: StateFlow<List<TierPackagePlan>> = _tierPlans.asStateFlow()

    private val _itinerary = MutableStateFlow<List<DayItinerary>>(emptyList())
    val itinerary: StateFlow<List<DayItinerary>> = _itinerary.asStateFlow()

    private val _bookings = MutableStateFlow<List<BookingRecord>>(createInitialBookings())
    val bookings: StateFlow<List<BookingRecord>> = _bookings.asStateFlow()

    private val _disruptions = MutableStateFlow(TravelDataSource.sampleDisruptions)
    val disruptions: StateFlow<List<TravelDisruption>> = _disruptions.asStateFlow()

    // Replanning State
    private val _originalTierPlan = MutableStateFlow<TierPackagePlan?>(null)
    val originalTierPlan: StateFlow<TierPackagePlan?> = _originalTierPlan.asStateFlow()

    private val _replannedTierPlan = MutableStateFlow<TierPackagePlan?>(null)
    val replannedTierPlan: StateFlow<TierPackagePlan?> = _replannedTierPlan.asStateFlow()

    private var orchestrationJob: Job? = null

    init {
        // Initialize with default Goa dataset so screen has immediate preview
        loadDefaultPlan(TripRequest())
    }

    private fun createInitialAgentStates(): Map<AgentType, AgentState> {
        return AgentType.values().associateWith { type ->
            AgentState(
                type = type,
                status = AgentStatus.IDLE,
                progress = 0f,
                currentTask = "Standby - Waiting for trip prompt",
                completedResultSummary = "",
                activityLogs = emptyList()
            )
        }
    }

    private fun createInitialBookings(): List<BookingRecord> {
        return listOf(
            BookingRecord(
                id = "b-101",
                pnr = "TP-GOA-78241",
                title = "HYB VSG Express 3A (2 Travellers)",
                type = "Train",
                routeOrLocation = "Hyderabad Deccan (HYB) -> Madgaon (MAO)",
                travelDate = "15 Nov 2026",
                travellersText = "2 Adults (Confirmed: B2-24, B2-25)",
                totalAmountInr = 2760,
                status = "CONFIRMED",
                seatOrRoomNumber = "Coach B2, Seats 24 (MB) & 25 (UB)",
                qrPayload = "IRCTC-TP-78241-HYB-MAO-2P-OK"
            ),
            BookingRecord(
                id = "b-102",
                pnr = "TP-HTL-99312",
                title = "BloomSuites Candolim Beach Resort",
                type = "Hotel",
                routeOrLocation = "Candolim Beach Road, North Goa",
                travelDate = "16 Nov - 20 Nov 2026 (4 Nights)",
                travellersText = "2 Guests • Deluxe Balcony Room",
                totalAmountInr = 11400,
                status = "CONFIRMED",
                seatOrRoomNumber = "Room 304 (Pool View)",
                qrPayload = "HTL-BLOOM-99312-CANDOLIM-CONFIRMED"
            )
        )
    }

    private fun loadDefaultPlan(request: TripRequest) {
        val dest = TravelDataSource.destinations.find { it.name.equals(request.destination, ignoreCase = true) }
            ?: TravelDataSource.destinations.first()
        _destinationInfo.value = dest
        _flights.value = TravelDataSource.getFlightsForRoute(request.origin, request.destination)
        _trains.value = TravelDataSource.getTrainsForRoute(request.origin, request.destination)
        _buses.value = TravelDataSource.getBusesForRoute(request.origin, request.destination)
        _hotels.value = TravelDataSource.getHotelsForDestination(request.destination, request.days - 1)
        calculateDoorToDoor(request)
        calculateTierPlans(request)
        _itinerary.value = TravelDataSource.buildItinerary(request.destination, request.days)
    }

    fun startOrchestration(
        request: TripRequest,
        scope: CoroutineScope,
        onComplete: () -> Unit = {}
    ) {
        orchestrationJob?.cancel()
        _isOrchestrating.value = true
        _overallProgress.value = 0.05f
        _tripRequest.value = request

        orchestrationJob = scope.launch {
            try {
                // Step 1: Travel Planner Agent starts
                updateAgent(
                    AgentType.TRAVEL_PLANNER,
                    AgentStatus.ANALYZING,
                    0.3f,
                    "Parsing trip parameters & constraints: ${request.origin} -> ${request.destination}, ${request.days} days, ₹${request.budgetInr} for ${request.travellers} travellers",
                    logs = listOf(
                        "Extracted origin: ${request.origin}, destination: ${request.destination}",
                        "Budget: ₹${request.budgetInr} (₹${request.budgetInr / request.travellers}/person)",
                        "Preferences matched: ${request.preferences.joinToString(", ")}"
                    )
                )
                _overallProgress.value = 0.15f
                delay(400)

                // Step 2: Destination Agent
                val dest = TravelDataSource.destinations.find { it.name.equals(request.destination, ignoreCase = true) }
                    ?: TravelDataSource.destinations.first()
                _destinationInfo.value = dest

                updateAgent(
                    AgentType.DESTINATION,
                    AgentStatus.SEARCHING,
                    0.5f,
                    "Evaluating seasonal suitability, average temperature (${dest.avgTemp}) & top attractions",
                    logs = listOf(
                        "Season status: ${dest.bestSeason} (Optimal travel window)",
                        "Attractions indexed: ${dest.topAttractions.joinToString(", ")}",
                        "Vibe tags confirmed: ${dest.vibeTags.joinToString(", ")}"
                    )
                )
                _overallProgress.value = 0.28f
                delay(350)

                updateAgent(
                    AgentType.TRAVEL_PLANNER,
                    AgentStatus.COMPLETED,
                    1.0f,
                    "Parameters verified. Handed off to parallel logistics engines.",
                    summary = "Trip: ${request.origin} to ${request.destination} • ₹${request.budgetInr} • ${request.travellers}p",
                    logs = listOf("Constraint verification complete: Feasible for 3 tiers")
                )

                updateAgent(
                    AgentType.DESTINATION,
                    AgentStatus.COMPLETED,
                    1.0f,
                    "Destination profile finalized.",
                    summary = "${dest.name} (${dest.state}) - ${dest.bestSeason}",
                    logs = listOf("Recommended 5 top scenic hubs and local coastal route")
                )

                // Step 3: PARALLEL EXECUTION of Flight Agent, Railway & Bus Agent, and Hotel Agent!
                updateAgent(
                    AgentType.FLIGHT,
                    AgentStatus.SEARCHING,
                    0.25f,
                    "Querying airline fares for ${request.origin} to ${request.destination}...",
                    logs = listOf("Connecting to DGCA & airline booking APIs...")
                )
                updateAgent(
                    AgentType.RAILWAY_BUS,
                    AgentStatus.SEARCHING,
                    0.25f,
                    "Querying IRCTC availability & private sleeper bus operators...",
                    logs = listOf("Scanning trains via PRS & Bus networks via API...")
                )
                updateAgent(
                    AgentType.HOTEL,
                    AgentStatus.SEARCHING,
                    0.25f,
                    "Scanning hotel inventory across Budget, Boutique & 5-Star Luxury...",
                    logs = listOf("Checking verified guest reviews, beach proximity & sanitization...")
                )
                _overallProgress.value = 0.45f
                delay(500)

                // Parallel progress simulation
                coroutineScope {
                    launch {
                        _flights.value = TravelDataSource.getFlightsForRoute(request.origin, request.destination)
                        updateAgent(
                            AgentType.FLIGHT,
                            AgentStatus.COMPLETED,
                            1.0f,
                            "Retrieved 4 airline options.",
                            summary = "Best: IndiGo 6E-512 at ₹3,850/p (1h 20m)",
                            logs = listOf(
                                "IndiGo 6E-512: ₹3,850 • Akasa QP-1392: ₹3,450",
                                "Baggage: 15kg checked + 7kg cabin included",
                                "On-time rating: 95%"
                            )
                        )
                    }
                    launch {
                        _trains.value = TravelDataSource.getTrainsForRoute(request.origin, request.destination)
                        _buses.value = TravelDataSource.getBusesForRoute(request.origin, request.destination)
                        updateAgent(
                            AgentType.RAILWAY_BUS,
                            AgentStatus.COMPLETED,
                            1.0f,
                            "Retrieved 4 IRCTC trains & 4 luxury sleeper bus options.",
                            summary = "Train: 17027 (3A ₹1,380) | Bus: Orange Sleeper (₹1,650)",
                            logs = listOf(
                                "Train 17027 HYB VSG Exp: 3A Confirm seats AVL 34",
                                "Sleeper Bus: Orange Travels AC Sleeper ₹1,650 (Rating 4.6)",
                                "IRCTC Tatkal & General quotas checked"
                            )
                        )
                    }
                    launch {
                        val nights = if (request.days > 1) request.days - 1 else 1
                        _hotels.value = TravelDataSource.getHotelsForDestination(request.destination, nights)
                        updateAgent(
                            AgentType.HOTEL,
                            AgentStatus.COMPLETED,
                            1.0f,
                            "Curated 3 tier hotels with zero-cancellation fee guarantees.",
                            summary = "Economy: ₹1,200/n | Balanced: ₹2,850/n | Taj: ₹8,500/n",
                            logs = listOf(
                                "Zostel / Casa Backpacker: ₹1,200/night (98% rating)",
                                "BloomSuites Candolim: ₹2,850/night (Breakfast included)",
                                "Taj Fort Aguada: ₹8,500/night (Private beachfront)"
                            )
                        )
                    }
                }

                _overallProgress.value = 0.75f
                delay(400)

                // Step 4: Budget Optimization Agent calculates plans & door-to-door
                updateAgent(
                    AgentType.BUDGET_OPTIMIZER,
                    AgentStatus.OPTIMIZING,
                    0.6f,
                    "Computing door-to-door transit cost matrices & 3-tier expense allocations...",
                    logs = listOf(
                        "Summing local cabs + intercity + hotel + food + sightseeing",
                        "Balancing against ₹${request.budgetInr} target budget"
                    )
                )
                calculateDoorToDoor(request)
                calculateTierPlans(request)
                _overallProgress.value = 0.88f
                delay(350)

                val recommendedPlan = _tierPlans.value.find { it.isRecommended } ?: _tierPlans.value.first()
                updateAgent(
                    AgentType.BUDGET_OPTIMIZER,
                    AgentStatus.COMPLETED,
                    1.0f,
                    "Budget optimized with ₹${recommendedPlan.savingsOrExtraInr} savings in Balanced plan.",
                    summary = "Economy: ₹${_tierPlans.value.getOrNull(0)?.breakdown?.totalInr ?: 0} | Balanced: ₹${recommendedPlan.breakdown.totalInr}",
                    logs = listOf(
                        "Door-to-door transport matrix generated",
                        "Tier recommendations saved for dynamic replanning"
                    )
                )

                // Step 5: Itinerary & Support Agent
                updateAgent(
                    AgentType.ITINERARY_SUPPORT,
                    AgentStatus.OPTIMIZING,
                    0.8f,
                    "Compiling personalized ${request.days}-day hour-by-hour itinerary & alerts...",
                    logs = listOf(
                        "Allocated beach morning slots & sunset viewpoints",
                        "Checked local road maintenance & tidal safety notices"
                    )
                )
                _itinerary.value = TravelDataSource.buildItinerary(request.destination, request.days)
                _overallProgress.value = 1.0f
                delay(300)

                updateAgent(
                    AgentType.ITINERARY_SUPPORT,
                    AgentStatus.COMPLETED,
                    1.0f,
                    "Full travel package ready with live itinerary & travel alerts.",
                    summary = "${request.days}-Day Itinerary + 3 Active Travel Alerts",
                    logs = listOf(
                        "Detailed Day 1 to Day ${request.days} schedule constructed",
                        "Emergency contacts (112, 139) mapped to destination"
                    )
                )

                _isOrchestrating.value = false
                onComplete()
            } catch (e: Exception) {
                _isOrchestrating.value = false
            }
        }
    }

    private fun updateAgent(
        type: AgentType,
        status: AgentStatus,
        progress: Float,
        task: String,
        summary: String = "",
        logs: List<String> = emptyList()
    ) {
        val current = _agentsState.value.toMutableMap()
        val existing = current[type] ?: AgentState(type)
        val combinedLogs = (existing.activityLogs + logs).takeLast(8)
        current[type] = existing.copy(
            status = status,
            progress = progress,
            currentTask = task,
            completedResultSummary = if (summary.isNotEmpty()) summary else existing.completedResultSummary,
            activityLogs = combinedLogs
        )
        _agentsState.value = current
    }

    private fun calculateDoorToDoor(request: TripRequest) {
        val travellers = request.travellers

        // Flight Door-to-Door: Home -> Rajiv Gandhi Intl Airport (Cab ₹900) + Flight (₹3,850 * 2) + Goa Airport -> Candolim (Cab ₹1,200) + Return
        val flightTicket = 3850 * 2 // Round trip per person
        val flightLocalHomeCab = 1800 // Round trip cab in origin
        val flightDestCab = 2400 // Round trip cab in Goa
        val flightPerPersonTotal = flightTicket + (flightLocalHomeCab + flightDestCab) / travellers
        val flightTripTotal = flightPerPersonTotal * travellers

        // Train Door-to-Door: Home -> HYB Station (Auto/Cab ₹250) + Train 3A (₹1,380 * 2) + Madgaon Station -> Candolim (Cab ₹1,100) + Return
        val trainTicket = 1380 * 2
        val trainLocalHomeCab = 500
        val trainDestCab = 2200
        val trainPerPersonTotal = trainTicket + (trainLocalHomeCab + trainDestCab) / travellers
        val trainTripTotal = trainPerPersonTotal * travellers

        // Bus Door-to-Door: Home -> Bus Pick-up (Auto ₹150) + Sleeper Bus (₹1,650 * 2) + Mapusa Drop -> Candolim (Auto ₹400) + Return
        val busTicket = 1650 * 2
        val busLocalHomeCab = 300
        val busDestCab = 800
        val busPerPersonTotal = busTicket + (busLocalHomeCab + busDestCab) / travellers
        val busTripTotal = busPerPersonTotal * travellers

        _doorToDoorComparisons.value = listOf(
            DoorToDoorComparison(
                mode = "TRAIN",
                modeDisplayName = "Indian Railways (3A)",
                iconName = "train",
                homeToStationCabInr = trainLocalHomeCab / 2,
                transitTicketPerPersonInr = trainTicket,
                stationToHotelCabInr = trainDestCab / 2,
                returnMultiplier = 2,
                totalDoorToDoorPerPersonInr = trainPerPersonTotal,
                totalForTripInr = trainTripTotal,
                travelDurationHours = 20.5,
                comfortScore = 4,
                proBadge = "Most Economical & Comfortable Sleep",
                conBadge = "Longer Transit (20h 30m)"
            ),
            DoorToDoorComparison(
                mode = "FLIGHT",
                modeDisplayName = "Domestic Flight (Direct)",
                iconName = "flight",
                homeToStationCabInr = flightLocalHomeCab / 2,
                transitTicketPerPersonInr = flightTicket,
                stationToHotelCabInr = flightDestCab / 2,
                returnMultiplier = 2,
                totalDoorToDoorPerPersonInr = flightPerPersonTotal,
                totalForTripInr = flightTripTotal,
                travelDurationHours = 1.33,
                comfortScore = 5,
                proBadge = "Fastest: Save 18 Hours of Travel",
                conBadge = "Higher Airport Taxi & Ticket Cost"
            ),
            DoorToDoorComparison(
                mode = "BUS",
                modeDisplayName = "Multi-Axle AC Sleeper Bus",
                iconName = "bus",
                homeToStationCabInr = busLocalHomeCab / 2,
                transitTicketPerPersonInr = busTicket,
                stationToHotelCabInr = busDestCab / 2,
                returnMultiplier = 2,
                totalDoorToDoorPerPersonInr = busPerPersonTotal,
                totalForTripInr = busTripTotal,
                travelDurationHours = 15.0,
                comfortScore = 4,
                proBadge = "Direct Pick-up in City Center",
                conBadge = "Highway Traffic & Curvy Ghats"
            )
        )
    }

    private fun calculateTierPlans(request: TripRequest) {
        val nights = if (request.days > 1) request.days - 1 else 1
        val travellers = request.travellers
        val budget = request.budgetInr

        // 1. Economy Plan (Train Sleeper/3A, Backpacker/Oyo Villa, Scooters & Local food)
        val ecoTransport = 1380 * 2 * travellers // Train 3A round trip
        val ecoHotel = 1200 * nights // 1 room for 2
        val ecoLocal = 450 * request.days // Scooters & fuel
        val ecoFood = 700 * request.days * travellers // Shacks & thalis
        val ecoActivities = 600 * travellers // Self exploration & beach entry
        val ecoMisc = 1000
        val ecoBreakdown = CostBreakdown(ecoTransport, ecoHotel, ecoLocal, ecoFood, ecoActivities, ecoMisc)
        val ecoDiff = budget - ecoBreakdown.totalInr

        // 2. Balanced Plan (Train 3A or Promo Flight, 3-Star Resort, Cabs & Water Sports)
        val balTransport = (1650 * 2 * travellers) // Sleeper bus or combo
        val balHotel = 2850 * nights // 3-4 star resort
        val balLocal = 850 * request.days // Private cabs & auto
        val balFood = 1200 * request.days * travellers // Beachfront dining
        val balActivities = 1500 * travellers // Water sports combo
        val balMisc = 1500
        val balBreakdown = CostBreakdown(balTransport, balHotel, balLocal, balFood, balActivities, balMisc)
        val balDiff = budget - balBreakdown.totalInr

        // 3. Premium Luxury Plan (Direct Flight, 5-Star Beach Resort, Chauffeur, Fine Dining)
        val luxTransport = 3850 * 2 * travellers // Flight round trip
        val luxHotel = 8500 * nights // Taj / W Resort
        val luxLocal = 2200 * request.days // Private Innova Chauffeur
        val luxFood = 2500 * request.days * travellers // Fine dining & coastal wine
        val luxActivities = 4000 * travellers // Yacht cruise & scuba
        val luxMisc = 3500
        val luxBreakdown = CostBreakdown(luxTransport, luxHotel, luxLocal, luxFood, luxActivities, luxMisc)
        val luxDiff = budget - luxBreakdown.totalInr

        _tierPlans.value = listOf(
            TierPackagePlan(
                tier = TravelTier.ECONOMY,
                transportModeDesc = "Indian Railways AC 3-Tier Express",
                hotelTierDesc = "Cozy Backpacker Hostel / Beach Cottage",
                breakdown = ecoBreakdown,
                savingsOrExtraInr = ecoDiff,
                savingsBadgeText = if (ecoDiff >= 0) "Save ₹$ecoDiff" else "Exceeds by ₹${-ecoDiff}",
                isRecommended = budget < 20000
            ),
            TierPackagePlan(
                tier = TravelTier.BALANCED,
                transportModeDesc = "Multi-Axle AC Sleeper Bus / Train 2A",
                hotelTierDesc = "4-Star BloomSuites Beach Resort (Candolim)",
                breakdown = balBreakdown,
                savingsOrExtraInr = balDiff,
                savingsBadgeText = if (balDiff >= 0) "Within Budget (Save ₹$balDiff)" else "Requires ₹${-balDiff} more",
                isRecommended = budget in 20000..45000
            ),
            TierPackagePlan(
                tier = TravelTier.PREMIUM_LUXURY,
                transportModeDesc = "IndiGo Direct Flight + Airport Chauffeur",
                hotelTierDesc = "5-Star Taj Fort Aguada Oceanfront Resort",
                breakdown = luxBreakdown,
                savingsOrExtraInr = luxDiff,
                savingsBadgeText = if (luxDiff >= 0) "Luxury Savings ₹$luxDiff" else "VIP Premium (+₹${-luxDiff})",
                isRecommended = budget > 45000
            )
        )

        if (_originalTierPlan.value == null) {
            _originalTierPlan.value = _tierPlans.value.find { it.tier == request.preferredTier }
                ?: _tierPlans.value[1]
        }
    }

    fun replanTrip(
        newBudgetInr: Int,
        newDays: Int,
        newTravellers: Int,
        preferredTransport: String,
        preferredTier: TravelTier
    ) {
        val updatedRequest = _tripRequest.value.copy(
            budgetInr = newBudgetInr,
            days = newDays,
            travellers = newTravellers,
            preferredTier = preferredTier
        )
        _tripRequest.value = updatedRequest
        calculateDoorToDoor(updatedRequest)
        calculateTierPlans(updatedRequest)
        _itinerary.value = TravelDataSource.buildItinerary(updatedRequest.destination, updatedRequest.days)

        val updatedPlan = _tierPlans.value.find { it.tier == preferredTier } ?: _tierPlans.value.first()
        _replannedTierPlan.value = updatedPlan
    }

    fun createBooking(
        type: String,
        title: String,
        routeOrLocation: String,
        amountInr: Int,
        travellersText: String,
        seatOrRoom: String
    ): BookingRecord {
        val randomPnr = "TP-" + UUID.randomUUID().toString().take(6).uppercase()
        val newBooking = BookingRecord(
            id = UUID.randomUUID().toString(),
            pnr = randomPnr,
            title = title,
            type = type,
            routeOrLocation = routeOrLocation,
            travelDate = "18 Nov 2026",
            travellersText = travellersText,
            totalAmountInr = amountInr,
            status = "CONFIRMED",
            seatOrRoomNumber = seatOrRoom,
            qrPayload = "TRIPPILOT-$randomPnr-VERIFIED-$amountInr"
        )
        _bookings.value = listOf(newBooking) + _bookings.value
        return newBooking
    }

    fun cancelBooking(bookingId: String) {
        _bookings.value = _bookings.value.map {
            if (it.id == bookingId) it.copy(status = "CANCELLED") else it
        }
    }
}
