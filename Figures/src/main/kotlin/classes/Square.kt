package classes

import interfaces.RotateDirection
import kotlin.math.pow

class Square(var topLeftCorner: Point, val length: Double): Figure() {

    var dimension: Double
    override var center: Point = Point(0.0, 0.0)

    init {
        if (length <= 0) {
            throw FigureException("Length of the square side should be greater than zero")
        }
        this.dimension = length
        this.center = Point(this.topLeftCorner.x + (dimension / 2), this.topLeftCorner.y - (dimension / 2))
    }

    override fun calculateArea(): Double {
        return this.dimension * this.dimension
    }

    override fun moveTo(point: Point) {
        this.center = point
        this.calculateNewPoint()
    }

    private fun calculateNewPoint() {
        this.topLeftCorner = Point(this.center.x - this.dimension / 2, this.center.y + this.dimension / 2)
    }

    override fun resize(zoom: Double) {
        this.dimension *= zoom
        this.calculateNewPoint()
    }

    override fun rotate(direction: RotateDirection, rotationPoint: Point) {
        if(direction == RotateDirection.Clockwise) {
            this.topLeftCorner.x = this.topLeftCorner.y - rotationPoint.y + rotationPoint.x
        } else {
            this.topLeftCorner.x = -1 * (this.topLeftCorner.y - rotationPoint.y) + rotationPoint.x
        }
        this.center = Point(this.topLeftCorner.x + (dimension / 2), this.topLeftCorner.y - (dimension / 2))
    }

    override fun toString(): String {
        return "Square with center at ${this.center}, " +
                "top left corner at ${this.topLeftCorner}), " +
                "and dimension is ${this.dimension}"
    }
}