package com.example.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.R
import com.example.ui.components.FrameInspectorDialog
import com.example.ui.components.LcdDisplayPanel
import com.example.ui.components.NoIrBlasterDialog
import com.example.ui.components.RemoteControls
import com.example.ui.components.TransmissionStatusBar
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CoolCyan300
import com.example.ui.theme.CoolCyan400
import com.example.ui.theme.CoolCyanGlow
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.SunAmber
import com.example.viewmodel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteScreen(
    viewModel: RemoteViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val baseContext = LocalContext.current
    val baseConfig = LocalConfiguration.current
    val baseLayoutDir = LocalLayoutDirection.current

    val (localizedContext, localizedConfig, layoutDirection) = remember(uiState.forcePersianLanguage, baseContext, baseConfig, baseLayoutDir) {
        if (uiState.forcePersianLanguage) {
            val persianLocale = Locale("fa")
            val newConfig = Configuration(baseConfig).apply {
                setLocale(persianLocale)
                setLayoutDirection(persianLocale)
            }
            val ctx = baseContext.createConfigurationContext(newConfig)
            Triple(ctx, newConfig, LayoutDirection.Rtl)
        } else {
            Triple(baseContext, baseConfig, baseLayoutDir)
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfig
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SlateDark950,
                            Color(0xFF030A17),
                            SlateDark950
                        )
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = if (uiState.forcePersianLanguage) 0.sp else 0.75.sp,
                                    color = Color(0xFFF8FAFC)
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (uiState.hasHardwareIr) CoolCyan400 else SunAmber)
                                        .shadow(4.dp, CircleShape, spotColor = if (uiState.hasHardwareIr) CoolCyanGlow else AmberGlow)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (uiState.hasHardwareIr) {
                                        stringResource(R.string.ir_transmitter_badge)
                                    } else {
                                        stringResource(R.string.simulation_badge)
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = if (uiState.forcePersianLanguage) 0.sp else 0.8.sp,
                                        color = if (uiState.hasHardwareIr) CoolCyan300 else SunAmber
                                    )
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xF2020617),
                        titleContentColor = Color(0xFFF1F5F9)
                    ),
                    actions = {
                        // Protocol & Frame Inspector Button
                        IconButton(
                            onClick = { viewModel.setShowFrameInspector(true) },
                            modifier = Modifier
                                .testTag("button_open_frame_inspector")
                                .clip(CircleShape)
                                .background(Color(0xFF0B172E))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = stringResource(R.string.raw_frame),
                                tint = CoolCyan400,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Haptic feedback toggle
                        IconButton(
                            onClick = { viewModel.toggleHaptics() },
                            modifier = Modifier
                                .testTag("button_toggle_haptics")
                                .clip(CircleShape)
                                .background(Color(0xFF0B172E))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = stringResource(R.string.haptics),
                                tint = if (uiState.hapticsEnabled) CoolCyan400 else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Language Toggle (FA / EN)
                        IconButton(
                            onClick = { viewModel.toggleLanguage() },
                            modifier = Modifier
                                .testTag("button_toggle_language")
                                .clip(CircleShape)
                                .background(Color(0xFF0B172E))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = CoolCyan400,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (uiState.forcePersianLanguage) "FA" else "EN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = CoolCyan300
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 480.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // TOP PHYSICAL IR EMITTER DIODE (Simulated hardware lens)
                    IrEmitterLens(
                        isTransmitting = uiState.isTransmitting,
                        modifier = Modifier.testTag("simulated_ir_lens")
                    )

                    // REALISTIC LCD/LED DISPLAY PANEL
                    LcdDisplayPanel(
                        acState = uiState.acState,
                        isTransmitting = uiState.isTransmitting
                    )

                    // TRANSMISSION STATUS BANNER
                    TransmissionStatusBar(
                        hasHardwareIr = uiState.hasHardwareIr,
                        lastTransmission = uiState.lastTransmission,
                        onOpenInspector = { viewModel.setShowFrameInspector(true) }
                    )

                    // MAIN REMOTE CONTROL BUTTONS
                    RemoteControls(
                        acState = uiState.acState,
                        onPowerToggle = { viewModel.togglePower() },
                        onTempIncrease = { viewModel.increaseTemp() },
                        onTempDecrease = { viewModel.decreaseTemp() },
                        onModeSelect = { viewModel.setMode(it) },
                        onFanSpeedSelect = { viewModel.setFanSpeed(it) },
                        onSwingToggle = { viewModel.toggleSwing() },
                        onTurboToggle = { viewModel.toggleTurbo() },
                        onLightToggle = { viewModel.toggleLight() },
                        onSleepToggle = { viewModel.toggleSleep() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // DIALOG: Startup Safety Check (When device lacks IR emitter)
            if (uiState.showNoIrDialog) {
                NoIrBlasterDialog(
                    onDismiss = { viewModel.dismissNoIrDialog() }
                )
            }

            // DIALOG: Frame & Protocol Inspector
            if (uiState.showFrameInspector) {
                FrameInspectorDialog(
                    acState = uiState.acState,
                    lastTransmission = uiState.lastTransmission,
                    onProtocolChange = { viewModel.setProtocol(it) },
                    onDismiss = { viewModel.setShowFrameInspector(false) }
                )
            }
        }
    }
}

@Composable
private fun IrEmitterLens(
    isTransmitting: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(96.dp)
            .height(16.dp)
            .shadow(
                elevation = if (isTransmitting) 14.dp else 2.dp,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                spotColor = if (isTransmitting) CoolCyanGlow else Color.Black
            )
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1726),
                        Color(0xFF030712)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = if (isTransmitting) {
                        listOf(CoolCyanGlow, Color.White, CoolCyanGlow)
                    } else {
                        listOf(SlateDark800, Color(0xFF1E293B), SlateDark800)
                    }
                ),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Infrared window lens with LED core
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (isTransmitting) CoolCyanGlow else Color(0xFF1E293B)
                    )
                    .shadow(if (isTransmitting) 10.dp else 0.dp, CircleShape, spotColor = CoolCyanGlow)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isTransmitting) Color.White else Color(0xFF0A1220)
                    )
                    .border(
                        1.dp,
                        if (isTransmitting) CoolCyanGlow else Color(0xFF263B5D),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (isTransmitting) CoolCyanGlow else Color(0xFF1E293B)
                    )
                    .shadow(if (isTransmitting) 10.dp else 0.dp, CircleShape, spotColor = CoolCyanGlow)
            )
        }
    }
}
