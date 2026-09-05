package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ModeFanOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AcFanSpeed
import com.example.model.AcMode
import com.example.model.AcPower
import com.example.model.AcState
import com.example.ui.theme.CoolCyan300
import com.example.ui.theme.CoolCyan400
import com.example.ui.theme.CoolCyanGlow
import com.example.ui.theme.LcdBackground
import com.example.ui.theme.LcdBezel
import com.example.ui.theme.LcdDimGhost
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.PowerRed
import com.example.ui.theme.PowerRedGlow
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SunAmber

@Composable
fun LcdDisplayPanel(
    acState: AcState,
    isTransmitting: Boolean,
    modifier: Modifier = Modifier
) {
    val isPoweredOn = acState.power == AcPower.ON

    // Glowing cyan/amber color depending on mode
    val activeGlowColor by animateColorAsState(
        targetValue = when {
            !isPoweredOn -> Color(0xFF64748B)
            acState.mode == AcMode.HEAT -> SunAmber
            acState.mode == AcMode.AUTO -> MintEmerald
            else -> CoolCyan400
        },
        animationSpec = tween(300),
        label = "glowColor"
    )

    // Pulsing transmission animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val transmissionPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "irPulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lcd_display_panel")
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = activeGlowColor.copy(alpha = if (isPoweredOn) 0.42f else 0.06f)
            ),
        shape = RoundedCornerShape(26.dp),
        color = LcdBezel,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.verticalGradient(
                listOf(
                    Color(0xFF1E3A5F),
                    Color(0xFF0C192E),
                    SlateDark800
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .padding(7.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF020712),
                            Color(0xFF030E1C),
                            Color(0xFF020712)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            activeGlowColor.copy(alpha = if (isPoweredOn) 0.35f else 0.08f),
                            Color(0x0D00F2FE),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // TOP HEADER: Brand & IR Transmission LED
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GPLUS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.5.sp,
                                color = if (isPoweredOn) activeGlowColor else Color(0xFF64748B)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0B1B30))
                                .border(0.5.dp, Color(0xFF1E3A5F), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.inverter_ac),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    // IR Transmission Blinking LED indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF071322))
                            .border(0.5.dp, if (isTransmitting) CoolCyanGlow else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("ir_transmission_indicator")
                    ) {
                        Text(
                            text = if (isTransmitting) stringResource(R.string.ir_tx_badge) else stringResource(R.string.ir_ready_badge),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isTransmitting) CoolCyanGlow else Color(0xFF64748B)
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isTransmitting) {
                                        CoolCyanGlow.copy(alpha = transmissionPulse)
                                    } else {
                                        Color(0xFF1E293B)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isTransmitting) CoolCyanGlow else Color(0xFF334155),
                                    CircleShape
                                )
                                .shadow(if (isTransmitting) 6.dp else 0.dp, CircleShape, spotColor = CoolCyanGlow)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // MIDDLE: Temperature & Status Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Active Mode badge and Fan Speed
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Mode Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isPoweredOn) activeGlowColor.copy(alpha = 0.16f) else LcdDimGhost)
                                .border(
                                    width = 1.dp,
                                    color = if (isPoweredOn) activeGlowColor.copy(alpha = 0.4f) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("active_mode_badge")
                        ) {
                            val modeIcon = when (acState.mode) {
                                AcMode.COOL -> Icons.Default.Air
                                AcMode.HEAT -> Icons.Default.WbSunny
                                AcMode.AUTO -> Icons.Default.Autorenew
                                AcMode.FAN -> Icons.Default.Air
                                AcMode.DRY -> Icons.Default.WaterDrop
                            }
                            Icon(
                                imageVector = modeIcon,
                                contentDescription = stringResource(acState.mode.stringResId),
                                tint = if (isPoweredOn) activeGlowColor else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPoweredOn) stringResource(acState.mode.stringResId) else stringResource(R.string.power_off),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isPoweredOn) activeGlowColor else Color(0xFF475569)
                                )
                            )
                        }

                        // Fan Speed Indicator
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.fan_speed),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF64748B)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPoweredOn) stringResource(acState.fanSpeed.stringResId) else "-",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPoweredOn) CoolCyan300 else Color(0xFF475569)
                                    )
                                )
                            }
                            // Fan bars (Auto shows all 3 pulsing or labeled, Low=1, Med=2, High=3)
                            FanSpeedBars(
                                speed = acState.fanSpeed,
                                isPoweredOn = isPoweredOn,
                                activeColor = activeGlowColor
                            )
                        }
                    }

                    // Center/Right: Prominent Large Target Temperature Display
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.testTag("target_temperature_display")
                    ) {
                        if (isPoweredOn) {
                            Text(
                                text = "${acState.temperature}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 62.sp,
                                    lineHeight = 62.sp,
                                    color = activeGlowColor
                                )
                            )
                            Text(
                                text = "°C",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = activeGlowColor.copy(alpha = 0.9f)
                                ),
                                modifier = Modifier.padding(top = 6.dp, start = 2.dp)
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "--",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 54.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                )
                                Text(
                                    text = stringResource(R.string.standby),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        color = Color(0xFF475569)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BOTTOM BADGES ROW: Swing, Turbo, Light, Sleep
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF040B16))
                        .border(0.5.dp, Color(0xFF0E1F35), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LcdIndicatorBadge(
                        label = stringResource(R.string.swing),
                        isActive = isPoweredOn && acState.swing,
                        icon = Icons.Default.Sync,
                        activeColor = activeGlowColor,
                        testTag = "indicator_swing"
                    )

                    LcdIndicatorBadge(
                        label = stringResource(R.string.turbo),
                        isActive = isPoweredOn && acState.turbo,
                        icon = Icons.Default.Bolt,
                        activeColor = SunAmber,
                        testTag = "indicator_turbo"
                    )

                    LcdIndicatorBadge(
                        label = stringResource(R.string.light),
                        isActive = isPoweredOn && acState.light,
                        icon = Icons.Default.Lightbulb,
                        activeColor = CoolCyan400,
                        testTag = "indicator_light"
                    )

                    LcdIndicatorBadge(
                        label = stringResource(R.string.sleep),
                        isActive = isPoweredOn && acState.sleep,
                        icon = Icons.Default.Bedtime,
                        activeColor = MintEmerald,
                        testTag = "indicator_sleep"
                    )
                }
            }
        }
    }
}

@Composable
private fun FanSpeedBars(
    speed: AcFanSpeed,
    isPoweredOn: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("fan_speed_bars"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val activeBars = when {
            !isPoweredOn -> 0
            speed == AcFanSpeed.AUTO -> 4
            speed == AcFanSpeed.LOW -> 1
            speed == AcFanSpeed.MED -> 2
            speed == AcFanSpeed.HIGH -> 3
            else -> 0
        }

        val heights = listOf(8.dp, 12.dp, 16.dp, 20.dp)

        for (i in 0 until 4) {
            val isBarActive = i < activeBars
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(heights[i])
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isBarActive) {
                            if (speed == AcFanSpeed.AUTO) MintEmerald else activeColor
                        } else {
                            Color(0xFF1E293B)
                        }
                    )
            )
        }
    }
}

@Composable
private fun LcdIndicatorBadge(
    label: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) activeColor else Color(0xFF334155),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.5.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) activeColor else Color(0xFF475569)
            )
        )
    }
}
