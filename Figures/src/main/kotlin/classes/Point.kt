package classes

import kotlin.math.pow

class Point(var x: Double = 0.0, var y: Double = 0.0) {
    operator fun compareTo(otherPoint: Point): Int {
        return if (this.x > otherPoint.x && this.y > otherPoint.y) {
            1
        } else {
            -1
        }
    }

    fun distanceTo(otherPoint: Point): Double {
        return ((otherPoint.x - this.x).pow(2) + (otherPoint.y - this.y).pow(2)).pow(0.5)
    }

    override fun toString(): String {
        return "(x: ${this.x}, y: ${this.y})"
    }
}