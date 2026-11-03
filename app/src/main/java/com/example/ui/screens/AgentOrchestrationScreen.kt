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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.TripOrchestrator
import com.example.model.AgentState
import com.example.model.AgentStatus
import com.example.model.AgentType
import com.example.ui.components.AgentStatusBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun AgentOrchestrationScreen(
    orchestrator: TripOrchestrator,
    onNavigateToTransport: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToItinerary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val agentsMap by orchestrator.agentsState.collectAsState()
    val isOrchestrating by orchestrator.isOrchestrating.collectAsState()
    val overallProgress by orchestrator.overallProgress.collectAsState()
    val tripRequest by orchestrator.tripRequest.collectAsState()

    var expandedAgentType by remember { mutableStateOf<AgentType?>(AgentType.TRAVEL_PLANNER) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("agents_dashboard_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Orchestration Header & Overall Progress
        item {
            Column {
                SectionHeader(
                    title = "Multi-Agent Collaboration Engine",
                    subtitle = "7 intelligent agents coordinating live logistics & budget optimization",
                    badgeText = if (isOrchestrating) "ORCHESTRATING" else "READY"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Trip: ${tripRequest.origin} ➔ ${tripRequest.destination}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${(overallProgress * 100).toInt()}% Done",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (overallProgress >= 1f) EmeraldLight else CyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { overallProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ElectricBlue,
                            trackColor = Navy850
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isOrchestrating)
                                "Parallel Execution: Flight, IRCTC & Bus, and Hotel Agents searching concurrently..."
                            else
                                "Collaboration complete. All 7 agents have consolidated trip plans.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Quick Navigation to Results
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToTransport,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Transport", fontSize = 12.sp, color = TextPrimary)
                }

                Button(
                    onClick = onNavigateToBudget,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Budget", fontSize = 12.sp, color = TextPrimary)
                }

                Button(
                    onClick = onNavigateToItinerary,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalViolet.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = VioletSoft, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Itinerary", fontSize = 12.sp, color = TextPrimary)
                }
            }
        }

        // Agents List
        items(AgentType.values()) { type ->
            val state = agentsMap[type] ?: AgentState(type)
            val isExpanded = expandedAgentType == type

            AgentCard(
                state = state,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedAgentType = if (isExpanded) null else type
                }
            )
        }
    }
}

@Composable
fun AgentCard(
    state: AgentState,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val (icon, accentColor) = when (state.type) {
        AgentType.TRAVEL_PLANNER -> Pair(Icons.Default.Psychology, CyanAccent)
        AgentType.DESTINATION -> Pair(Icons.Default.Place, RoyalViolet)
        AgentType.FLIGHT -> Pair(Icons.Default.Flight, ElectricBlueLight)
        AgentType.HOTEL -> Pair(Icons.Default.Hotel, AmberGold)
        AgentType.RAILWAY_BUS -> Pair(Icons.Default.DirectionsTransit, EmeraldGreen)
        AgentType.BUDGET_OPTIMIZER -> Pair(Icons.Default.TrendingDown, EmeraldLight)
        AgentType.ITINERARY_SUPPORT -> Pair(Icons.Default.EventNote, VioletSoft)
    }

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) accentColor.copy(alpha = 0.7f) else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .testTag("agent_card_${state.type.name}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.type.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = state.type.role,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                AgentStatusBadge(status = state.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current task or completed summary
            Text(
                text = if (state.completedResultSummary.isNotEmpty())
                    "✓ ${state.completedResultSummary}"
                else
                    "Task: ${state.currentTask}",
                fontSize = 12.sp,
                fontWeight = if (state.completedResultSummary.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                color = if (state.completedResultSummary.isNotEmpty()) EmeraldLight else TextSecondary,
                lineHeight = 16.sp
            )

            // Progress bar if active
            if (state.status == AgentStatus.ANALYZING || state.status == AgentStatus.SEARCHING || state.status == AgentStatus.OPTIMIZING) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = accentColor,
                    trackColor = Navy850
                )
            }

            // Expandable Activity Logs
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = BorderSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Real-time Agent Activity Logs:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (state.activityLogs.isEmpty()) {
                        Text(
                            text = "No recorded log events yet.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    } else {
                        state.activityLogs.forEach { log ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text("› ", color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = log,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
