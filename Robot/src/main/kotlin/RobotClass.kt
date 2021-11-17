enum class Direction(var dir: Int) {
    UP(1),
    LEFT(2),
    DOWN(3),
    RIGHT(4);

    companion object {
        fun fromInt(intVal: Int) = Direction.values().first {it.dir == intVal}
    }
}

class Robot(private var direction: Direction = Direction.UP, private var position: Position = Position(0, 0)) {

    fun getDirection(): Direction {
        return this.direction
    }

    fun printDirection() {
        println("Robot direction is " + this.direction.toString())
    }

    fun getPosition(): Position {
        return this.position
    }

    fun printPosition() {
        println("Robot position is: x = " + this.position.x + ", y = " + this.position.y)
    }

    fun turnLeft() {
        this.turn("left")
    }

    fun turnRight() {
        this.turn("right")
    }

    private fun turn(dir: String) {
        var currentDirection = this.direction.dir
        if (dir == "left") {
            if (++currentDirection > 4) {
                println("caught 1")
                this.direction = Direction.fromInt(1)
                return
            }
        } else {
            if (--currentDirection < 1) {
                println("caught 4")
                this.direction = Direction.fromInt(4)
                return
            }
        }
        this.direction = Direction.fromInt(currentDirection)
    }

    fun moveTo(targetPosition: Position) {
        if (targetPosition.x == this.position.x && targetPosition.y == this.position.y) {
            println("You're already in required position")
            return
        }
        val targetXDirection: Direction = if (targetPosition.x > this.position.x) Direction.RIGHT else Direction.LEFT
        val targetYDirection: Direction = if (targetPosition.y > this.position.y) Direction.UP else Direction.DOWN
        while(this.direction != targetXDirection) {
            this.turnLeft()
        }
        while(this.position.x != targetPosition.x) {
            if (targetXDirection == Direction.RIGHT) {
                this.position.x++
            } else {
                this.position.x--
            }
        }
        while(this.direction != targetYDirection) {
            this.turnRight()
        }
        while(this.position.y != targetPosition.y) {
            if (targetYDirection == Direction.UP) {
                this.position.y++
            } else {
                this.position.y--
            }
        }
    }
}