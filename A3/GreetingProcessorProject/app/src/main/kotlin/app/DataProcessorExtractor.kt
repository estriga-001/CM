package org.example.app.app
import org.example.annotations.Extract

// A anotação @Extract define a expressão regular usada para procurar o nome.
// Neste caso, procura texto no formato "Name: valor"
abstract class DataProcessor(val input: String) {
    // (\w+) procura um caracter de palavra: letras, numeros ou _
    @Extract(regex = "Name: (\\w+)")
    abstract fun getName(): String ?

    // Esta anotação define a expressão regular usada para procurar o endereço.
    // O padrão "Address: (.+)" captura tudo o que aparece depois de "Address:"
    @Extract(regex = "Address: (.+)")
    abstract fun getAddress(): String ?
}