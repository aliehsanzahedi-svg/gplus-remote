package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R
import com.example.ui.theme.CoolCyan400
import com.example.ui.theme.DryDropletBlue
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.SunAmber

enum class AcPower {
    ON, OFF;

    val isPoweredOn: Boolean get() = this == ON
}

enum class AcMode(
    val rawValue: Int,
    val stringResId: Int,
    val color: Color
) {
    COOL(1, R.string.mode_cool, CoolCyan400),
    HEAT(4, R.string.mode_heat, SunAmber),
    AUTO(0, R.string.mode_auto, MintEmerald),
    FAN(3, R.string.mode_fan, CoolCyan400),
    DRY(2, R.string.mode_dry, DryDropletBlue);
}

enum class AcFanSpeed(
    val rawValue: Int,
    val stringResId: Int,
    val level: Int
) {
    AUTO(0, R.string.fan_auto, 0),
    LOW(1, R.string.fan_low, 1),
    MED(2, R.string.fan_med, 2),
    HIGH(3, R.string.fan_high, 3);
}

enum class IrProtocol(val displayName: String, val stringResId: Int) {
    GREE_STANDARD("GPlus / Gree Standard (8-Byte)", R.string.protocol_gree),
    GPLUS_NEC("GPlus / NEC Protocol (32-Bit)", R.string.protocol_nec)
}

data class AcState(
    val power: AcPower = AcPower.ON,
    val temperature: Int = 24, // 16°C to 30°C
    val mode: AcMode = AcMode.COOL,
    val fanSpeed: AcFanSpeed = AcFanSpeed.AUTO,
    val swing: Boolean = false,
    val turbo: Boolean = false,
    val light: Boolean = true,
    val sleep: Boolean = false,
    val protocol: IrProtocol = IrProtocol.GREE_STANDARD
) {
    companion object {
        const val MIN_TEMP = 16
        const val MAX_TEMP = 30
    }
}

data class LastTransmissionInfo(
    val timestampMillis: Long = 0L,
    val hexCode: String = "",
    val pulseCount: Int = 0,
    val isHardwareTransmitted: Boolean = false,
    val message: String = ""
)
