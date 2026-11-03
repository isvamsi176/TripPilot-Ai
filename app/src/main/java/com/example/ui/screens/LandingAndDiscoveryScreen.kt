package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.TripOrchestrator
import com.example.data.TravelDataSource
import com.example.model.DestinationInfo
import com.example.model.TripRequest
import com.example.ui.components.SectionHeader
import com.example.ui.components.TripFormatter
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope

@Composable
fun LandingAndDiscoveryScreen(
    orchestrator: TripOrchestrator,
    scope: CoroutineScope,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAgents: () -> Unit,
    onSelectDestination: (DestinationInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedVibe by remember { mutableStateOf("All") }
    val vibes = listOf("All", "Beaches", "Heritage", "Snow Peaks", "Adventure", "Tech Hub")

    val filteredDestinations = remember(selectedVibe) {
        if (selectedVibe == "All") TravelDataSource.destinations
        else TravelDataSource.destinations.filter { it.vibeTags.any { tag -> tag.contains(selectedVibe, ignoreCase = true) } }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .testTag("landing_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(NavyCardElevated, Navy900)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = RoyalViolet.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalViolet),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TravelExplore,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TRIPPILOT AI",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Plan Smarter. Travel Better. Experience More.",
                                fontSize = 12.sp,
                                color = CyanAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hackathon 1-Tap Scenario Card
                    Surface(
                        color = NavyCard,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(ElectricBlue, RoyalViolet))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hackathon_demo_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = EmeraldGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "★ HACKATHON DEMO SCENARIO",
                                        color = EmeraldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "₹25,000 for 2",
                                    color = AmberGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Hyderabad ➔ Goa (5 Days)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Beaches, Sightseeing & Adventure with Door-to-Door Train vs Flight vs Bus analysis.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    orchestrator.startOrchestration(
                                        TripRequest(
                                            origin = "Hyderabad",
                                            destination = "Goa",
                                            days = 5,
                                            travellers = 2,
                                            budgetInr = 25000,
                                            preferences = listOf("Beaches", "Sightseeing", "Adventure")
                                        ),
                                        scope
                                    ) {
                                        onNavigateToAgents()
                                    }
                                    onNavigateToAgents()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBlue
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("run_demo_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Launch 7-Agent AI Orchestrator", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Vibe Filters
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "Explore by Vibe",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(vibes) { vibe ->
                        val isSelected = vibe == selectedVibe
                        Surface(
                            color = if (isSelected) ElectricBlue else NavyCard,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyanAccent else BorderSubtle
                            ),
                            modifier = Modifier.clickable { selectedVibe = vibe }
                        ) {
                            Text(
                                text = vibe,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Featured Destinations with Real Generated Images
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                PaddingValues(horizontal = 20.dp).let {
                    SectionHeader(
                        title = "Featured Indian Destinations",
                        subtitle = "High-definition real destinations with AI seasonal scoring",
                        badgeText = "Live Pricing in ₹",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredDestinations) { dest ->
                        DestinationCard(
                            dest = dest,
                            onSelect = {
                                onSelectDestination(dest)
                            }
                        )
                    }
                }
            }
        }

        // Tier Packages: Middle Class & Luxury
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                SectionHeader(
                    title = "Trips for Every Traveler",
                    subtitle = "Tailored for smart middle-class savings & high-end luxury",
                    badgeText = "Tier Curated"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Middle Class Package
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Savings, contentDescription = null, tint = EmeraldGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Middle-Class Smart Saver",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EmeraldGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "SUPER SAVER",
                                        color = EmeraldLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Train 3A + Boutique Stay + Scooters + Local Thalis",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "From ₹11,999 / couple (5 Days)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Luxury Package
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AmberGold.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = AmberGold)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "VIP Luxury & Royal Palace",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AmberGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "5-STAR RETREAT",
                                        color = AmberLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Direct Flight + Taj Beachfront Resort + Chauffeur Innova",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "From ₹44,999 / couple (5 Days)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationCard(
    dest: DestinationInfo,
    onSelect: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(dest.imageDrawableName) {
        context.resources.getIdentifier(dest.imageDrawableName, "drawable", context.packageName)
    }

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onSelect)
            .testTag("dest_card_${dest.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = dest.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Navy700)
                    )
                }

                // Gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, NavyCard.copy(alpha = 0.95f)),
                                startY = 60f
                            )
                        )
                )

                // Season tag
                Surface(
                    color = Navy900.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = AmberGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(dest.avgTemp, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dest.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${dest.state}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = dest.tagLine,
                    fontSize = 12.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dest.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Season: ${dest.bestSeason.split("(")[0].trim()}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Surface(
                        color = ElectricBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                    ) {
                        Text(
                            text = "Explore Plan ➔",
                            color = ElectricBlueLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
