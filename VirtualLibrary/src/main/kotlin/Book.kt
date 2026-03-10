package org.example

open class Book (
    val title: String,
    val author: String,
    val publicationYear: Int,
) {

    open fun getPublicationCategory(): String {
        return when{
            publicationYear < 1980 -> "Clássico"
            publicationYear <= 2010 -> "Moderno"
            else -> "Contemporâneo"
        }
    }

    override fun toString(): String {
        return "Título: $title | Autor: $author | Ano de publicação: $publicationYear - ${getPublicationCategory()}"
    }
}