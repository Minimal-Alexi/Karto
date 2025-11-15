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
    /*
    The route generator class contains two functions, generateRouteCorrect, and generateRouteGreedy.
    They both take the input travelCostMatrix, and both of the algorithms start from the point of origin 0.
    They return a TravelRoute data class, which contains the travel path, and the cost of said route.
     */
    /*
    generateRouteAccurate generates all the permutations of possible routes that can be done, and finds the least costly route.
    If you check the test case, you can notice the routes are WAY better than the greedyRoutes, but this comes at a time cost.
    To be discussed in detail later.
    This mean its time complexity is (n!), meaning it shouldn't be used for more than ten or so nodes, unless we want to turn the app into a screen saver.
    */
    /*TODO: WORK ON GENERATE ROUTE ACCURATE.*/
    fun generateRouteAccurate(travelCostMatrix: Array<Array<Int>>) : TravelRoute{
        val numberOfNodes = travelCostMatrix.size
        val availablePermutations = permutations((1.. numberOfNodes - 1).toMutableList(),
            arrayOf(0))
        return TravelRoute(arrayOf(0),0)
    }
    /*
    generateRouteGreedy creates a route, by travelling from node to node, selecting the shortest route between each two nodes.
    This mean its time complexity is (n), making it way more time efficient than generateRouteAccurate(), at the cost of the result being "acceptable enough."
    */
    fun generateRouteGreedy(travelCostMatrix: Array<Array<Int>>) : TravelRoute{
        return TravelRoute(arrayOf(0),0)
    }
//    private fun calculateTravelCost(travelPath: Array<Int>, travelCostMatrix: Array<Array<Int>>):Int{
//
//    }
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