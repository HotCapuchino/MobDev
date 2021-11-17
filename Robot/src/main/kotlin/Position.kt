class Position(x: Int = 0, y: Int = 0) {
    var x: Int
    var y: Int

    init {
        this.x = if (x >= 0) x else 0
        this.y = if (y >= 0) y else 0
    }
}