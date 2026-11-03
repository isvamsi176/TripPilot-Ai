package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.TripOrchestrator
import com.example.model.BookingRecord
import com.example.ui.components.SectionHeader
import com.example.ui.components.TripFormatter
import com.example.ui.theme.*

@Composable
fun BookingAndPassScreen(
    orchestrator: TripOrchestrator,
    modifier: Modifier = Modifier
) {
    val bookings by orchestrator.bookings.collectAsState()
    var selectedBookingForPass by remember { mutableStateOf<BookingRecord?>(bookings.firstOrNull()) }
    var bookingToCancel by remember { mutableStateOf<BookingRecord?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .padding(horizontal = 20.dp)
            .testTag("bookings_screen"),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "Booking & Travel Management",
                subtitle = "Manage verified bookings, view e-Tickets & IRCTC boarding passes",
                badgeText = "Simulated Service"
            )
        }

        // Transparency Disclaimer
        item {
            Surface(
                color = ElectricBlue.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TripPilot Simulated Reservation Gateway: Test bookings generate realistic PNRs and scannable tickets with user-approved cancellation safeguards.",
                        fontSize = 11.sp,
                        color = CyanAccent,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Active Digital Pass / e-Ticket View (if selected)
        if (selectedBookingForPass != null) {
            item {
                Text(
                    text = "Digital e-Ticket & Boarding Pass",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                DigitalPassCard(booking = selectedBookingForPass!!)
            }
        }

        // Booking History List
        item {
            Text(
                text = "Active & Historical Bookings (${bookings.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(bookings) { booking ->
            BookingItemCard(
                booking = booking,
                isSelected = selectedBookingForPass?.id == booking.id,
                onSelectPass = { selectedBookingForPass = booking },
                onRequestCancel = { bookingToCancel = booking }
            )
        }
    }

    // Cancellation Approval Dialog (Mandatory user confirmation)
    if (bookingToCancel != null) {
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = {
                Text("Confirm Cancellation", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to cancel '${bookingToCancel?.title}' (PNR: ${bookingToCancel?.pnr})?",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Refund Amount: ${TripFormatter.formatInr(bookingToCancel?.totalAmountInr ?: 0)} will be credited back to your original payment method.",
                        color = EmeraldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        bookingToCancel?.let { orchestrator.cancelBooking(it.id) }
                        bookingToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Yes, Cancel Booking", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { bookingToCancel = null }) {
                    Text("Keep Booking", color = TextPrimary)
                }
            },
            containerColor = NavyCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DigitalPassCard(booking: BookingRecord) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricBlue.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ElectricBlue,
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("TP", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRIPPILOT BOARDING PASS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = if (booking.status == "CONFIRMED") EmeraldGreen.copy(alpha = 0.2f) else CrimsonAlert.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = booking.status,
                        color = if (booking.status == "CONFIRMED") EmeraldLight else CrimsonAlert,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = booking.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = booking.routeOrLocation,
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Two-column details
            Surface(
                color = Navy850,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PASSENGER / GUEST", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(booking.travellersText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("SEAT / ROOM", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(booking.seatOrRoomNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("TRAVEL DATE", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(booking.travelDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("PNR / BOOKING REF", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(booking.pnr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Perforated line effect
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                val step = 16f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0xFF23365F),
                        start = Offset(x, 0f),
                        end = Offset(x + 8f, 0f),
                        strokeWidth = 2f
                    )
                    x += step
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // QR & Barcode Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Procedural QR Code Canvas
                ProceduralQrCode(modifier = Modifier.size(72.dp))

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Paid: ${TripFormatter.formatInr(booking.totalAmountInr)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldLight
                    )
                    Text(
                        text = "Scan at terminal e-Gate / hotel desk",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hash: ${booking.qrPayload.hashCode()}",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ProceduralQrCode(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        val gridSize = 12
        val cellSize = size.width / gridSize
        val darkColor = Color(0xFF070D1E)

        // Draw corner alignment squares
        drawRect(color = darkColor, topLeft = Offset(0f, 0f), size = Size(cellSize * 3, cellSize * 3))
        drawRect(color = Color.White, topLeft = Offset(cellSize, cellSize), size = Size(cellSize, cellSize))

        drawRect(color = darkColor, topLeft = Offset(cellSize * 9, 0f), size = Size(cellSize * 3, cellSize * 3))
        drawRect(color = Color.White, topLeft = Offset(cellSize * 10, cellSize), size = Size(cellSize, cellSize))

        drawRect(color = darkColor, topLeft = Offset(0f, cellSize * 9), size = Size(cellSize * 3, cellSize * 3))
        drawRect(color = Color.White, topLeft = Offset(cellSize, cellSize * 10), size = Size(cellSize, cellSize))

        // Draw data dots
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                // pseudo-random deterministic pattern
                if ((i * 7 + j * 13 + i * j) % 3 == 0) {
                    val inTopLeft = i < 3 && j < 3
                    val inTopRight = i >= 9 && j < 3
                    val inBottomLeft = i < 3 && j >= 9
                    if (!inTopLeft && !inTopRight && !inBottomLeft) {
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(i * cellSize, j * cellSize),
                            size = Size(cellSize * 0.9f, cellSize * 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: BookingRecord,
    isSelected: Boolean,
    onSelectPass: () -> Unit,
    onRequestCancel: () -> Unit
) {
    val (typeColor, typeIcon) = when (booking.type) {
        "Flight" -> Pair(ElectricBlue, Icons.Default.Flight)
        "Train" -> Pair(EmeraldGreen, Icons.Default.Train)
        "Bus" -> Pair(RoyalViolet, Icons.Default.DirectionsBus)
        else -> Pair(AmberGold, Icons.Default.Hotel)
    }

    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyanAccent else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelectPass)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = typeColor.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "PNR: ${booking.pnr} • ${booking.travelDate}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = TripFormatter.formatInr(booking.totalAmountInr),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (booking.status == "CONFIRMED") EmeraldGreen.copy(alpha = 0.2f) else CrimsonAlert.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = booking.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (booking.status == "CONFIRMED") EmeraldLight else CrimsonAlert,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (booking.status == "CONFIRMED") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cancel",
                        fontSize = 11.sp,
                        color = CrimsonAlert,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onRequestCancel)
                    )
                }
            }
        }
    }
}
