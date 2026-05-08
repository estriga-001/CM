package org.example.app

import org.example.app.app.DataProcessorExtractor

/*
Esta aplicação demonstra a utilização de anotações personalizadas para
associar comportamento extra a classes e métodos.
A classe MyClass possui métodos anotados com @Greeting,
permitindo que um wrapper leia essas anotações e execute lógica adicional,
como apresentar mensagens antes da execução dos métodos.
Além disso, a classe abstrata DataProcessor usa a anotação @Extract para
indicar quais expressões regulares devem ser usadas para extrair dados
de uma string. No main, são criados objetos que executam estes métodos anotados e
processam uma string de entrada, extraindo o nome e o endereço de forma automática.
*/
fun main() {

    // Cria uma instância da classe MyClass, que contém métodos anotados com @Greeting
    val myClass = MyClass()

    // Envolve a classe MyClass num wrapper, permitindo executar lógica adicional
    // associada às anotações antes ou depois dos métodos originais
    val wrappedMyClass = MyClassWrapper(myClass)

    // Chama os métodos da classe através do wrapper
    // O wrapper pode ler as anotações @Greeting e apresentar mensagens associadas
    wrappedMyClass.sayHello()
    wrappedMyClass.compute()

    // String de entrada que contém dados organizados num formato textual simples
    val input = "Name: John Address: 123 Street"

    // Cria um extractor responsável por processar a string de entrada
    // e extrair informações específicas usando expressões regulares
    val extractor = DataProcessorExtractor(input)

    // Obtém e apresenta o nome extraído da string
    println("Name: ${extractor.getName()}")

    // Obtém e apresenta o endereço extraído da string
    println("Address: ${extractor.getAddress()}")
}