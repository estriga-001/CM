package org.example

class DigitalBook (
    title: String,
    author: String,
    publicationYear: Int,
    val fileSize: Double,
    val format: String

) : Book(title, author, publicationYear){

    override fun toString(): String {
        return super.toString() + " | Tamanho: $fileSize MB | Formato: $format"
    }
}