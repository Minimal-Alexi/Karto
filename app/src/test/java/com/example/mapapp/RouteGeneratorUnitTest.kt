package com.example.mapapp

import com.example.mapapp.data.model.RouteGenerator
import com.example.mapapp.data.model.TravelRoute

import org.junit.Test
import org.junit.Assert.*
import kotlin.collections.listOf

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RouteGeneratorUnitTest {
    val routeGenerator: RouteGenerator = RouteGenerator()
    @Test
    fun testRouteGeneration() {
        val matrix1 = arrayOf(
            arrayOf(0,4,7,32),
            arrayOf(4,0,6,25),
            arrayOf(7,6,0,3),
            arrayOf(25,32,3,0),
        )
        val origin1Distances = arrayOf(2,6,7,13)
        val expectedResult1 = TravelRoute(arrayOf(0,1,2,3,4),15)
        val matrix2 = arrayOf(
            arrayOf(0,4,7,32),
            arrayOf(4,0,37,25),
            arrayOf(7,37,0,3),
            arrayOf(25,32,3,0)
        )
        val origin2Distances = arrayOf(2,6,7,13)
        val expectedResult2 = TravelRoute(arrayOf(0,2,1,3,4),20)
        assertEquals(expectedResult1, routeGenerator.generateRoute(
            origin1Distances,
            matrix1)
        )
        assertEquals(expectedResult2,routeGenerator.generateRoute(
            origin2Distances,
            matrix2
        ))
    }
}