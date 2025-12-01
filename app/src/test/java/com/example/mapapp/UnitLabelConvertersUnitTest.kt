package com.example.mapapp

import com.example.mapapp.utils.getDistanceLabel
import com.example.mapapp.utils.getTimeLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitLabelConvertersUnitTest {
    @Test
    fun timeLabelLessThanHour() {
        assertEquals("1 min", getTimeLabel("59s"))
        assertEquals("1 min", getTimeLabel("31s"))
        assertEquals("1 min", getTimeLabel("60s"))

        assertEquals("2 min", getTimeLabel("120s"))
        assertEquals("2 min", getTimeLabel("119s"))

        assertEquals("3 min", getTimeLabel("140s"))
    }

    @Test
    fun timeLabelMoreThanHour() {
        assertEquals("1 h 0 min", getTimeLabel("3600s"))
        assertEquals("1 h 1 min", getTimeLabel("3660s"))
        assertEquals("24 h 0 min", getTimeLabel("86400s"))
    }

    @Test
    fun distanceLabelLessThanKm() {
        assertEquals("100 m", getDistanceLabel("100"))
        assertEquals("280 m", getDistanceLabel("280"))
        assertEquals("1 m", getDistanceLabel("1"))
    }

    @Test
    fun distanceLabelMoreThanKm() {
        assertEquals("1.1 km", getDistanceLabel("1099"))
        assertEquals("7.2 km", getDistanceLabel("7200"))
        assertEquals("3.2 km", getDistanceLabel("3150"))
        assertEquals("3.1 km", getDistanceLabel("3149"))
    }
}