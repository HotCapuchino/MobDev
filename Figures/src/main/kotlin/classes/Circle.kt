package classes

import interfaces.RotateDirection
import kotlin.math.pow

class Circle(override var center: Point, var radius: Double): Figure() {
    override fun calculateArea(): Double {
        return this.radius.pow(2.0) * Math.PI
    }

    override fun moveTo(point: Point) {
        this.center = point
    }

    override fun resize(zoom: Double) {
       this.radius *= zoom
    }

    override fun rotate(direction: RotateDirection, rotationPoint: Point) {
        if (direction == RotateDirection.Clockwise) {
            this.center.x = this.center.y - rotationPoint.y + rotationPoint.x.also {
                this.center.y = -1 * (this.center.x - rotationPoint.x) + rotationPoint.y
            }
        } else {
            this.center.x = -1 * (this.center.y - rotationPoint.y) + rotationPoint.x.also {
                this.center.y = this.center.y - rotationPoint.x + rotationPoint.y
            }
        }
    }

    override fun toString(): String {
        return "Circle with center at ${this.center}, with radius: ${this.radius}"
    }
}