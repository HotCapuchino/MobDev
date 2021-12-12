package classes

import interfaces.RotateDirection
import kotlin.math.abs
import kotlin.math.pow

class Rect(private var topLeftCorner: Point, private var bottomRightCorner: Point): Figure() {

    private var width: Double
    private var height: Double
    override var center: Point = Point(0.0, 0.0)

    init {
        if (topLeftCorner.y <= bottomRightCorner.y || topLeftCorner.x >= bottomRightCorner.x) {
            throw FigureException("Top left corner X should be less than right bottom corner X, as well as top left corner Y should be greater than bottom right corner Y")
        }
        this.width = abs(topLeftCorner.x - bottomRightCorner.x)
        this.height = abs(topLeftCorner.y - bottomRightCorner.y)
        this.center = Point((this.topLeftCorner.x + this.bottomRightCorner.x) / 2, (this.topLeftCorner.y + this.bottomRightCorner.y) / 2)
    }

    override fun calculateArea(): Double {
        return width * height
    }

    override fun moveTo(point: Point) {
        this.center = point
        this.calculateNewPoints()
    }

    private fun calculateNewPoints(newPoints: ArrayList<Point>? = null) {
        if (newPoints == null) {
            this.topLeftCorner = Point(this.center.x - this.width / 2, this.center.y + this.height / 2)
            this.bottomRightCorner = Point(this.center.x + this.width / 2, this.center.y - this.height / 2)
        } else {
            var maxPoint: Point? = null
            var minPoint: Point? = null
            for (i in 0 until newPoints.size) {
                if (minPoint == null) {
                    minPoint = newPoints[i]
                } else if (minPoint.x < newPoints[i].x && minPoint.y > newPoints[i].y){
                    minPoint = newPoints[i]
                }

                if (maxPoint == null) {
                    maxPoint = newPoints[i]
                } else if (maxPoint.x > newPoints[i].x && maxPoint.y < newPoints[i].y){
                    maxPoint = newPoints[i]
                }
            }
            if (minPoint != null) {
                this.topLeftCorner = minPoint
            }
            if (maxPoint != null) {
                this.bottomRightCorner = maxPoint
            }
        }
    }

    override fun resize(zoom: Double) {
        val vectors = this.calculateVectors(this.center)
        for (vector in vectors) {
            vector.x *= zoom
            vector.y *= zoom
        }
        this.calculateNewPoints(vectors)
        this.width = abs(topLeftCorner.x - bottomRightCorner.x)
        this.height = abs(topLeftCorner.y - bottomRightCorner.y)
        this.calculateNewPoints()
    }

    private fun calculateVectors(rotationPoint: Point): ArrayList<Point> {
        val vectors = ArrayList<Point>()
        vectors.add(Point(this.topLeftCorner.x - rotationPoint.x, this.topLeftCorner.y - rotationPoint.y))
        vectors.add(Point(this.topLeftCorner.x + this.width - rotationPoint.x, this.topLeftCorner.y - rotationPoint.y))
        vectors.add(Point(this.bottomRightCorner.x - this.width - rotationPoint.x, this.bottomRightCorner.y - rotationPoint.y))
        vectors.add(Point(this.bottomRightCorner.x - rotationPoint.x, this.bottomRightCorner.y - rotationPoint.y))
        return vectors
    }

    override fun rotate(direction: RotateDirection, rotationPoint: Point) {
        val newPoints = ArrayList<Point>()
        if (direction == RotateDirection.Clockwise) {
            for (vector in this.calculateVectors(rotationPoint)) {
                vector.x = vector.y - rotationPoint.x
                vector.y = vector.x * -1 - rotationPoint.y
                newPoints.add(vector)
            }
        } else {
            for (vector in this.calculateVectors(rotationPoint)) {
                val bufX = vector.x
                val bufY = vector.y
                vector.x = bufY * -1 + rotationPoint.x
                vector.y = bufX + rotationPoint.y
                newPoints.add(vector)
            }
        }

        this.width = this.height.also { this.height = this.width }

        this.center = Point(
            (this.topLeftCorner.x + this.bottomRightCorner.x) / 2,
            (this.topLeftCorner.y + this.bottomRightCorner.y) / 2
        )
    }

    override fun toString(): String {
        return "Square with left top corner at ${this.topLeftCorner}, " +
                "right bottom corner at ${this.bottomRightCorner}, " +
                "width: ${this.width}, height: ${this.height}\n"
    }
}