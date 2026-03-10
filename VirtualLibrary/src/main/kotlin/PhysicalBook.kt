package org.example

class PhysicalBook (
    title: String,
    author: String,
    publicationYear: Int,
    availableCopies: Int,
    var weight: Double,
    var hasHardCover: Boolean = true

) : Book(title, author, publicationYear) {

    var availableCopies: Int = availableCopies
        set (value) {
            if(value >= 0) {
                field = value
                if (value == 0) {
                    println("Atenção: o livro está agora fora de stock.")
                }
            }
        }

    override fun toString(): String {
        return super.toString() + " | Cópias disponíveis: $availableCopies | Peso: $weight | Capa rija: $hasHardCover"
    }
}