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
/*DataProcessor é uma classe abstrata, ou seja, serve como uma classe base e 
não pode ser instanciada diretamente. Ela define uma estrutura que outras classes devem seguir. 
Neste caso, recebe no construtor um input: String, que representa o texto de entrada de onde 
queremos extrair dados. O val significa que esse valor fica guardado 
como uma propriedade imutável da classe. Faz sentido esta classe ser abstrata porque 
ela apenas define os métodos que devem existir, como getName() e getAddress(), 
mas a implementação concreta desses métodos fica numa classe gerada ou numa subclasse, 
como o DataProcessorExtractor. */