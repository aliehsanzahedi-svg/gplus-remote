package com.example.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.util.Log

class IrTransmitter(context: Context) {

    private val tag = "IrTransmitter"

    private val irManager: ConsumerIrManager? = try {
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
    } catch (e: Exception) {
        Log.w(tag, "ConsumerIrManager service not accessible", e)
        null
    }

    /**
     * Checks if the device has an active Infrared (IR) emitter.
     */
    fun hasIrEmitter(): Boolean {
        return try {
            irManager?.hasIrEmitter() == true
        } catch (e: Exception) {
            Log.w(tag, "Failed to query hasIrEmitter", e)
            false
        }
    }

    /**
     * Returns the supported carrier frequencies for diagnostic display.
     */
    fun getCarrierFrequencyDescription(): String {
        return try {
            val ranges = irManager?.carrierFrequencies
            if (ranges != null && ranges.isNotEmpty()) {
                ranges.joinToString(", ") { "${it.minFrequency / 1000}-${it.maxFrequency / 1000}kHz" }
            } else {
                "38 kHz Carrier (Standard)"
            }
        } catch (e: Exception) {
            "38 kHz"
        }
    }

    /**
     * Transmits the raw microsecond timing pattern using the IR blaster at 38kHz.
     */
    fun transmit(pattern: IntArray, frequency: Int = IrFrameGenerator.CARRIER_FREQUENCY_HZ): Result<Unit> {
        val manager = irManager ?: return Result.failure(IllegalStateException("ConsumerIrManager is unavailable"))
        return try {
            if (!manager.hasIrEmitter()) {
                return Result.failure(IllegalStateException("Device does not possess an IR Emitter"))
            }
            manager.transmit(frequency, pattern)
            Log.d(tag, "Successfully transmitted ${pattern.size} pulses at ${frequency}Hz")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error transmitting IR signal", e)
            Result.failure(e)
        }
    }
}
