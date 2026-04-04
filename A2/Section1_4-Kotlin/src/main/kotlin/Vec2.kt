package org.example

data class Vec2(val x: Double, val y: Double): Comparable<Vec2> {

    /*operator fun component1(): Double = x
    operator fun component2(): Double = y*/ //assim da overload

    operator fun plus(other: Vec2): Vec2 {
        return Vec2(x + other.x, y + other.y)
    }

    operator fun minus(other: Vec2): Vec2 {
        return Vec2(x - other.x, y - other.y)
    }

    operator fun times(scalar: Double): Vec2 {
        return Vec2(x * scalar, y * scalar)
    }

    operator fun unaryMinus(): Vec2 {
        return Vec2(-x, -y)
    }

    // compareTo retorna negativo se this < other
    // retorna 0 se são iguais
    // positivo se this > other
    override operator fun compareTo(other: Vec2): Int {
        return this.magnitude().compareTo(other.magnitude())
    }

    fun magnitude(): Double {
        return kotlin.math.sqrt(x*x + y*y)
    }

    fun dot(other: Vec2): Double {
        return x*other.x + y*other.y
    }

    fun normalized(): Vec2 {
        val mag = magnitude()

        if (mag == 0.0) {
            throw IllegalStateException("A distancia euclidiana é 0.")
        }

        return Vec2(x / mag, y / mag)
    }

    operator fun get(index: Int): Double {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("O índice tem de ser 0 ou 1.")
        }
    }

    // fazer override do times
    // temos 'V * a' e queremos 'a * V' por isso temos de "ensinar" Double a fazer isso
}

operator fun Double.times(v: Vec2): Vec2 {
    return Vec2(this * v.x, this * v.y)
}
