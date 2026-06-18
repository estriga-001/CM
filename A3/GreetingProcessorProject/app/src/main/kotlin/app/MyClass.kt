package org.example.app

import org.example.annotations.Greeting // para usar o annotation class greeting

/*
as classes por default em kotlin sao fechadas (nao podem ser herdadas)
assim, open MyClass pode ser herdada por outra classe, ou que codigo
gerado/wrappers possam estender o seu comportamento
 */

open class MyClass {

    // A anotação @Greeting associa uma mensagem a este método.
    // Essa mensagem pode ser lida mais tarde por reflexão ou por código gerado
    @Greeting("Hello from MyClass!")
    open fun sayHello() { // open para, se quisermos fazer override da função
        println("Executing sayHello method")
    }

    // se chamar diretamente myClass.sayHello() nao vai aparecer a mensagem
    // a mensagem aparece se a função é chamada atraves do wrapper ou de codigo gerado pelo processor

    // Método também anotado com @Greeting.
    // O objetivo é permitir que o wrapper detete esta anotação
    // e execute algum comportamento adicional
    @Greeting("Hello to the compute function!")
    open fun compute() {
        println("Computing something important...")
    }
}