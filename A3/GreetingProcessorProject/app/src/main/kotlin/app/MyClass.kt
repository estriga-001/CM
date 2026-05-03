package org.example.app

import org.example.annotations.Greeting

open class MyClass {
    @Greeting("Hello from MyClass!")
    open fun sayHello() {
        println("Executing sayHello method")
    }

    @Greeting("Hello to the compute function!")
    open fun compute() {
        println("Computing something important...")
    }
}