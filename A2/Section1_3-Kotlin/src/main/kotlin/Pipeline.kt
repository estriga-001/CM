package org.example

class Pipeline {
    // Guarda os stages pela ordem em que foram adicionados
    // Estrutura de dados que guarda um par, String e função que recebe Lista de strings e devolve uma lista de strings
    private val stages = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(name to transform)
        // em vez disto podia ter escrito
        // stages.add(Pair(name, transform))
    }

    fun execute(input: List<String>): List<String> {
        var result = input // pegamos na lista original

        // destructuring declaration, desmonta o objeto em partes (como neste caso usamos pair)
        // o _ significa que ignoramos o primeiro parametro do pair e assim nao guardamos a primeira parte e usamos so a segunda
        // transform (que é a função lambda do addStage) é so o nome da variavel que damos ao segundo parametro do pair
        for ((_, transform) in stages) {
            result = transform(result) // modificamos o result com o transform atual
        }
        return result
    }

    // forEachIndexed é uma variação do forEach mas dá-nos o indice em que vai
    fun describe() {
        println("Pipeline stages:")
        stages.forEachIndexed { index, stage ->
            println("${index + 1}. ${stage.first}")
        }
    }

    // Challenge: compõe dois stages existentes num novo
    fun compose(firstStageName: String, secondStageName: String, newStageName: String) {
        // val first é uma função lambda
        // it.first é outro e nao esta relacionado com val first. o it.first refere-se ao nome do estagio guardado na lista
        // it.first -> Pair("First", Second)
        // se a procura falhar, como temos '?.' devolve null
        // se suceder o find devolve o objeto Pair
        val first = stages.find { it.first == firstStageName }?.second
        val second = stages.find { it.first == secondStageName }?.second

        // se o find falhar num deles, dá erro
        if (first == null || second == null) {
            println("Não foi possível compor os stages: nomes inválidos.")
            return
        }

        // composed é a variavel retornada apos uma sequencia de uma função lambda que
        // é do tipo List<String> e devolve List<String>. 
        // Recebe input como parametro
        val composed: (List<String>) -> List<String> = { input ->

            second(first(input)) // basicamente permite-nos criar um 'super estagio' dentro do pipeline original

            // o first executa as funções com o input, gerando uma nova lista como resultado
            // second recebe uma lista e aplica a sua propria transformação
        }

        // ou
        // stages.add(Pair(newStagename, composed))
        stages.add(newStageName to composed) // adiciona o novo super estagio ao pipeline
    }

    // Challenge: corre o mesmo input em dois pipelines independentes
    fun fork(
        input: List<String>,
        other: Pipeline
    ): Pair<List<String>, List<String>> {
        return Pair(this.execute(input), other.execute(input))
    }
}

// Função top-level com lambda receiver
// pipeline.() estamos a dizer que todos os metodos adicionados no main na pipeline vao ser function extensions de pipeline
// o unit é um void, a função que puser la dentro n vai devolver nada de especial, so vai executar algo
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    val pipeline = Pipeline()
    pipeline.block()
    return pipeline
}