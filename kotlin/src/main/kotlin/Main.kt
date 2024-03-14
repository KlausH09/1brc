package org.example

import kotlin.io.path.Path


fun main(args: Array<String>) {
    val path = args.singleOrNull() ?: "./weather_stations.csv"

    val result = Path(path).toFile().bufferedReader().lineSequence()
        .dropWhile { it.startsWith('#') }
        .map { Measurement(it) }
        .groupingBy { it.stationName }
        .fold(StationData()) { acc, measurement -> acc + measurement.temperature }
        .toSortedMap()
    println(result)
}

data class Measurement(
    val stationName: String,
    val temperature: Double,
) {
    constructor(data: String) : this(
        stationName = data.substringBefore(';'),
        temperature = data.substringAfter(';').toDouble(),
    )
}

data class StationData(
    val tempSum: Double = .0,
    val tempMin: Double = Double.MAX_VALUE,
    val tempMax: Double = Double.MIN_VALUE,
    val n: UInt = 0u,
) {
    private val tempMean get() = tempSum / n.toDouble()

    override fun toString() = "${tempMin.toStr()}/${tempMean.toStr()}/${tempMax.toStr()}"

    operator fun plus(temp: Double) = StationData(
        tempSum = tempSum + temp,
        tempMin = minOf(tempMin, temp),
        tempMax = maxOf(tempMax, temp),
        n = n + 1u,
    )

    companion object {
        private fun Double.toStr() = "%.1f".format(this)
    }
}