package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.AuthManager
import com.example.data.firebase.TripPilotRepository
import com.example.data.firebase.UserEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ProfileAndAuthScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val repository = remember { TripPilotRepository(context) }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var userProfile by remember { mutableStateOf<UserEntity?>(null) }
    var isSigningIn by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    var linkedIrctcInput by remember { mutableStateOf("vamsi_irctc26") }
    var frequentFlyerInput by remember { mutableStateOf("6E-99382104") }
    var isSavingProfile by remember { mutableStateOf(false) }

    val languages = listOf(
        Pair("en", "English"),
        Pair("hi", "हिन्दी (Hindi)"),
        Pair("te", "తెలుగు (Telugu)")
    )

    // Listen to Auth State
    DisposableEffect(Unit) {
        val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    // Observe Firestore Profile when user is authenticated
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            repository.observeUserProfile().collect { profile ->
                userProfile = profile
                if (profile != null) {
                    if (profile.linkedIrctc.isNotEmpty()) linkedIrctcInput = profile.linkedIrctc
                    if (profile.frequentFlyer.isNotEmpty()) frequentFlyerInput = profile.frequentFlyer
                }
            }
        } else {
            userProfile = null
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = if (currentUser != null) "User Profile & Cloud Sync" else "Sign In to TripPilot AI",
                subtitle = "Connected with Firebase Cloud Firestore & Google Sign-In",
                badgeText = if (currentUser != null) "AUTHENTICATED" else "SIGN IN REQUIRED"
            )
        }

        if (currentUser != null) {
            val user = currentUser!!
            val displayName = user.displayName ?: userProfile?.displayName ?: "Traveler"
            val email = user.email ?: userProfile?.email ?: ""
            val miles = userProfile?.miles ?: 14250L

            // Profile Card
            item {
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = RoyalViolet,
                                shape = CircleShape,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = displayName.take(2).uppercase(),
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = displayName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = AmberGold.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "GOOGLE SIGN-IN",
                                            color = AmberGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = email,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$miles TripPilot Miles (₹${miles / 2} value)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Frequent Flyer & IRCTC Info inputs with Cloud Save
                        Surface(
                            color = Navy850,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Travel Identity & Credentials (Firestore Synced)", fontSize = 11.sp, color = CyanAccent, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = linkedIrctcInput,
                                    onValueChange = { linkedIrctcInput = it },
                                    label = { Text("IRCTC Username", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanAccent,
                                        unfocusedBorderColor = BorderSubtle,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = frequentFlyerInput,
                                    onValueChange = { frequentFlyerInput = it },
                                    label = { Text("Frequent Flyer / Airline Number", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanAccent,
                                        unfocusedBorderColor = BorderSubtle,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        isSavingProfile = true
                                        scope.launch {
                                            try {
                                                repository.saveUserProfile(
                                                    displayName = displayName,
                                                    email = email,
                                                    linkedIrctc = linkedIrctcInput,
                                                    frequentFlyer = frequentFlyerInput,
                                                    preferredTier = "BALANCED"
                                                )
                                                isSavingProfile = false
                                            } catch (e: Exception) {
                                                isSavingProfile = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isSavingProfile) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TextPrimary)
                                    } else {
                                        Text("Save to Cloud Firestore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {
                                AuthManager.signOut(context, credentialManager, onSignOutComplete = {
                                    currentUser = null
                                }, scope = scope)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out of Google Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Google Sign-In Card
            item {
                Surface(
                    color = NavyCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(28.dp))
                            }
                        }

                        Text(
                            text = "Authenticate with Google",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Sign in securely with your Google Account to sync trip plans, track confirmed bookings, and enable multi-agent cloud optimization.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 17.sp
                        )

                        if (authErrorMessage != null) {
                            Surface(
                                color = CrimsonAlert.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = authErrorMessage!!,
                                    color = CrimsonAlert,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                isSigningIn = true
                                authErrorMessage = null
                                AuthManager.signInWithGoogle(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isSigningIn = false
                                        currentUser = Firebase.auth.currentUser
                                    },
                                    onAuthError = { error ->
                                        isSigningIn = false
                                        authErrorMessage = error
                                    },
                                    onAuthCancelled = {
                                        isSigningIn = false
                                    },
                                    scope = scope
                                )
                            },
                            enabled = !isSigningIn,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("sign_in_google_button")
                        ) {
                            if (isSigningIn) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextPrimary)
                            } else {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Sign in with Google", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // Language & Localization Preferences
        item {
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Regional & Multilingual Localization",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Default Currency Pill (Fixed to INR)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Navy850, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Base Currency", fontSize = 11.sp, color = TextSecondary)
                            Text("Indian Rupee (₹ / INR)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                        }
                        Surface(
                            color = AmberGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    // Multilingual Switcher
                    Column {
                        Text("App Interface Language", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            languages.forEach { (code, label) ->
                                val isSelected = currentLanguage == code
                                Surface(
                                    color = if (isSelected) RoyalViolet.copy(alpha = 0.25f) else Navy850,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) RoyalViolet else BorderSubtle
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onLanguageChange(code) }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) VioletSoft else TextSecondary,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
