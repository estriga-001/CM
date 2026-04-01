package org.example

import sun.security.util.resources.auth

abstract class Book (
    val title: String,
    val author: String,
    val publicationYear: Int,
) {

    // quando digitalBook ou physical book é instanciado esta mensagem é mostrada
    init {
        println("Livro $title de $author registado.")
    }

    fun getPublicationCategory(): String {
        return when{
            publicationYear < 1980 -> "Clássico"
            publicationYear <= 2010 -> "Moderno"
            else -> "Contemporâneo"
        }
    }

    abstract fun getStorageInfo(): String

    override fun toString(): String {
        return "Título: $title | Autor: $author | Ano de publicação: $publicationYear - ${getPublicationCategory()}"
    }
}