package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ir.IrFrameGenerator
import com.example.model.AcState
import com.example.model.IrProtocol
import com.example.model.LastTransmissionInfo
import com.example.ui.theme.CoolCyan300
import com.example.ui.theme.CoolCyan400
import com.example.ui.theme.CoolCyan500
import com.example.ui.theme.CoolCyanGlow
import com.example.ui.theme.LcdBackground
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SunAmber
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.SurfaceVariantSlate

@Composable
fun NoIrBlasterDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .testTag("dialog_no_ir_blaster")
            .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = SunAmber.copy(alpha = 0.35f)),
        icon = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(SunAmber.copy(alpha = 0.16f))
                    .border(1.dp, SunAmber.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SensorsOff,
                    contentDescription = null,
                    tint = SunAmber,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.ir_blaster_required_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9)
                )
            )
        },
        text = {
            Text(
                text = stringResource(R.string.ir_blaster_required_msg),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFCBD5E1),
                    lineHeight = 22.sp
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CoolCyan500),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = CoolCyanGlow.copy(alpha = 0.4f))
                    .testTag("dialog_button_continue_demo")
            ) {
                Text(
                    text = stringResource(R.string.continue_demo),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF030D14)
                    )
                )
            }
        },
        containerColor = SurfaceSlate,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun TransmissionStatusBar(
    hasHardwareIr: Boolean,
    lastTransmission: LastTransmissionInfo?,
    onOpenInspector: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = Color(0x2200F2FE))
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenInspector)
            .testTag("transmission_status_bar"),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF040C18),
        border = BorderStroke(1.dp, Color(0xFF102540))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(if (hasHardwareIr) MintEmerald else SunAmber)
                        .shadow(6.dp, CircleShape, spotColor = if (hasHardwareIr) MintEmerald else SunAmber)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (hasHardwareIr) {
                            stringResource(R.string.ir_supported)
                        } else {
                            stringResource(R.string.ir_not_supported_banner)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (hasHardwareIr) MintEmerald else SunAmber
                        )
                    )
                    if (lastTransmission != null) {
                        Text(
                            text = "Frame: ${lastTransmission.hexCode} (${lastTransmission.pulseCount} pulses)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1D33))
                    .border(0.5.dp, Color(0xFF1E3A5F), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = stringResource(R.string.raw_frame),
                    tint = CoolCyan400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FrameInspectorDialog(
    acState: AcState,
    lastTransmission: LastTransmissionInfo?,
    onProtocolChange: (IrProtocol) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val frameBytes = IrFrameGenerator.encodeState(acState)
    val hexCode = IrFrameGenerator.bytesToHexString(frameBytes)
    val timings = IrFrameGenerator.generateRawTimings(acState)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .testTag("dialog_frame_inspector")
            .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = Color(0x3300F2FE)),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0C1D33))
                        .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = CoolCyan400,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.frame_inspector_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF1F5F9)
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Protocol Selection
                Text(
                    text = stringResource(R.string.protocol_label),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CoolCyan300
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (proto in IrProtocol.values()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (acState.protocol == proto) Color(0xFF0A1828) else Color.Transparent)
                                .clickable { onProtocolChange(proto) }
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                        ) {
                            RadioButton(
                                selected = acState.protocol == proto,
                                onClick = { onProtocolChange(proto) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CoolCyan400,
                                    unselectedColor = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(proto.stringResId),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (acState.protocol == proto) FontWeight.Bold else FontWeight.Normal,
                                    color = if (acState.protocol == proto) Color(0xFFF1F5F9) else Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }

                // Raw Hex Display
                Text(
                    text = stringResource(R.string.hex_payload_title),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CoolCyan300
                    )
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF020712),
                    border = BorderStroke(1.dp, Color(0xFF162945))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = hexCode,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 2.sp,
                                color = CoolCyanGlow
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pulses: ${timings.size} marks/spaces | Buffer: ${timings.size * 4} bytes",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }

                // Stateful Fields Summary
                Text(
                    text = stringResource(R.string.decoded_state_title),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CoolCyan300
                    )
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF060E1A)),
                    border = BorderStroke(1.dp, Color(0xFF14243B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        StateFieldRow(label = stringResource(R.string.state_power_status), value = acState.power.name)
                        StateFieldRow(label = stringResource(R.string.state_target_temp), value = "${acState.temperature}°C (Code: ${acState.temperature - 16})")
                        StateFieldRow(label = stringResource(R.string.state_mode), value = "${acState.mode.name} (Code: ${acState.mode.rawValue})")
                        StateFieldRow(label = stringResource(R.string.state_fan_speed), value = "${acState.fanSpeed.name} (Code: ${acState.fanSpeed.rawValue})")
                        StateFieldRow(label = stringResource(R.string.state_vertical_swing), value = if (acState.swing) "ENABLED (0x01)" else "DISABLED (0x00)")
                        StateFieldRow(label = stringResource(R.string.state_turbo_mode), value = if (acState.turbo) "ACTIVE" else "OFF")
                        StateFieldRow(label = stringResource(R.string.state_display_light), value = if (acState.light) "ON" else "OFF")
                        StateFieldRow(label = stringResource(R.string.state_checksum), value = "Computed (Auto)")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_button_close_inspector")
            ) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = CoolCyan400,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        containerColor = SurfaceSlate,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun StateFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFFF1F5F9)
            )
        )
    }
}
