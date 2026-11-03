package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.TripOrchestrator
import com.example.ai.AiServiceResult
import com.example.ai.GeminiTravelService
import com.example.model.TravelDisruption
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SupportScreen(
    orchestrator: TripOrchestrator,
    geminiService: GeminiTravelService,
    modifier: Modifier = Modifier
) {
    val disruptions by orchestrator.disruptions.collectAsState()
    val scope = rememberCoroutineScope()

    var userCustomQuestion by remember { mutableStateOf("") }
    var aiAnswer by remember { mutableStateOf<String?>(null) }
    var isAskingAi by remember { mutableStateOf(false) }

    val quickQuestions = listOf(
        "IndiGo baggage & cancellation policy",
        "How to reach Candolim from Madgaon?",
        "Best vegetarian food spots in Goa",
        "IRCTC train delay refund rules"
    )

    val faqs = listOf(
        Pair(
            "What happens if my Indian Railway train is delayed by over 3 hours?",
            "Under Indian Railways (IRCTC) TDR rules, if a train is delayed by more than 3 hours, passengers are entitled to a full refund without cancellation charges if TDR is filed before departure."
        ),
        Pair(
            "How do I commute from Madgaon Junction (MAO) to Candolim / North Goa?",
            "You have three reliable options: 1) GoaMiles App Prepaid Taxi (~₹1,200), 2) Kadamba AC Electric Shuttle to Panjim (₹80) + local bus, or 3) Private rental car delivered at station."
        ),
        Pair(
            "Are international credit/debit cards accepted in local beach shacks?",
            "UPI (Google Pay, PhonePe, Paytm) is accepted everywhere, even at small coconut water stalls. International cards are accepted in resorts and larger cafes, but carrying ₹2,000 in cash is advised."
        ),
        Pair(
            "What is TripPilot AI's Re-routing assistance?",
            "If a flight or train is delayed, Agent 7 automatically alerts you and recalculates hotel check-in buffers and adjusts your day itinerary to avoid missed reservations."
        )
    )

    var expandedFaqIndex by remember { mutableStateOf<Int?>(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("support_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Travel Support & Live Disruption Center",
                subtitle = "Agent 7 24/7 monitoring, real-time safety advisories & Gemini AI assist",
                badgeText = "Live Advisory"
            )
        }

        // Live Gemini Travel Assistant Card
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ElectricBlue, RoyalViolet))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = RoyalViolet.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VioletSoft, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Ask Gemini Travel Assistant", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Powered by gemini-3.8-flash", fontSize = 10.sp, color = CyanAccent)
                            }
                        }

                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("AI ONLINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userCustomQuestion,
                        onValueChange = { userCustomQuestion = it },
                        placeholder = { Text("Ask about flights, IRCTC, local transport or food...", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick question chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickQuestions) { q ->
                            Surface(
                                color = Navy850,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.clickable {
                                    userCustomQuestion = q
                                }
                            ) {
                                Text(
                                    text = q,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (userCustomQuestion.isNotBlank()) {
                                isAskingAi = true
                                scope.launch {
                                    val res = geminiService.askTravelAssistant(userCustomQuestion)
                                    isAskingAi = false
                                    when (res) {
                                        is AiServiceResult.Success -> aiAnswer = res.data
                                        is AiServiceResult.Error -> aiAnswer = "Notice: ${res.message}"
                                    }
                                }
                            }
                        },
                        enabled = !isAskingAi && userCustomQuestion.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isAskingAi) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini 3.8 Flash is thinking...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Get Instant AI Answer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Display AI Answer
                    if (aiAnswer != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Navy850,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gemini Travel Assistant Response:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = aiAnswer!!,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Emergency Helplines for India
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Official Indian Helplines",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HelplinePill(label = "National Emergency", number = "112", color = CrimsonAlert, modifier = Modifier.weight(1f))
                        HelplinePill(label = "Railway IRCTC", number = "139", color = EmeraldGreen, modifier = Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HelplinePill(label = "Tourist Police", number = "1363", color = CyanAccent, modifier = Modifier.weight(1f))
                        HelplinePill(label = "Coastal Guard", number = "1554", color = AmberGold, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Live Disruption Notices
        item {
            Text(
                text = "Active Trip Disruptions & Alerts (${disruptions.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(disruptions) { disruption ->
            DisruptionCard(disruption = disruption)
        }

        // AI Travel Support FAQ
        item {
            Text(
                text = "Instant Travel Support FAQs",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(faqs.indices.toList()) { index ->
            val (q, a) = faqs[index]
            val isExpanded = expandedFaqIndex == index

            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isExpanded) CyanAccent.copy(alpha = 0.5f) else BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedFaqIndex = if (isExpanded) null else index }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = q,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CyanAccent
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Divider(color = BorderSubtle, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = a,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelplinePill(
    label: String,
    number: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Navy850,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, color = TextMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(number, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
            }
        }
    }
}

@Composable
fun DisruptionCard(disruption: TravelDisruption) {
    val (statusColor, icon) = when (disruption.severity) {
        "Alert" -> Pair(CrimsonAlert, Icons.Default.Warning)
        "Notice" -> Pair(AmberGold, Icons.Default.Info)
        else -> Pair(EmeraldGreen, Icons.Default.CheckCircle)
    }

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = disruption.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = disruption.timeAgo,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = disruption.message,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Navy850,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Action: ${disruption.recommendedAction}",
                    fontSize = 11.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
