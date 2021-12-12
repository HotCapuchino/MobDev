import classes.Circle
import classes.Point
import classes.Rect
import classes.Square
import interfaces.RotateDirection

fun main() {
    val circle = Circle(Point(5.0, 5.0),  3.0)
    println(circle)
    circle.rotate(RotateDirection.Clockwise, Point(7.0, 6.0))
    println(circle)
    circle.resize(2.0)
    println(circle)

    val rect = Rect(Point(2.0, 5.0), Point(3.0, 3.0))
    print(rect)
    rect.rotate(RotateDirection.CounterClockwise, Point(4.0, 6.0))
    print(rect)
    rect.resize(2.0)
    print(rect)

    val square= Square(Point(4.0, 4.0), 2.0)
    println(square)
    square.rotate(RotateDirection.Clockwise, Point(6.0, 6.0))
    println(square)
    square.resize(2.0)
    print(square)
}