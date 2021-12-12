package classes

import interfaces.Movable
import interfaces.Transformable

abstract class Figure: Movable, Transformable {
    abstract fun calculateArea(): Double
    abstract val center: Point
}

class FigureException(override val message: String) : Exception(message) {
    override fun toString(): String {
        return "Oops! An Error occurred: "+ super.toString()
    }
}