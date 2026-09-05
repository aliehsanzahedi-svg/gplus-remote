package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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
import com.example.ui.theme.CoolCyan500
import com.example.ui.theme.CoolCyanGlow
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.PowerRed
import com.example.ui.theme.PowerRedGlow
import com.example.ui.theme.SlateDark600
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SunAmber
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.SurfaceVariantSlate

@Composable
fun RemoteControls(
    acState: AcState,
    onPowerToggle: () -> Unit,
    onTempIncrease: () -> Unit,
    onTempDecrease: () -> Unit,
    onModeSelect: (AcMode) -> Unit,
    onFanSpeedSelect: (AcFanSpeed) -> Unit,
    onSwingToggle: () -> Unit,
    onTurboToggle: () -> Unit,
    onLightToggle: () -> Unit,
    onSleepToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPoweredOn = acState.power == AcPower.ON

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("remote_controls_card")
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color(0x3300F2FE)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        border = BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                listOf(
                    Color(0xFF1E3A5F),
                    Color(0xFF0D1B32),
                    Color(0xFF081224)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ROW 1: Large Highlighted Power Button & Quick Action Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Action: Light toggle
                RemoteRoundButton(
                    icon = Icons.Default.Lightbulb,
                    label = stringResource(R.string.light),
                    isActive = isPoweredOn && acState.light,
                    activeColor = CoolCyan400,
                    onClick = onLightToggle,
                    testTag = "button_light_toggle",
                    size = 54.dp
                )

                // MAIN HIGHLIGHTED POWER BUTTON
                PowerButton(
                    isPoweredOn = isPoweredOn,
                    onClick = onPowerToggle,
                    modifier = Modifier.testTag("button_power_toggle")
                )

                // Secondary Action: Sleep toggle
                RemoteRoundButton(
                    icon = Icons.Default.Bedtime,
                    label = stringResource(R.string.sleep),
                    isActive = isPoweredOn && acState.sleep,
                    activeColor = MintEmerald,
                    onClick = onSleepToggle,
                    testTag = "button_sleep_toggle",
                    size = 54.dp
                )
            }

            // ROW 2: Big '+' and '-' Temperature Adjustment Controls
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("temperature_controls_container")
                    .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color(0x2200F2FE)),
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, Color(0xFF162945))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF060E1C),
                                    Color(0xFF0B172B),
                                    Color(0xFF060E1C)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big Minus (-) Button
                        TempAdjustButton(
                            icon = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.temp_down),
                            isEnabled = isPoweredOn && acState.temperature > AcState.MIN_TEMP,
                            onClick = onTempDecrease,
                            testTag = "button_temp_minus"
                        )

                        // Centered Current Temp Label
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.testTag("temp_indicator_box")
                        ) {
                            Text(
                                text = stringResource(R.string.temperature).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${acState.temperature}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp,
                                        color = if (isPoweredOn) CoolCyanGlow else Color(0xFF475569)
                                    )
                                )
                                Text(
                                    text = "°C",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPoweredOn) CoolCyan300 else Color(0xFF475569)
                                    ),
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        }

                        // Big Plus (+) Button
                        TempAdjustButton(
                            icon = Icons.Default.Add,
                            contentDescription = stringResource(R.string.temp_up),
                            isEnabled = isPoweredOn && acState.temperature < AcState.MAX_TEMP,
                            onClick = onTempIncrease,
                            testTag = "button_temp_plus"
                        )
                    }
                }
            }

            // ROW 3: Mode Selector Grid / Tabs (Cool, Heat, Auto, Fan, Dry)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.mode).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF060E1C))
                        .border(1.dp, Color(0xFF162945), RoundedCornerShape(16.dp))
                        .padding(4.dp)
                        .testTag("mode_selector_row"),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val modes = listOf(
                        AcMode.COOL to Icons.Default.Air,
                        AcMode.HEAT to Icons.Default.WbSunny,
                        AcMode.AUTO to Icons.Default.Autorenew,
                        AcMode.FAN to Icons.Default.Air,
                        AcMode.DRY to Icons.Default.WaterDrop
                    )

                    for ((mode, icon) in modes) {
                        val isSelected = acState.mode == mode
                        ModeTabItem(
                            mode = mode,
                            icon = icon,
                            isSelected = isSelected,
                            isPoweredOn = isPoweredOn,
                            onClick = { onModeSelect(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ROW 4: Fan Speed Switcher (Auto, Low, Med, High)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.fan_speed).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF060E1C))
                        .border(1.dp, Color(0xFF162945), RoundedCornerShape(16.dp))
                        .padding(4.dp)
                        .testTag("fan_speed_selector_row"),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val speeds = listOf(
                        AcFanSpeed.AUTO,
                        AcFanSpeed.LOW,
                        AcFanSpeed.MED,
                        AcFanSpeed.HIGH
                    )

                    for (speed in speeds) {
                        val isSelected = acState.fanSpeed == speed
                        FanSpeedTabItem(
                            speed = speed,
                            isSelected = isSelected,
                            isPoweredOn = isPoweredOn,
                            onClick = { onFanSpeedSelect(speed) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ROW 5: Special Function Buttons (Swing & Turbo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Swing Toggle Button
                FeatureToggleButton(
                    icon = Icons.Default.Sync,
                    label = stringResource(R.string.swing),
                    stateLabel = if (acState.swing) stringResource(R.string.swing_on) else stringResource(R.string.swing_off),
                    isActive = isPoweredOn && acState.swing,
                    activeColor = CoolCyan400,
                    onClick = onSwingToggle,
                    testTag = "button_swing_toggle",
                    modifier = Modifier.weight(1f)
                )

                // Turbo Toggle Button
                FeatureToggleButton(
                    icon = Icons.Default.Bolt,
                    label = stringResource(R.string.turbo),
                    stateLabel = if (acState.turbo) stringResource(R.string.turbo_on) else stringResource(R.string.turbo_off),
                    isActive = isPoweredOn && acState.turbo,
                    activeColor = SunAmber,
                    onClick = onTurboToggle,
                    testTag = "button_turbo_toggle",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PowerButton(
    isPoweredOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor by animateColorAsState(
        targetValue = if (isPoweredOn) MintEmerald else PowerRed,
        animationSpec = tween(300),
        label = "powerColor"
    )

    val glowColor by animateColorAsState(
        targetValue = if (isPoweredOn) MintEmerald.copy(alpha = 0.5f) else PowerRedGlow.copy(alpha = 0.45f),
        animationSpec = tween(300),
        label = "powerGlow"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .shadow(16.dp, CircleShape, spotColor = glowColor)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF14243B),
                        Color(0xFF060D19)
                    )
                )
            )
            .border(2.5.dp, buttonColor, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = buttonColor),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = stringResource(R.string.power),
                tint = buttonColor,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isPoweredOn) stringResource(R.string.power_on) else stringResource(R.string.power_off),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = buttonColor
                )
            )
        }
    }
}

@Composable
private fun TempAdjustButton(
    icon: ImageVector,
    contentDescription: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(58.dp)
            .testTag(testTag)
            .shadow(if (isEnabled) 8.dp else 0.dp, CircleShape, spotColor = CoolCyanGlow.copy(alpha = 0.35f))
            .clip(CircleShape)
            .clickable(
                enabled = isEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = CoolCyanGlow),
                onClick = onClick
            ),
        shape = CircleShape,
        color = if (isEnabled) Color(0xFF0E1C30) else Color(0xFF070F1C),
        border = BorderStroke(1.5.dp, if (isEnabled) CoolCyan400.copy(alpha = 0.8f) else Color(0xFF14243D))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isEnabled) CoolCyan300 else Color(0xFF334155),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ModeTabItem(
    mode: AcMode,
    icon: ImageVector,
    isSelected: Boolean,
    isPoweredOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = mode.color
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected && isPoweredOn) activeColor.copy(alpha = 0.22f) else Color.Transparent,
        animationSpec = tween(200),
        label = "modeBg"
    )

    Surface(
        modifier = modifier
            .height(58.dp)
            .testTag("mode_tab_${mode.name.lowercase()}")
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = activeColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = if (isSelected && isPoweredOn) BorderStroke(1.2.dp, activeColor) else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(mode.stringResId),
                tint = if (isSelected && isPoweredOn) activeColor else Color(0xFF64748B),
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = stringResource(mode.stringResId),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected && isPoweredOn) FontWeight.Black else FontWeight.Normal,
                    letterSpacing = 0.5.sp,
                    color = if (isSelected && isPoweredOn) activeColor else Color(0xFF94A3B8)
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FanSpeedTabItem(
    speed: AcFanSpeed,
    isSelected: Boolean,
    isPoweredOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = CoolCyan400
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected && isPoweredOn) activeColor.copy(alpha = 0.22f) else Color.Transparent,
        animationSpec = tween(200),
        label = "fanBg"
    )

    Surface(
        modifier = modifier
            .height(44.dp)
            .testTag("fan_speed_tab_${speed.name.lowercase()}")
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = activeColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = if (isSelected && isPoweredOn) BorderStroke(1.2.dp, activeColor) else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = stringResource(speed.stringResId),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected && isPoweredOn) FontWeight.Black else FontWeight.Normal,
                    letterSpacing = 0.5.sp,
                    color = if (isSelected && isPoweredOn) activeColor else Color(0xFF94A3B8)
                )
            )
        }
    }
}

@Composable
private fun FeatureToggleButton(
    icon: ImageVector,
    label: String,
    stateLabel: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .testTag(testTag)
            .shadow(
                elevation = if (isActive) 8.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = activeColor.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = activeColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) activeColor.copy(alpha = 0.16f) else Color(0xFF060E1C),
        border = BorderStroke(1.2.dp, if (isActive) activeColor else Color(0xFF162945))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isActive) activeColor else Color(0xFFE2E8F0)
                    )
                )
                Text(
                    text = stateLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) activeColor.copy(alpha = 0.85f) else Color(0xFF64748B)
                    )
                )
            }
        }
    }
}

@Composable
private fun RemoteRoundButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(size)
            .testTag(testTag)
            .shadow(
                elevation = if (isActive) 8.dp else 0.dp,
                shape = CircleShape,
                spotColor = activeColor.copy(alpha = 0.4f)
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = activeColor),
                onClick = onClick
            ),
        shape = CircleShape,
        color = if (isActive) activeColor.copy(alpha = 0.2f) else Color(0xFF060E1C),
        border = BorderStroke(1.2.dp, if (isActive) activeColor else Color(0xFF162945))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else Color(0xFF64748B),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
