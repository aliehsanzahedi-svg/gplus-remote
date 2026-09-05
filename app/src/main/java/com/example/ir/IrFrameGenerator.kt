package com.example.ir

import com.example.model.AcFanSpeed
import com.example.model.AcMode
import com.example.model.AcPower
import com.example.model.AcState
import com.example.model.IrProtocol
import java.util.Locale

object IrFrameGenerator {

    const val CARRIER_FREQUENCY_HZ = 38000

    // Gree Protocol Timings (in microseconds)
    private const val GREE_HDR_MARK = 9000
    private const val GREE_HDR_SPACE = 4500
    private const val GREE_BIT_MARK = 560
    private const val GREE_ONE_SPACE = 1680
    private const val GREE_ZERO_SPACE = 560
    private const val GREE_BLOCK_SPACE = 20000
    private const val GREE_FOOTER_SPACE = 40000

    // NEC Protocol Timings (in microseconds)
    private const val NEC_HDR_MARK = 9000
    private const val NEC_HDR_SPACE = 4500
    private const val NEC_BIT_MARK = 560
    private const val NEC_ONE_SPACE = 1690
    private const val NEC_ZERO_SPACE = 560
    private const val NEC_FOOTER_SPACE = 20000

    /**
     * Builds the stateful frame bytes according to the selected protocol.
     */
    fun encodeState(state: AcState): ByteArray {
        return when (state.protocol) {
            IrProtocol.GREE_STANDARD -> encodeGreeFrame(state)
            IrProtocol.GPLUS_NEC -> encodeNecFrame(state)
        }
    }

    /**
     * Encodes complete 8-byte stateful frame for GPlus/Gree air conditioners.
     */
    fun encodeGreeFrame(state: AcState): ByteArray {
        val bytes = ByteArray(8)

        // Byte 0: [Sleep:1][Swing:1][FanSpeed:2][Power:1][Mode:3]
        val modeVal = state.mode.rawValue and 0x07
        val powerVal = if (state.power == AcPower.ON) 1 else 0
        val fanVal = state.fanSpeed.rawValue and 0x03
        val swingVal = if (state.swing) 1 else 0
        val sleepVal = if (state.sleep) 1 else 0

        bytes[0] = ((modeVal) or
                (powerVal shl 3) or
                (fanVal shl 4) or
                (swingVal shl 6) or
                (sleepVal shl 7)).toByte()

        // Byte 1: [Timer:4][Temp-16:4]
        val clampedTemp = state.temperature.coerceIn(AcState.MIN_TEMP, AcState.MAX_TEMP)
        val tempCode = (clampedTemp - AcState.MIN_TEMP) and 0x0F
        bytes[1] = (tempCode and 0x0F).toByte()

        // Byte 2: [Health/Blow:2][Light:1][Turbo:1][Timer2:4]
        val turboVal = if (state.turbo) 1 else 0
        val lightVal = if (state.light) 1 else 0
        bytes[2] = ((turboVal shl 4) or (lightVal shl 5)).toByte()

        // Byte 3: Gree Model Identification code (standard default 0x50)
        bytes[3] = 0x50.toByte()

        // Byte 4: Vertical swing angle detail (0x01 if auto-swing active, 0x00 otherwise)
        bytes[4] = (if (state.swing) 0x01 else 0x00).toByte()

        // Byte 5: Energy saving / display bits
        bytes[5] = 0x00.toByte()

        // Byte 6: Reserved
        bytes[6] = 0x00.toByte()

        // Byte 7: Checksum
        // Standard Gree protocol checksum:
        // Sum of all nibbles from byte 0 to byte 6 + offset (10), masked with 0x0F,
        // placed in the upper nibble of Byte 7.
        var nibbleSum = 0
        for (i in 0 until 7) {
            val b = bytes[i].toInt() and 0xFF
            nibbleSum += (b and 0x0F) + ((b ushr 4) and 0x0F)
        }
        val checksum = (nibbleSum + 0x0A) and 0x0F
        bytes[7] = ((checksum shl 4) and 0xF0).toByte()

        return bytes
    }

    /**
     * Encodes 4-byte frame for GPlus NEC AC variations.
     */
    fun encodeNecFrame(state: AcState): ByteArray {
        val address = 0xC3
        val invAddress = address.inv() and 0xFF

        val modeNibble = state.mode.rawValue and 0x07
        val powerBit = if (state.power == AcPower.ON) 1 else 0
        val tempNibble = (state.temperature.coerceIn(16, 30) - 16) and 0x0F
        val fanBits = state.fanSpeed.rawValue and 0x03

        val command = (modeNibble or (powerBit shl 3) or (fanBits shl 4) or (tempNibble shl 6)) and 0xFF
        val invCommand = command.inv() and 0xFF

        return byteArrayOf(
            address.toByte(),
            invAddress.toByte(),
            command.toByte(),
            invCommand.toByte()
        )
    }

    /**
     * Converts state directly to alternating microsecond durations (mark/space) for ConsumerIrManager.
     */
    fun generateRawTimings(state: AcState): IntArray {
        val bytes = encodeState(state)
        return when (state.protocol) {
            IrProtocol.GREE_STANDARD -> generateGreeRawTimings(bytes)
            IrProtocol.GPLUS_NEC -> generateNecRawTimings(bytes)
        }
    }

    /**
     * Generates microsecond timing buffer for Gree standard 8-byte frame.
     */
    fun generateGreeRawTimings(bytes: ByteArray): IntArray {
        // 1 header pair + 32 bits (Block 1) + 1 block gap pair + 32 bits (Block 2) + 1 footer pair = 136 ints
        val timings = ArrayList<Int>(140)

        // 1. Leader Header Pulse
        timings.add(GREE_HDR_MARK)
        timings.add(GREE_HDR_SPACE)

        // 2. Block 1: Bytes 0..3 (32 bits, LSB first)
        for (byteIndex in 0..3) {
            val b = bytes[byteIndex].toInt() and 0xFF
            for (bit in 0..7) {
                val isOne = ((b ushr bit) and 1) == 1
                timings.add(GREE_BIT_MARK)
                timings.add(if (isOne) GREE_ONE_SPACE else GREE_ZERO_SPACE)
            }
        }

        // 3. Block 1 spacer / inter-block gap
        timings.add(GREE_BIT_MARK)
        timings.add(GREE_BLOCK_SPACE)

        // 4. Block 2: Bytes 4..7 (32 bits, LSB first)
        for (byteIndex in 4..7) {
            val b = bytes[byteIndex].toInt() and 0xFF
            for (bit in 0..7) {
                val isOne = ((b ushr bit) and 1) == 1
                timings.add(GREE_BIT_MARK)
                timings.add(if (isOne) GREE_ONE_SPACE else GREE_ZERO_SPACE)
            }
        }

        // 5. Trailing stop pulse and ending space
        timings.add(GREE_BIT_MARK)
        timings.add(GREE_FOOTER_SPACE)

        return timings.toIntArray()
    }

    /**
     * Generates microsecond timing buffer for NEC 32-bit frame.
     */
    fun generateNecRawTimings(bytes: ByteArray): IntArray {
        val timings = ArrayList<Int>(70)

        // Leader
        timings.add(NEC_HDR_MARK)
        timings.add(NEC_HDR_SPACE)

        // 32 bits, LSB first
        for (b in bytes) {
            val byteVal = b.toInt() and 0xFF
            for (bit in 0..7) {
                val isOne = ((byteVal ushr bit) and 1) == 1
                timings.add(NEC_BIT_MARK)
                timings.add(if (isOne) NEC_ONE_SPACE else NEC_ZERO_SPACE)
            }
        }

        // Stop mark
        timings.add(NEC_BIT_MARK)
        timings.add(NEC_FOOTER_SPACE)

        return timings.toIntArray()
    }

    /**
     * Formats byte array into human-readable uppercase hexadecimal string.
     */
    fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString(" ") { String.format(Locale.US, "%02X", it) }
    }
}
