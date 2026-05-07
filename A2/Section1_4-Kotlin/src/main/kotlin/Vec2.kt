package org.example

// usamos a implementação de comparable para o kotlin saber como ordenar os vbetores e resolver operadores de comp
// com comparable podemos associar <, >, <= e >= a função compareTo
data class Vec2(val x: Double, val y: Double): Comparable<Vec2> {

    /*operator fun component1(): Double = x
    operator fun component2(): Double = y*/ //assim da overload

    operator fun plus(other: Vec2): Vec2 {
        return Vec2(x + other.x, y + other.y)
    }

    operator fun minus(other: Vec2): Vec2 {
        return Vec2(x - other.x, y - other.y)
    }

    // função membro da classe vec2
    // o obj a esquerda (vec2) 
    operator fun times(scalar: Double): Vec2 {
        return Vec2(x * scalar, y * scalar)
    }

    operator fun unaryMinus(): Vec2 {
        return Vec2(-x, -y)
    }

    // compareTo retorna negativo se this < other
    // retorna 0 se são iguais
    // positivo se this > other
    // magnitude = sqrt(x^2 + y^2)
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


// aqui extendemos a classe Double para aceitar inputs do genero 2*vec2. ja que se usassemos
// o outro metodo com 2*vec2 nao iria funcionar, pois a ordem importa
operator fun Double.times(v: Vec2): Vec2 {
    return Vec2(this * v.x, this * v.y)
}
