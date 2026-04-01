package org.example

sealed class Event

class Login(val username: String, val timeStamp: Long) : Event()

class Purchase(val username: String, val amount: Double, val timeStamp: Long) : Event()

class Logout(val username: String, val timeStamp: Long) : Event()

// Função lista de objetos do tipo Event
// assim é uma extension function
fun List<Event>.filterByUser(username: String) : List<Event> {
    val result = mutableListOf<Event>() // criamos uma lista de resultados de Objetos Event

    for(event in this) {
        if(event is Login) { // se o evento for login
            if(event.username == username) { // verifica se o username esta a associado
                result.add(event) // se sim, adiciona aos resultados
            }
        }

        if (event is Purchase) {
            if(event.username == username) {
                result.add(event)
            }
        }

        if(event is Logout) {
            if(event.username == username) {
                result.add(event)
            }
        }
    }

    return result // retornamos uma List<Event>
}

fun List<Event>.totalSpent( username: String): Double {
    val compras = this.filterIsInstance<Purchase>()
    val userCompras = mutableListOf<Double>()

    for (compra in compras) {
        if(compra.username == username) {
            userCompras.add(compra.amount)
        }
    }

    //o it é criado automaticamente pelo kotlin, nao é preciso declarar, e é basicamente
    // x -> x, ou seja, sumOf precisa de uma função lambda
    // essa função recebe um parametro e como eu n dei nome nenhum
    // o kotlin chama-a de it
    return userCompras.sumOf{it}
}

// recebe outra função como argumento e executa essa função
fun processEvents(events: List<Event>, handler: (Event) -> Unit) {
    // neste caso, para cada evento, chama a função handler
    for (event in events) {
        handler(event)
    }
}