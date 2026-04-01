package org.example

class PhysicalBook (
    title: String,
    author: String,
    publicationYear: Int,
    availableCopies: Int,
    var weight: Double,
    var hasHardCover: Boolean = true

) : Book(title, author, publicationYear) {

    // value é valor novo que alguem quer por 
    // field é o valor atual guardado dentro da variavel
    // field -> availableCopies
    // lado esquerdo: propriedade da classe | lado direito: parametro do construtor
    var availableCopies: Int = availableCopies
        set (value) {
            if(value >= 0) {
                field = value
            }
        }

    override fun getStorageInfo(): String {
        val cover = if(hasHardCover) "Yes" else "No"
        return "Livro físico: ${weight}g, Capa rija: $cover"
    }

    override fun toString(): String {
        return super.toString() + " | Cópias disponíveis: $availableCopies | ${getStorageInfo()}"
    }
}