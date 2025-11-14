package com.example.mapapp.data.model

import java.util.Collections
import kotlin.math.min

data class TravelRoute(
    val travelPath : Array<Int>,
    val travelCost: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TravelRoute

        if (travelCost != other.travelCost) return false
        if (!travelPath.contentEquals(other.travelPath)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = travelCost
        result = 31 * result + travelPath.contentHashCode()
        return result
    }
}

class RouteGenerator(){

//    fun generateRoute(originTravelCost : Array<Int>,travelCostMatrix: Array<Array<Int>>) : TravelRoute{
//        val numberOfNodes = travelCostMatrix.size
//
//    }
    fun permutations(
        nodes: List<Int>,
        prefix: List<Int> = emptyList(),
        acc: MutableList<List<Int>> = mutableListOf()
    ): List<List<Int>> {

        if (nodes.isEmpty()) {
            acc.add(prefix)
            return acc
        }

        for (i in nodes.indices) {
            val next = prefix + nodes[i]
            val remaining = nodes.take(i) + nodes.drop(i + 1)
            permutations(remaining, next, acc)
        }

        return acc
    }

}