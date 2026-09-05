package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ir.IrFrameGenerator
import com.example.ir.IrTransmitter
import com.example.model.AcFanSpeed
import com.example.model.AcMode
import com.example.model.AcPower
import com.example.model.AcState
import com.example.model.IrProtocol
import com.example.model.LastTransmissionInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RemoteUiState(
    val acState: AcState = AcState(),
    val hasHardwareIr: Boolean = false,
    val showNoIrDialog: Boolean = false,
    val isDemoMode: Boolean = false,
    val isTransmitting: Boolean = false,
    val lastTransmission: LastTransmissionInfo? = null,
    val showFrameInspector: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val carrierFrequencyText: String = "38 kHz",
    val forcePersianLanguage: Boolean = false
)

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val transmitter = IrTransmitter(application)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    init {
        val hasIr = transmitter.hasIrEmitter()
        val freqDesc = transmitter.getCarrierFrequencyDescription()
        _uiState.update {
            it.copy(
                hasHardwareIr = hasIr,
                showNoIrDialog = !hasIr,
                isDemoMode = !hasIr,
                carrierFrequencyText = freqDesc
            )
        }
    }

    fun dismissNoIrDialog() {
        _uiState.update { it.copy(showNoIrDialog = false) }
    }

    fun setShowFrameInspector(show: Boolean) {
        _uiState.update { it.copy(showFrameInspector = show) }
    }

    fun toggleHaptics() {
        _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
        performHapticClick()
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(forcePersianLanguage = !it.forcePersianLanguage) }
        performHapticClick()
    }

    fun setProtocol(protocol: IrProtocol) {
        _uiState.update {
            it.copy(acState = it.acState.copy(protocol = protocol))
        }
        performHapticClick()
        transmitCurrentState()
    }

    fun togglePower() {
        performHapticClick()
        val current = _uiState.value.acState
        val newPower = if (current.power == AcPower.ON) AcPower.OFF else AcPower.ON
        val newState = current.copy(power = newPower)
        _uiState.update { it.copy(acState = newState) }
        transmitCurrentState()
    }

    fun increaseTemp() {
        performHapticClick()
        val current = _uiState.value.acState
        if (current.temperature < AcState.MAX_TEMP) {
            val newState = current.copy(temperature = current.temperature + 1)
            _uiState.update { it.copy(acState = newState) }
            transmitCurrentState()
        }
    }

    fun decreaseTemp() {
        performHapticClick()
        val current = _uiState.value.acState
        if (current.temperature > AcState.MIN_TEMP) {
            val newState = current.copy(temperature = current.temperature - 1)
            _uiState.update { it.copy(acState = newState) }
            transmitCurrentState()
        }
    }

    fun setTemperature(temp: Int) {
        performHapticClick()
        val clamped = temp.coerceIn(AcState.MIN_TEMP, AcState.MAX_TEMP)
        val current = _uiState.value.acState
        if (current.temperature != clamped) {
            val newState = current.copy(temperature = clamped)
            _uiState.update { it.copy(acState = newState) }
            transmitCurrentState()
        }
    }

    fun setMode(mode: AcMode) {
        performHapticClick()
        val current = _uiState.value.acState
        if (current.mode != mode) {
            val newState = current.copy(mode = mode)
            _uiState.update { it.copy(acState = newState) }
            transmitCurrentState()
        }
    }

    fun setFanSpeed(speed: AcFanSpeed) {
        performHapticClick()
        val current = _uiState.value.acState
        if (current.fanSpeed != speed) {
            val newState = current.copy(fanSpeed = speed)
            _uiState.update { it.copy(acState = newState) }
            transmitCurrentState()
        }
    }

    fun toggleSwing() {
        performHapticClick()
        val current = _uiState.value.acState
        val newState = current.copy(swing = !current.swing)
        _uiState.update { it.copy(acState = newState) }
        transmitCurrentState()
    }

    fun toggleTurbo() {
        performHapticClick()
        val current = _uiState.value.acState
        val newState = current.copy(turbo = !current.turbo)
        _uiState.update { it.copy(acState = newState) }
        transmitCurrentState()
    }

    fun toggleLight() {
        performHapticClick()
        val current = _uiState.value.acState
        val newState = current.copy(light = !current.light)
        _uiState.update { it.copy(acState = newState) }
        transmitCurrentState()
    }

    fun toggleSleep() {
        performHapticClick()
        val current = _uiState.value.acState
        val newState = current.copy(sleep = !current.sleep)
        _uiState.update { it.copy(acState = newState) }
        transmitCurrentState()
    }

    fun transmitCurrentState() {
        val state = _uiState.value.acState
        val frameBytes = IrFrameGenerator.encodeState(state)
        val hexString = IrFrameGenerator.bytesToHexString(frameBytes)
        val rawPattern = IrFrameGenerator.generateRawTimings(state)

        viewModelScope.launch {
            _uiState.update { it.copy(isTransmitting = true) }

            val success: Boolean
            val message: String

            if (_uiState.value.hasHardwareIr) {
                val result = transmitter.transmit(rawPattern)
                success = result.isSuccess
                message = if (success) "IR Signal Transmitted (38kHz)" else "IR Error: ${result.exceptionOrNull()?.message}"
            } else {
                // Simulation mode
                success = true
                message = "Simulated IR Signal Generated"
            }

            _uiState.update {
                it.copy(
                    lastTransmission = LastTransmissionInfo(
                        timestampMillis = System.currentTimeMillis(),
                        hexCode = hexString,
                        pulseCount = rawPattern.size,
                        isHardwareTransmitted = _uiState.value.hasHardwareIr,
                        message = message
                    )
                )
            }

            // Keep LED pulse animation visible for 280ms
            delay(280)
            _uiState.update { it.copy(isTransmitting = false) }
        }
    }

    private fun performHapticClick() {
        if (!_uiState.value.hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(25)
            }
        } catch (_: Exception) {
        }
    }
}
