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
import com.example.model.CostBreakdown
import com.example.model.TierPackagePlan
import com.example.model.TravelTier
import com.example.ui.components.SectionHeader
import com.example.ui.components.TripFormatter
import com.example.ui.theme.*

@Composable
fun BudgetOptimizerScreen(
    orchestrator: TripOrchestrator,
    onNavigateToReplan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierPlans by orchestrator.tierPlans.collectAsState()
    val tripRequest by orchestrator.tripRequest.collectAsState()
    var selectedTierIndex by remember { mutableStateOf(1) } // Default to Balanced

    val activePlan = tierPlans.getOrNull(selectedTierIndex) ?: tierPlans.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("budget_optimizer_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "Smart Budget Optimizer",
                subtitle = "Agent 6 dynamically calculates expense allocations & maximizes INR savings",
                badgeText = "Target: ${TripFormatter.formatInr(tripRequest.budgetInr)}"
            )
        }

        // Tier Selection Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tierPlans.forEachIndexed { index, plan ->
                    val isSelected = selectedTierIndex == index
                    val (tierColor, tierIcon) = when (plan.tier) {
                        TravelTier.ECONOMY -> Pair(EmeraldGreen, Icons.Default.Savings)
                        TravelTier.BALANCED -> Pair(ElectricBlue, Icons.Default.Balance)
                        TravelTier.PREMIUM_LUXURY -> Pair(AmberGold, Icons.Default.Diamond)
                    }

                    Surface(
                        color = if (isSelected) tierColor.copy(alpha = 0.2f) else NavyCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) tierColor else BorderSubtle
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTierIndex = index }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(tierIcon, contentDescription = null, tint = if (isSelected) tierColor else TextSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = plan.tier.name.take(4).capitalize(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹${plan.breakdown.totalInr}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = tierColor
                            )
                        }
                    }
                }
            }
        }

        // Active Plan Overview Card
        if (activePlan != null) {
            item {
                ActivePlanCard(
                    plan = activePlan,
                    targetBudgetInr = tripRequest.budgetInr,
                    travellers = tripRequest.travellers,
                    days = tripRequest.days
                )
            }

            // Visual Expense Breakdown Bars
            item {
                ExpenseBreakdownCard(
                    breakdown = activePlan.breakdown,
                    total = activePlan.breakdown.totalInr
                )
            }
        }

        // Replan Trip CTA Card
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalViolet.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = RoyalViolet.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = VioletSoft, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Dynamic Trip Replanning",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Want to reduce budget to ₹18k or upgrade to ₹40k?",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToReplan,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_replan_cta")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Replan My Trip & Compare Diff", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivePlanCard(
    plan: TierPackagePlan,
    targetBudgetInr: Int,
    travellers: Int,
    days: Int
) {
    val (tierColor, badgeLabel) = when (plan.tier) {
        TravelTier.ECONOMY -> Pair(EmeraldGreen, "BUDGET SAVER")
        TravelTier.BALANCED -> Pair(ElectricBlue, "RECOMMENDED BALANCED")
        TravelTier.PREMIUM_LUXURY -> Pair(AmberGold, "5-STAR LUXURY")
    }

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, tierColor.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = tierColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        color = tierColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = plan.savingsBadgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (plan.savingsOrExtraInr >= 0) EmeraldLight else AmberGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = plan.tier.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = plan.tier.subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = TripFormatter.formatInr(plan.breakdown.totalInr),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = tierColor
                    )
                    Text(
                        text = "Total for $travellers people ($days d)",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = Navy850,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(plan.transportModeDesc, fontSize = 12.sp, color = TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Hotel, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(plan.hotelTierDesc, fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseBreakdownCard(
    breakdown: CostBreakdown,
    total: Int
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Itemized Expense Breakdown (INR)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            val items = listOf(
                Pair("Transport (Flight/Train/Bus)", Pair(breakdown.transportInr, ElectricBlue)),
                Pair("Hotel & Stays", Pair(breakdown.hotelInr, RoyalViolet)),
                Pair("Local Transport (Cabs & Auto)", Pair(breakdown.localTransitInr, CyanAccent)),
                Pair("Food & Coastal Dining", Pair(breakdown.foodInr, AmberGold)),
                Pair("Sightseeing & Adventures", Pair(breakdown.activitiesInr, EmeraldGreen)),
                Pair("Emergency & Misc Buffer", Pair(breakdown.miscBufferInr, TextSecondary))
            )

            items.forEach { (label, data) ->
                val (amount, color) = data
                val percentage = if (total > 0) (amount.toFloat() / total.toFloat()) else 0f

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "₹$amount (${(percentage * 100).toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = color,
                        trackColor = Navy850
                    )
                }
            }
        }
    }
}
