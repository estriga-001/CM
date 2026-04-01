package org.example.cm.exer_3

fun main(){
    // altura inicial = 100
    val h0 = 100.0
    val hn = generateSequence(h0) {it* 0.6}

    // numeros maior ou igual que 1
    // até 15 no max
    // map transforma cada numero (Double) em string tipo 100.0 -> 100.00
    // toList transforma numa lista
    print(hn.takeWhile{it >= 1.0}.take(15).map{"%.2f".format(it)}.toList())
}