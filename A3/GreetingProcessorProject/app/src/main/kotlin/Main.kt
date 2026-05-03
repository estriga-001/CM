package org.example.app

import org.example.app.app.DataProcessorExtractor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val myClass = MyClass()
    val wrappedMyClass = MyClassWrapper(myClass)

    wrappedMyClass.sayHello()
    wrappedMyClass.compute()

    val input = "Name: John Address: 123 Street"

    val extractor = DataProcessorExtractor(input)
    println("Name: ${extractor.getName()}")
    println("Address: ${extractor.getAddress()}")
}