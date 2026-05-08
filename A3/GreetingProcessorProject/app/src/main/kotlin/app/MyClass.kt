package org.example.app

import org.example.annotations.Greeting

open class MyClass {

    // A anotação @Greeting associa uma mensagem a este método.
    // Essa mensagem pode ser lida mais tarde por reflexão ou por código gerado
    @Greeting("Hello from MyClass!")
    open fun sayHello() {
        println("Executing sayHello method")
    }

    // Método também anotado com @Greeting.
    // O objetivo é permitir que o wrapper detete esta anotação
    // e execute algum comportamento adicional
    @Greeting("Hello to the compute function!")
    open fun compute() {
        println("Computing something important...")
    }
}