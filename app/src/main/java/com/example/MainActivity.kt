package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.model.TripRequest
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class AppScreen(val label: String, val icon: ImageVector) {
    DISCOVERY("Explore", Icons.Default.TravelExplore),
    PLANNER("Planner", Icons.Default.AutoAwesome),
    AGENTS("Agents", Icons.Default.Hub),
    TRANSPORT("Transport", Icons.Default.DirectionsTransit),
    BUDGET("Budget", Icons.Default.AccountBalanceWallet),
    ITINERARY("Itinerary", Icons.Default.CalendarMonth),
    REPLAN("Replan", Icons.Default.Tune),
    BOOKINGS("Bookings", Icons.Default.ConfirmationNumber),
    SUPPORT("Support", Icons.Default.HeadsetMic),
    PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val orchestrator = TripOrchestrator()

        setContent {
            TripPilotTheme {
                TripPilotApp(orchestrator = orchestrator)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPilotApp(orchestrator: TripOrchestrator) {
    var currentScreen by remember { mutableStateOf(AppScreen.DISCOVERY) }
    var navigationStack by remember { mutableStateOf(listOf(AppScreen.DISCOVERY)) }
    var currentLanguage by remember { mutableStateOf("en") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val bookings by orchestrator.bookings.collectAsState()
    val disruptions by orchestrator.disruptions.collectAsState()

    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            navigationStack = navigationStack + screen
            currentScreen = screen
        }
    }

    fun navigateBack() {
        if (navigationStack.size > 1) {
            val updated = navigationStack.dropLast(1)
            navigationStack = updated
            currentScreen = updated.last()
        } else {
            currentScreen = AppScreen.DISCOVERY
        }
    }

    // Intercept Back Press for sub-screens
    if (currentScreen != AppScreen.DISCOVERY) {
        BackHandler {
            navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Navy900,
        contentColor = TextPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { navigateTo(AppScreen.DISCOVERY) }
                    ) {
                        Surface(
                            color = RoyalViolet,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TRIPPILOT AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "India Multi-Agent Travel",
                                fontSize = 10.sp,
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    // Bookings quick icon with counter
                    IconButton(
                        onClick = { navigateTo(AppScreen.BOOKINGS) },
                        modifier = Modifier.testTag("top_bookings_icon")
                    ) {
                        BadgedBox(
                            badge = {
                                if (bookings.isNotEmpty()) {
                                    Badge(containerColor = EmeraldGreen, contentColor = TextPrimary) {
                                        Text("${bookings.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = "Bookings", tint = if (currentScreen == AppScreen.BOOKINGS) CyanAccent else TextSecondary)
                        }
                    }

                    // Support quick icon with notice badge
                    IconButton(
                        onClick = { navigateTo(AppScreen.SUPPORT) },
                        modifier = Modifier.testTag("top_support_icon")
                    ) {
                        BadgedBox(
                            badge = {
                                if (disruptions.isNotEmpty()) {
                                    Badge(containerColor = AmberGold, contentColor = Navy900) {
                                        Text("!")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.HeadsetMic, contentDescription = "Support", tint = if (currentScreen == AppScreen.SUPPORT) CyanAccent else TextSecondary)
                        }
                    }

                    // Profile / Language button
                    IconButton(
                        onClick = { navigateTo(AppScreen.PROFILE) },
                        modifier = Modifier.testTag("top_profile_icon")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = if (currentScreen == AppScreen.PROFILE) CyanAccent else TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy850,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = TextSecondary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Navy850,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val primaryNavItems = listOf(
                    AppScreen.DISCOVERY,
                    AppScreen.PLANNER,
                    AppScreen.AGENTS,
                    AppScreen.TRANSPORT,
                    AppScreen.BUDGET
                )

                primaryNavItems.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { navigateTo(screen) },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimary,
                            selectedTextColor = CyanAccent,
                            indicatorColor = ElectricBlue,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DISCOVERY -> {
                    LandingAndDiscoveryScreen(
                        orchestrator = orchestrator,
                        scope = scope,
                        onNavigateToPlanner = { navigateTo(AppScreen.PLANNER) },
                        onNavigateToAgents = { navigateTo(AppScreen.AGENTS) },
                        onSelectDestination = { dest ->
                            orchestrator.startOrchestration(
                                TripRequest(
                                    origin = "Hyderabad",
                                    destination = dest.name,
                                    days = 5,
                                    travellers = 2,
                                    budgetInr = 25000,
                                    preferences = dest.vibeTags
                                ),
                                scope
                            ) {
                                navigateTo(AppScreen.AGENTS)
                            }
                            navigateTo(AppScreen.AGENTS)
                        }
                    )
                }

                AppScreen.PLANNER -> {
                    SmartPlannerScreen(
                        orchestrator = orchestrator,
                        scope = scope,
                        onOrchestrationStarted = {
                            navigateTo(AppScreen.AGENTS)
                        }
                    )
                }

                AppScreen.AGENTS -> {
                    AgentOrchestrationScreen(
                        orchestrator = orchestrator,
                        onNavigateToTransport = { navigateTo(AppScreen.TRANSPORT) },
                        onNavigateToBudget = { navigateTo(AppScreen.BUDGET) },
                        onNavigateToItinerary = { navigateTo(AppScreen.ITINERARY) }
                    )
                }

                AppScreen.TRANSPORT -> {
                    TransportComparisonScreen(
                        orchestrator = orchestrator,
                        onBookTransport = { type, title, route, amount, seat ->
                            val record = orchestrator.createBooking(type, title, route, amount, "2 Travellers", seat)
                            scope.launch {
                                snackbarHostState.showSnackbar("Booking Confirmed! PNR: ${record.pnr}")
                            }
                            navigateTo(AppScreen.BOOKINGS)
                        }
                    )
                }

                AppScreen.BUDGET -> {
                    BudgetOptimizerScreen(
                        orchestrator = orchestrator,
                        onNavigateToReplan = { navigateTo(AppScreen.REPLAN) }
                    )
                }

                AppScreen.ITINERARY -> {
                    ItineraryScreen(orchestrator = orchestrator)
                }

                AppScreen.REPLAN -> {
                    ReplanTripScreen(
                        orchestrator = orchestrator,
                        onBack = { navigateBack() }
                    )
                }

                AppScreen.BOOKINGS -> {
                    BookingAndPassScreen(orchestrator = orchestrator)
                }

                AppScreen.SUPPORT -> {
                    SupportScreen(orchestrator = orchestrator)
                }

                AppScreen.PROFILE -> {
                    ProfileAndAuthScreen(
                        currentLanguage = currentLanguage,
                        onLanguageChange = { newLang ->
                            currentLanguage = newLang
                            scope.launch {
                                snackbarHostState.showSnackbar("Language switched to $newLang")
                            }
                        }
                    )
                }
            }
        }
    }
}
