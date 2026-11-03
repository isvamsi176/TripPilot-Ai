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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.TripOrchestrator
import com.example.model.DayItinerary
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun ItineraryScreen(
    orchestrator: TripOrchestrator,
    modifier: Modifier = Modifier
) {
    val itineraryList by orchestrator.itinerary.collectAsState()
    val tripRequest by orchestrator.tripRequest.collectAsState()
    var expandedDay by remember { mutableStateOf(1) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("itinerary_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "AI-Generated Itinerary",
                subtitle = "${tripRequest.days}-Day Curated Experience for ${tripRequest.destination} by Agent 7",
                badgeText = "Day-by-Day"
            )
        }

        // Summary Card
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = RoyalViolet.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VioletSoft, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${tripRequest.origin} ➔ ${tripRequest.destination}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Paced for leisure, water sports & cultural highlights",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Days List
        items(itineraryList) { day ->
            DayItineraryCard(
                day = day,
                isExpanded = expandedDay == day.dayNumber,
                onToggle = {
                    expandedDay = if (expandedDay == day.dayNumber) 0 else day.dayNumber
                }
            )
        }

        // Smart Packing Checklist for India
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Checklist, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Packing & Essentials for India",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Divider(color = BorderSubtle, thickness = 0.5.dp)
                    val checklist = listOf(
                        "✓ Original Gov ID (Aadhaar/Passport) for IRCTC & Flight Boarding",
                        "✓ UPI Apps (Google Pay / PhonePe) with offline cash backup (₹3,000)",
                        "✓ High SPF Sunscreen, polarized sunglasses & linen/cotton apparel",
                        "✓ Universal power bank & beach waterproof phone pouch",
                        "✓ Downloaded offline offline maps & emergency helpline 112"
                    )
                    checklist.forEach { itemText ->
                        Text(itemText, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DayItineraryCard(
    day: DayItinerary,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) RoyalViolet.copy(alpha = 0.6f) else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("day_card_${day.dayNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = RoyalViolet,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "D${day.dayNumber}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Day ${day.dayNumber}: ${day.title}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Est. Day Expenses: ₹${day.dayExpenseEstimateInr}",
                        fontSize = 11.sp,
                        color = AmberGold
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Divider(color = BorderSubtle, thickness = 0.5.dp)

                    // Morning
                    ScheduleSlot(
                        timeSlot = "Morning",
                        description = day.morning,
                        icon = Icons.Default.WbSunny,
                        iconColor = AmberGold
                    )

                    // Afternoon
                    ScheduleSlot(
                        timeSlot = "Afternoon",
                        description = day.afternoon,
                        icon = Icons.Default.LightMode,
                        iconColor = CyanAccent
                    )

                    // Evening
                    ScheduleSlot(
                        timeSlot = "Evening",
                        description = day.evening,
                        icon = Icons.Default.NightsStay,
                        iconColor = VioletSoft
                    )

                    // Dinner Recommendation
                    Surface(
                        color = Navy850,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Curated Dining Spot", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(day.dinnerSpot, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Insider Tip
                    Surface(
                        color = EmeraldGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Local Insider Hack", fontSize = 10.sp, color = EmeraldLight, fontWeight = FontWeight.Bold)
                                Text(day.insiderTip, fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleSlot(
    timeSlot: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(timeSlot, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = iconColor)
            Text(description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}
