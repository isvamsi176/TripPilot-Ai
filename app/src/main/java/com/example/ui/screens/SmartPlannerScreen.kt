package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.TravelTier
import com.example.model.TripRequest
import com.example.ui.components.SectionHeader
import com.example.ui.components.TierChip
import com.example.ui.components.TripFormatter
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartPlannerScreen(
    orchestrator: TripOrchestrator,
    scope: CoroutineScope,
    onOrchestrationStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var naturalLanguageInput by remember {
        mutableStateOf("I want to travel from Hyderabad to Goa for 5 days with a budget of ₹25,000 for two people.")
    }

    var origin by remember { mutableStateOf("Hyderabad") }
    var destination by remember { mutableStateOf("Goa") }
    var days by remember { mutableStateOf(5) }
    var travellers by remember { mutableStateOf(2) }
    var budgetInr by remember { mutableStateOf(25000) }
    var selectedTier by remember { mutableStateOf(TravelTier.BALANCED) }

    val allPreferences = listOf("Beaches", "Sightseeing", "Adventure", "Nightlife", "Heritage", "Relaxation")
    var selectedPreferences by remember { mutableStateOf(listOf("Beaches", "Sightseeing", "Adventure")) }

    val presetQueries = listOf(
        "Hyderabad to Goa • 5 Days • 2p • ₹25,000",
        "Jaipur Royal Heritage • 4 Days • 2p • ₹18,000",
        "Manali Snow Adventure • 6 Days • 2p • ₹32,000"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("smart_planner_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            SectionHeader(
                title = "Conversational AI Trip Planner",
                subtitle = "Agent 1 (Travel Planner) parses your intent & dispatches logistics engines",
                badgeText = "Agent Orchestrated"
            )
        }

        // Natural Language Prompt Card
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ElectricBlue, RoyalViolet))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Natural Language Trip Prompt",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = naturalLanguageInput,
                        onValueChange = { naturalLanguageInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prompt_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = Navy850,
                            unfocusedContainerColor = Navy850,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Presets:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetQueries) { query ->
                            Surface(
                                color = Navy850,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.clickable {
                                    naturalLanguageInput = query
                                    if (query.contains("Goa")) {
                                        origin = "Hyderabad"; destination = "Goa"; days = 5; travellers = 2; budgetInr = 25000
                                    } else if (query.contains("Jaipur")) {
                                        origin = "Delhi"; destination = "Jaipur"; days = 4; travellers = 2; budgetInr = 18000
                                    } else if (query.contains("Manali")) {
                                        origin = "Chandigarh"; destination = "Manali"; days = 6; travellers = 2; budgetInr = 32000
                                    }
                                }
                            ) {
                                Text(
                                    text = query,
                                    fontSize = 11.sp,
                                    color = CyanAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Granular Trip Parameters
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Trip Parameters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Origin & Destination
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Origin City", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Navy850,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = ElectricBlueLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(origin, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Destination", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Navy850,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.FlightLand, contentDescription = null, tint = RoyalViolet, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(destination, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }

                    // Duration & Travellers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Days Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Duration: $days Days", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Navy850, RoundedCornerShape(10.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (days > 2) days-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                                }
                                Text("$days d", fontWeight = FontWeight.Bold, color = CyanAccent)
                                IconButton(
                                    onClick = { if (days < 14) days++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                                }
                            }
                        }

                        // Travellers Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Travellers: $travellers Person(s)", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Navy850, RoundedCornerShape(10.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (travellers > 1) travellers-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                                }
                                Text("$travellers", fontWeight = FontWeight.Bold, color = RoyalViolet)
                                IconButton(
                                    onClick = { if (travellers < 8) travellers++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                                }
                            }
                        }
                    }

                    // Target Budget in INR
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Trip Budget (INR)", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = TripFormatter.formatInr(budgetInr),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberGold
                            )
                        }
                        Slider(
                            value = budgetInr.toFloat(),
                            onValueChange = { budgetInr = it.toInt() },
                            valueRange = 10000f..100000f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = AmberGold,
                                activeTrackColor = AmberGold,
                                inactiveTrackColor = Navy850
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("₹10,000 (Backpacker)", fontSize = 10.sp, color = TextMuted)
                            Text("₹50,000", fontSize = 10.sp, color = TextMuted)
                            Text("₹1,00,000 (Luxury)", fontSize = 10.sp, color = TextMuted)
                        }
                    }

                    // Preferred Tier
                    Column {
                        Text("Travel Style Tier", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TravelTier.values().forEach { tier ->
                                TierChip(
                                    tier = tier,
                                    isSelected = selectedTier == tier,
                                    onClick = { selectedTier = tier }
                                )
                            }
                        }
                    }

                    // Preferences
                    Column {
                        Text("Vibes & Interests", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(allPreferences) { pref ->
                                val isSelected = selectedPreferences.contains(pref)
                                Surface(
                                    color = if (isSelected) RoyalViolet.copy(alpha = 0.25f) else Navy850,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) RoyalViolet else BorderSubtle
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedPreferences = if (isSelected) {
                                            selectedPreferences - pref
                                        } else {
                                            selectedPreferences + pref
                                        }
                                    }
                                ) {
                                    Text(
                                        text = pref,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) VioletSoft else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Orchestrator Action CTA
        item {
            Button(
                onClick = {
                    val req = TripRequest(
                        origin = origin,
                        destination = destination,
                        days = days,
                        travellers = travellers,
                        budgetInr = budgetInr,
                        preferences = selectedPreferences,
                        preferredTier = selectedTier
                    )
                    orchestrator.startOrchestration(req, scope) {
                        onOrchestrationStarted()
                    }
                    onOrchestrationStarted()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_orchestration_cta")
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Coordinate 7 AI Agents & Optimize",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
