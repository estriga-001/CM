package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    val events = listOf<Event> (
        Login ("alice", 1_000 ) ,
        Purchase ("alice", 49.99 , 1_100 ) ,
        Purchase ("bob", 19.99 , 1_200 ) ,
        Login ("bob", 1_050 ) ,
        Purchase ("alice", 15.00 , 1_300 ) ,
        Logout ("alice", 1_400 ) ,
        Logout ("bob", 1_500 )
    )

    // criamos esta lista para nao haver users duplicados
    val printedUsers = mutableListOf<String>()

    processEvents(events) { event ->

        if(event is Login){
            println("[LOGIN] ${event.username} logged in at t=${event.timeStamp}")
        }
        if(event is Purchase) {
            println("[PURCHASE] ${event.username} spent ${event.amount}$ at t=${event.timeStamp}")
        }
        if(event is Logout) {
            println("[LOGOUT] ${event.username} logged out at t=${event.timeStamp}")
        }
    }
    println()

    processEvents(events) { event ->
        if(event is Purchase) {
            if(event.username !in printedUsers) {
                println("Total spent by ${event.username}: ${events.totalSpent(event.username)}")
                printedUsers.add(event.username)
            }
        }
    }

    println()
    println("Events for Alice:")
    val eventosA = events.filterByUser("alice")
    for(event in eventosA) {
        when(event) {
            is Login -> println("\tLogin (username=${event.username}, timestamp=${event.timeStamp})")
            is Purchase -> println("\tPurchase (username=${event.username}, amount=${event.amount}, timestamp=${event.timeStamp})")
            is Logout -> println("\tLogout (username=${event.username}, timestamp=${event.timeStamp})")
        }
    }

}