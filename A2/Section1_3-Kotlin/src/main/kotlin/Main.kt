package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val logs = listOf(
        " INFO : server started ",
        " ERROR : disk full ",
        " DEBUG : checking config ",
        " ERROR : out of memory ",
        " INFO : request received ",
        " ERROR : connection timeout "
    )

    val pipeline = buildPipeline {
        addStage("Trim") { lines ->
            lines.map { it.trim() }
        }

        addStage("Filter errors") { lines ->
            lines.filter { "ERROR" in it }
        }

        addStage("Uppercase") { lines ->
            lines.map { it.uppercase() }
        }

        addStage("Add index") { lines ->
            lines.mapIndexed { index, line -> "${index + 1}. $line" }
        }
    }

    pipeline.describe()

    val result = pipeline.execute(logs)

    println("Result:")
    result.forEach { println(it) }

    // -------------------------
    // Exemplo extra do challenge
    // -------------------------

    val pipeline1 = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
        addStage("Only errors") { lines -> lines.filter { "ERROR" in it } }
    }

    val pipeline2 = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
        addStage("Uppercase") { lines -> lines.map { it.uppercase() } }
    }

    val (result1, result2) = pipeline1.fork(logs, pipeline2)

    println("\nFork result 1:")
    result1.forEach { println(it) }

    println("\nFork result 2:")
    result2.forEach { println(it) }
}