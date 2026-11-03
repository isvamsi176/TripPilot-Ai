package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgentStatus
import com.example.model.AgentType
import com.example.model.TravelTier
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

object TripFormatter {
    fun formatInr(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("INR", "₹").trim()
    }
}

@Composable
fun AgentStatusBadge(
    status: AgentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        AgentStatus.IDLE -> Triple(Navy700, TextMuted, "IDLE")
        AgentStatus.ANALYZING -> Triple(ElectricBlue.copy(alpha = 0.2f), ElectricBlueLight, "ANALYZING")
        AgentStatus.SEARCHING -> Triple(RoyalViolet.copy(alpha = 0.2f), VioletSoft, "PARALLEL SEARCH")
        AgentStatus.OPTIMIZING -> Triple(AmberGold.copy(alpha = 0.2f), AmberLight, "OPTIMIZING")
        AgentStatus.COMPLETED -> Triple(EmeraldGreen.copy(alpha = 0.2f), EmeraldLight, "READY")
        AgentStatus.ALERT -> Triple(CrimsonAlert.copy(alpha = 0.2f), CrimsonAlert, "ALERT")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .border(
                1.dp,
                if (status == AgentStatus.SEARCHING || status == AgentStatus.OPTIMIZING)
                    textColor.copy(alpha = alphaAnim)
                else textColor.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun TierChip(
    tier: TravelTier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val (accentColor, icon) = when (tier) {
        TravelTier.ECONOMY -> Pair(EmeraldGreen, Icons.Default.Savings)
        TravelTier.BALANCED -> Pair(ElectricBlue, Icons.Default.Balance)
        TravelTier.PREMIUM_LUXURY -> Pair(AmberGold, Icons.Default.Diamond)
    }

    Surface(
        color = if (isSelected) accentColor.copy(alpha = 0.22f) else NavyCard,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) accentColor else BorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = tier.title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextSecondary
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (badgeText != null) {
                Surface(
                    color = ElectricBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}
