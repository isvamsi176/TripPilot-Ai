package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.TripOrchestrator
import com.example.model.*
import com.example.ui.components.SectionHeader
import com.example.ui.components.TripFormatter
import com.example.ui.theme.*

@Composable
fun TransportComparisonScreen(
    orchestrator: TripOrchestrator,
    onBookTransport: (type: String, title: String, route: String, amount: Int, seat: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val doorToDoorList by orchestrator.doorToDoorComparisons.collectAsState()
    val flights by orchestrator.flights.collectAsState()
    val trains by orchestrator.trains.collectAsState()
    val buses by orchestrator.buses.collectAsState()
    val tripRequest by orchestrator.tripRequest.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Door-to-Door, 1: Trains, 2: Flights, 3: Buses
    val tabTitles = listOf("Door-to-Door", "Trains (IRCTC)", "Flights", "Buses")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("transport_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "Transport Comparison & Analysis",
                subtitle = "Complete analysis from your doorstep to hotel and return (${tripRequest.origin} ➔ ${tripRequest.destination})",
                badgeText = "All Modes in ₹"
            )
        }

        // Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavyCard,
                contentColor = TextPrimary,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = ElectricBlue
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) CyanAccent else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Door to Door Comparison
                item {
                    Text(
                        text = "Door-to-Door Cost Breakdown (${tripRequest.travellers} Travellers)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Includes Home Cab + Transit Round-Trip + Terminal-to-Hotel Transfers",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                items(doorToDoorList) { item ->
                    DoorToDoorCard(
                        item = item,
                        travellers = tripRequest.travellers,
                        onBook = {
                            onBookTransport(
                                item.modeDisplayName,
                                "${item.modeDisplayName} Round Trip",
                                "${tripRequest.origin} ➔ ${tripRequest.destination}",
                                item.totalForTripInr,
                                "Confirmed Reservation"
                            )
                        }
                    )
                }
            }

            1 -> {
                // Indian Railways IRCTC
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Train, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Indian Railways Schedules & Fares",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                items(trains) { train ->
                    TrainOptionCard(
                        train = train,
                        onBook = {
                            onBookTransport(
                                "Train",
                                "${train.trainNumber} ${train.trainName} (${train.travelClass})",
                                "${train.fromStation} ➔ ${train.toStation}",
                                train.fareInr * tripRequest.travellers,
                                "Coach B3, Berths 21, 24"
                            )
                        }
                    )
                }
            }

            2 -> {
                // Flights
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Flight, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Domestic Direct Flights",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                items(flights) { flight ->
                    FlightOptionCard(
                        flight = flight,
                        onBook = {
                            onBookTransport(
                                "Flight",
                                "${flight.airline} ${flight.flightNumber}",
                                "${flight.originCode} ➔ ${flight.destCode}",
                                flight.pricePerPersonInr * tripRequest.travellers,
                                "Seats 14A, 14B"
                            )
                        }
                    )
                }
            }

            3 -> {
                // Buses
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = RoyalViolet.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = VioletSoft, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Private & State Luxury Sleeper Buses",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                items(buses) { bus ->
                    BusOptionCard(
                        bus = bus,
                        onBook = {
                            onBookTransport(
                                "Bus",
                                "${bus.operator} - ${bus.busType.take(20)}",
                                "${bus.boardingPoint} ➔ ${bus.droppingPoint}",
                                bus.fareInr * tripRequest.travellers,
                                "Upper Sleeper U4, U5"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DoorToDoorCard(
    item: DoorToDoorComparison,
    travellers: Int,
    onBook: () -> Unit
) {
    val (accentColor, modeIcon) = when (item.mode) {
        "FLIGHT" -> Pair(ElectricBlue, Icons.Default.Flight)
        "TRAIN" -> Pair(EmeraldGreen, Icons.Default.Train)
        else -> Pair(RoyalViolet, Icons.Default.DirectionsBus)
    }

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = accentColor.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = modeIcon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.modeDisplayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Total Travel Time: ${item.travelDurationHours}h",
                        fontSize = 12.sp,
                        color = CyanAccent
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = TripFormatter.formatInr(item.totalForTripInr),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberGold
                    )
                    Text(
                        text = "for $travellers people",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-step breakdown container
            Surface(
                color = Navy850,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1. Home ➔ Departure Terminal (Cab/Auto)", fontSize = 11.sp, color = TextSecondary)
                        Text("₹${item.homeToStationCabInr * 2}", fontSize = 11.sp, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("2. Round-Trip Transit Tickets (x$travellers)", fontSize = 11.sp, color = TextSecondary)
                        Text("₹${item.transitTicketPerPersonInr * travellers}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("3. Arrival Terminal ➔ Hotel Transfer", fontSize = 11.sp, color = TextSecondary)
                        Text("₹${item.stationToHotelCabInr * 2}", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pros / Cons Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "✓ ${item.proBadge}",
                        fontSize = 10.sp,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }

                Surface(
                    color = AmberGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "! ${item.conBadge}",
                        fontSize = 10.sp,
                        color = AmberLight,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBook,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select & Book ${item.modeDisplayName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun TrainOptionCard(
    train: TrainOption,
    onBook: () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = train.trainNumber,
                                color = EmeraldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = train.trainName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Class: ${train.travelClass} • ${train.durationText}",
                        fontSize = 12.sp,
                        color = CyanAccent
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${train.fareInr}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberGold
                    )
                    Text(
                        text = "per berth",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timings Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy850, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(train.departureTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(train.fromStation, fontSize = 10.sp, color = TextSecondary)
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(train.arrivalTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(train.toStation, fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "IRCTC Status: ${train.availabilityStatus} (${train.confirmationProbability})",
                        color = EmeraldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = onBook,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Berth", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FlightOptionCard(
    flight: FlightOption,
    onBook: () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${flight.airline} (${flight.flightNumber})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${flight.cabinClass} • On-time: ${flight.onTimeRating}",
                        fontSize = 12.sp,
                        color = CyanAccent
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${flight.pricePerPersonInr}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberGold
                    )
                    Text(
                        text = "per seat",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timings Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy850, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(flight.departureTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(flight.originCode, fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(flight.durationText, fontSize = 10.sp, color = TextMuted)
                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(16.dp))
                    Text("Non-stop", fontSize = 10.sp, color = EmeraldLight)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(flight.arrivalTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(flight.destCode, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Baggage: ${flight.baggageAllowance}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Flight", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BusOptionCard(
    bus: BusOption,
    onBook: () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bus.operator,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = bus.busType,
                        fontSize = 12.sp,
                        color = VioletSoft,
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${bus.fareInr}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberGold
                    )
                    Text(
                        text = "${bus.seatsAvailable} seats left",
                        fontSize = 10.sp,
                        color = EmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timings Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy850, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(bus.departureTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(bus.boardingPoint, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(bus.durationText, fontSize = 10.sp, color = TextMuted)
                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = RoyalViolet, modifier = Modifier.size(16.dp))
                    Text("★ ${bus.rating}", fontSize = 10.sp, color = AmberGold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(bus.arrivalTime, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(bus.droppingPoint, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = RoyalViolet.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "AC Sleeper • Free Water & Blanket",
                        color = VioletSoft,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = onBook,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VioletSoft),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VioletSoft),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Sleeper", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
