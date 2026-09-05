package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ir.IrFrameGenerator
import com.example.model.AcFanSpeed
import com.example.model.AcMode
import com.example.model.AcPower
import com.example.model.AcState
import com.example.model.IrProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GPlus AC Remote", appName)
    }

    @Test
    fun `gree frame encodes power, mode, temp and checksum correctly`() {
        val state = AcState(
            power = AcPower.ON,
            temperature = 24,
            mode = AcMode.COOL,
            fanSpeed = AcFanSpeed.HIGH,
            swing = true,
            turbo = false,
            protocol = IrProtocol.GREE_STANDARD
        )

        val frame = IrFrameGenerator.encodeGreeFrame(state)
        assertEquals(8, frame.size)

        // Byte 0: mode=1 (COOL), power=1 (shl 3 = 8), fan=3 (shl 4 = 48), swing=1 (shl 6 = 64)
        // 1 | 8 | 48 | 64 = 121 (0x79)
        val byte0 = frame[0].toInt() and 0xFF
        assertEquals(1, byte0 and 0x07) // Mode COOL
        assertEquals(8, byte0 and 0x08) // Power ON
        assertEquals(48, byte0 and 0x30) // Fan HIGH
        assertEquals(64, byte0 and 0x40) // Swing ON

        // Byte 1: temp 24 -> 24 - 16 = 8 (0x08)
        val byte1 = frame[1].toInt() and 0xFF
        assertEquals(8, byte1 and 0x0F)

        // Byte 3: Magic signature
        assertEquals(0x50.toByte(), frame[3])

        // Raw timings should produce valid marks and spaces
        val timings = IrFrameGenerator.generateGreeRawTimings(frame)
        assertTrue("Timings buffer must contain valid pulse sequence", timings.isNotEmpty())
        assertEquals(9000, timings[0]) // Header mark
        assertEquals(4500, timings[1]) // Header space
    }

    @Test
    fun `nec frame encodes 4 bytes correctly`() {
        val state = AcState(
            power = AcPower.ON,
            temperature = 20,
            mode = AcMode.HEAT,
            protocol = IrProtocol.GPLUS_NEC
        )

        val frame = IrFrameGenerator.encodeNecFrame(state)
        assertEquals(4, frame.size)
        assertEquals(0xC3.toByte(), frame[0])

        val timings = IrFrameGenerator.generateNecRawTimings(frame)
        assertTrue(timings.isNotEmpty())
    }
}
