package com.example.mapapp

import com.example.mapapp.utils.RouteGenerator
import com.example.mapapp.utils.TravelRoute

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RouteGeneratorUnitTest {
    val routeGenerator: RouteGenerator = RouteGenerator()
    @Test
    fun testAccurateRouteGeneration() {
        // Initiating test cases.
        val matrix1 = arrayOf(
            arrayOf(0,2,6,7,13),
            arrayOf(0,0,4,7,32),
            arrayOf(0,4,0,6,25),
            arrayOf(0,7,6,0,3),
            arrayOf(0,25,32,3,0),
        )
        val expectedResult1 = TravelRoute(arrayOf(0,1,2,3,4),15)
        val matrix2 = arrayOf(
            arrayOf(0,2,6,7,13),
            arrayOf(0,0,4,7,32),
            arrayOf(0,4,0,37,25),
            arrayOf(0,7,37,0,3),
            arrayOf(0,25,32,3,0)
        )
        val expectedResult2 = TravelRoute(arrayOf(0,2,1,3,4),20)
        assertEquals(expectedResult1,
            routeGenerator.generateRouteAccurate(matrix1))
        assertEquals(expectedResult2,
            routeGenerator.generateRouteAccurate(matrix2))
    }
    @Test
    fun testGreedyRouteGeneration(){
        // Initiating test cases.
        val matrix1 = arrayOf(
            arrayOf(0,2,6,7,13),
            arrayOf(0,0,4,7,32),
            arrayOf(0,4,0,6,25),
            arrayOf(0,7,6,0,3),
            arrayOf(0,25,32,3,0),
        )
        val expectedResult1 = TravelRoute(arrayOf(0,1,2,3,4),15)
        val matrix2 = arrayOf(
            arrayOf(0,2,6,7,13),
            arrayOf(0,0,4,7,32),
            arrayOf(0,4,0,37,25),
            arrayOf(0,7,37,0,3),
            arrayOf(0,25,32,3,0)
        )
        val expectedResult2 = TravelRoute(arrayOf(0,1,2,4,3),34)
        assertEquals(expectedResult1,
            routeGenerator.generateRouteGreedy(matrix1))
        assertEquals(expectedResult2,
            routeGenerator.generateRouteGreedy(matrix2))

    }
    @Test
    fun testCheckIfAllNodesAreReachable(){
        val matrix1 = arrayOf(
            arrayOf(0,2,6,7,13),
            arrayOf(0,0,4,7,32),
            arrayOf(0,4,0,6,25),
            arrayOf(0,7,6,0,3),
            arrayOf(0,25,32,3,0),
        )
        assertTrue(routeGenerator.checkIfAllNodesAreReachable(matrix1))
        val matrix2 = arrayOf(
            arrayOf(0,2,6,7,0),
            arrayOf(0,0,4,7,0),
            arrayOf(0,4,0,6,0),
            arrayOf(0,7,6,0,0),
            arrayOf(0,0,0,0,0),
        )
        assertFalse(routeGenerator.checkIfAllNodesAreReachable(matrix2))
    }
}