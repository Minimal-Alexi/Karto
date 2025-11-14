package com.example.mapapp.data.model

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

    fun generateRoute(originTravelCost : Array<Int>,travelCostMatrix: Array<Array<Int>>) : TravelRoute{
        val numberOfNodes = travelCostMatrix.size
        val availablePermutations = permutations((1..numberOfNodes).toMutableList(),arrayOf(0))
        var minimumTravelRoute = TravelRoute(
            availablePermutations[0],
            calculateTravelCost
                (
                originTravelCost,
                availablePermutations[0],
                travelCostMatrix
                        )
        )
        for(path in availablePermutations){
            val cost = calculateTravelCost(originTravelCost, path, travelCostMatrix)
            if (cost < minimumTravelRoute.travelCost) {
                minimumTravelRoute = TravelRoute(path, cost)
            }
        }
        return minimumTravelRoute
    }
    private fun calculateTravelCost(originTravelCost: Array<Int>,travelPath: Array<Int>, travelCostMatrix: Array<Array<Int>>):Int{
        var sum = originTravelCost[travelPath[1]]
        for(i in 2 until travelPath.size - 1){
            sum += travelCostMatrix[travelPath[i-1]][travelPath[i]]
        }
        return sum
    }
    private fun permutations(
        nodes: List<Int>,
        prefix: Array<Int> = emptyArray(),
        acc: MutableList<Array<Int>> = mutableListOf()
    ): List<Array<Int>> {

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