package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.TierPackagePlan
import com.example.model.TravelTier
import com.example.ui.components.SectionHeader
import com.example.ui.components.TierChip
import com.example.ui.components.TripFormatter
import com.example.ui.theme.*

@Composable
fun ReplanTripScreen(
    orchestrator: TripOrchestrator,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tripRequest by orchestrator.tripRequest.collectAsState()
    val originalPlan by orchestrator.originalTierPlan.collectAsState()
    val replannedPlan by orchestrator.replannedTierPlan.collectAsState()

    var newBudgetInr by remember { mutableStateOf(tripRequest.budgetInr) }
    var newDays by remember { mutableStateOf(tripRequest.days) }
    var newTravellers by remember { mutableStateOf(tripRequest.travellers) }
    var preferredTier by remember { mutableStateOf(tripRequest.preferredTier) }
    var preferredTransport by remember { mutableStateOf("Any") }

    val activeComparison = replannedPlan ?: originalPlan

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("replan_trip_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back Row
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                SectionHeader(
                    title = "REPLAN MY TRIP",
                    subtitle = "Modify budget or travelers and see live re-evaluation diff",
                    badgeText = "Live Agent Diff"
                )
            }
        }

        // Sliders & Controls
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Modify Trip Constraints",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Budget Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Adjust Budget (INR)", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = TripFormatter.formatInr(newBudgetInr),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberGold
                            )
                        }
                        Slider(
                            value = newBudgetInr.toFloat(),
                            onValueChange = {
                                newBudgetInr = it.toInt()
                                orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                            },
                            valueRange = 10000f..80000f,
                            steps = 14,
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
                            Text("₹10k (Strict Economy)", fontSize = 10.sp, color = TextMuted)
                            Text("₹25k (Balanced)", fontSize = 10.sp, color = TextMuted)
                            Text("₹80k (Luxury)", fontSize = 10.sp, color = TextMuted)
                        }
                    }

                    // Quick Budget Cut Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15000, 20000, 25000, 35000).forEach { cutVal ->
                            Surface(
                                color = if (newBudgetInr == cutVal) ElectricBlue else Navy850,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        newBudgetInr = cutVal
                                        orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                                    }
                            ) {
                                Text(
                                    text = "₹${cutVal / 1000}k",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (newBudgetInr == cutVal) TextPrimary else TextSecondary,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    // Days & Travellers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Days: $newDays", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Navy850, RoundedCornerShape(8.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    if (newDays > 2) {
                                        newDays--
                                        orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                                    }
                                }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = TextPrimary)
                                }
                                Text("$newDays d", fontWeight = FontWeight.Bold, color = CyanAccent, fontSize = 12.sp)
                                IconButton(onClick = {
                                    if (newDays < 10) {
                                        newDays++
                                        orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                                    }
                                }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Travellers: $newTravellers", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Navy850, RoundedCornerShape(8.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    if (newTravellers > 1) {
                                        newTravellers--
                                        orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                                    }
                                }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = TextPrimary)
                                }
                                Text("$newTravellers", fontWeight = FontWeight.Bold, color = RoyalViolet, fontSize = 12.sp)
                                IconButton(onClick = {
                                    if (newTravellers < 6) {
                                        newTravellers++
                                        orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                                    }
                                }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary)
                                }
                            }
                        }
                    }

                    // Tier Selection
                    Column {
                        Text("Hotel & Transport Tier", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TravelTier.values().forEach { tier ->
                                TierChip(
                                    tier = tier,
                                    isSelected = preferredTier == tier,
                                    onClick = {
                                        preferredTier = tier
                                        orchestrator.replanTrip(newBudgetInr, newDays, newTravellers, preferredTransport, preferredTier)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Before vs After Comparison Diff Card
        if (originalPlan != null && activeComparison != null) {
            item {
                Text(
                    text = "Original Plan vs Re-planned Plan Diff",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                PlanDiffCard(
                    original = originalPlan!!,
                    updated = activeComparison,
                    originalBudget = tripRequest.budgetInr,
                    newBudget = newBudgetInr
                )
            }
        }
    }
}

@Composable
fun PlanDiffCard(
    original: TierPackagePlan,
    updated: TierPackagePlan,
    originalBudget: Int,
    newBudget: Int
) {
    val totalCostDiff = updated.breakdown.totalInr - original.breakdown.totalInr
    val budgetChange = newBudget - originalBudget

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanAccent.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Net savings highlight banner
            Surface(
                color = if (totalCostDiff <= 0) EmeraldGreen.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (totalCostDiff <= 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = if (totalCostDiff <= 0) EmeraldLight else AmberGold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (totalCostDiff <= 0)
                                "Net Cost Reduction: ₹${-totalCostDiff} Saved!"
                            else
                                "Upgrade Cost: +₹$totalCostDiff",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (totalCostDiff <= 0) EmeraldLight else AmberGold
                        )
                        Text(
                            text = if (updated.savingsOrExtraInr >= 0)
                                "Updated plan is fully within the new ₹$newBudget budget."
                            else
                                "Updated plan requires ₹${-updated.savingsOrExtraInr} buffer.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two-column side-by-side diff
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Original Plan Column
                Surface(
                    color = Navy850,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ORIGINAL PLAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(original.tier.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Budget: ₹$originalBudget", fontSize = 11.sp, color = TextSecondary)
                        Divider(color = BorderSubtle, thickness = 0.5.dp)
                        Text("Total: ₹${original.breakdown.totalInr}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text("Transport: ₹${original.breakdown.transportInr}", fontSize = 10.sp, color = TextSecondary)
                        Text("Hotel: ₹${original.breakdown.hotelInr}", fontSize = 10.sp, color = TextSecondary)
                        Text("Food: ₹${original.breakdown.foodInr}", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                // Re-planned Column
                Surface(
                    color = Navy850,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("RE-PLANNED PLAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        Text(updated.tier.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Budget: ₹$newBudget", fontSize = 11.sp, color = AmberGold)
                        Divider(color = BorderSubtle, thickness = 0.5.dp)
                        Text("Total: ₹${updated.breakdown.totalInr}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = CyanAccent)
                        Text("Transport: ₹${updated.breakdown.transportInr}", fontSize = 10.sp, color = TextSecondary)
                        Text("Hotel: ₹${updated.breakdown.hotelInr}", fontSize = 10.sp, color = TextSecondary)
                        Text("Food: ₹${updated.breakdown.foodInr}", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
