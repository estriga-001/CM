package org.example

class Pipeline {
    // Guarda os stages pela ordem em que foram adicionados
    private val stages = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(name to transform)
    }

    fun execute(input: List<String>): List<String> {
        var result = input
        for ((_, transform) in stages) {
            result = transform(result)
        }
        return result
    }

    fun describe() {
        println("Pipeline stages:")
        stages.forEachIndexed { index, stage ->
            println("${index + 1}. ${stage.first}")
        }
    }

    // Challenge: compõe dois stages existentes num novo
    fun compose(firstStageName: String, secondStageName: String, newStageName: String) {
        val first = stages.find { it.first == firstStageName }?.second
        val second = stages.find { it.first == secondStageName }?.second

        if (first == null || second == null) {
            println("Não foi possível compor os stages: nomes inválidos.")
            return
        }

        val composed: (List<String>) -> List<String> = { input ->
            second(first(input))
        }

        stages.add(newStageName to composed)
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
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    val pipeline = Pipeline()
    pipeline.block()
    return pipeline
}