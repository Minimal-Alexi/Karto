package com.example.mapapp.utils

class UnreachableNodeException(message:String) : Exception(message)

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
    If there are unreachable nodes, BOTH algorithms will break!
     */
    fun generateRoute(travelCostMatrix: Array<Array<Int>>): TravelRoute{
        if(checkIfAllNodesAreReachable(travelCostMatrix)){
            return if(travelCostMatrix.size >= 10)
                generateRouteGreedy(travelCostMatrix)
            else
                generateRouteAccurate(travelCostMatrix)
        }else{
            throw UnreachableNodeException("Not all nodes can be visited. There is a missing route.")
        }
    }
    /*
    generateRouteAccurate generates all the permutations of possible routes that can be done, and finds the least costly route.
    If you check the test case, you can notice the routes are WAY better than the greedyRoutes, but this comes at a time cost.
    To be discussed in detail later.
    This mean its time complexity is (n!), meaning it shouldn't be used for more than ten or so nodes, unless we want to turn the app into a screen saver.
    */
    fun generateRouteAccurate(travelCostMatrix: Array<Array<Int>>): TravelRoute {
        val numberOfNodes = travelCostMatrix.size
        val availablePermutations = permutations(
            (1..numberOfNodes - 1).toMutableList(),
            arrayOf(0)
        )
        var minimumTravelRoute = TravelRoute(
            availablePermutations[0], calculateTravelCost
                (
                availablePermutations[0],
                travelCostMatrix
            )
        )
        for (i in availablePermutations) {
            val newCost = calculateTravelCost(i, travelCostMatrix)
            if(newCost < minimumTravelRoute.travelCost){
                minimumTravelRoute = TravelRoute(i,newCost)
            }
        }
        return minimumTravelRoute
    }
    /*
    generateRouteGreedy creates a route, by travelling from node to node, selecting the shortest route between each two nodes.
    This mean its time complexity is (n^2), making it way more time efficient than generateRouteAccurate(), at the cost of the result being "acceptable enough."
    */
    fun generateRouteGreedy(travelCostMatrix: Array<Array<Int>>, startingPoint: Int = 0) : TravelRoute{
        val visitedNodes : MutableList<Boolean> = MutableList(travelCostMatrix.size){false}
        visitedNodes[startingPoint] = true

        var currentNode = startingPoint
        var travelCost = 0
        val travelPath: MutableList<Int> = mutableListOf(currentNode)

        while(visitedNodes.contains(false)){
            var selectedNextNode = -1
            var minimumValue = Int.MAX_VALUE
            var indexOfI = 0
            for(i in travelCostMatrix[currentNode]){
                if(minimumValue > i && i >= 0 && !visitedNodes[indexOfI]){
                    selectedNextNode = indexOfI
                    minimumValue = i
                }
                indexOfI++
            }

            if (selectedNextNode == -1) break

            visitedNodes[selectedNextNode] = true
            currentNode = selectedNextNode
            travelPath.add(currentNode)
            travelCost += minimumValue
        }

        return TravelRoute(travelPath.toTypedArray(),travelCost)
    }
    fun checkIfAllNodesAreReachable(travelCostMatrix: Array<Array<Int>>): Boolean{
        val visitedNodes = BooleanArray(travelCostMatrix.size)
        dfs(travelCostMatrix,visitedNodes)
        return !visitedNodes.contains(false)
    }
    private fun dfs(
        travelCostMatrix: Array<Array<Int>>,
        visited: BooleanArray,
        startingPoint: Int = 0
    ) {
        visited[startingPoint] = true

        for (i in travelCostMatrix.indices) {
            if (!visited[i] && travelCostMatrix[startingPoint][i] != 0) {
                dfs(travelCostMatrix, visited, i)
            }
        }
    }
    private fun calculateTravelCost(travelPath: Array<Int>, travelCostMatrix: Array<Array<Int>>):Int{
        val n = travelPath.size
        var sum = 0
        for(i in 1..n - 1){
            sum += travelCostMatrix[travelPath[i - 1]][travelPath[i]]
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