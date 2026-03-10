package org.example

class Library (
    var name: String
) {

    private val books = mutableListOf<Book>()

    fun addBook(book: Book) {
        books.add(book)
    }

    fun listBooks() {
        if (books.isEmpty()) {
            println("Não existem livros registados!")
            return
        }

        for(book in books) {
            println(book)
        }
    }

    fun borrowBook(title: String) {
        var found = false

        for (book in books) {

            if (book.title.equals(title, ignoreCase = true)) {
                found = true
                println("Livro ${book.title} encontrado com sucesso!")

                if(book is PhysicalBook) {
                    if(book.availableCopies > 0) {
                        book.availableCopies--
                        println("Livro ${book.title} emprestado com sucesso.")
                    }
                    else {
                        println("Não há cópias disponíveis do livro ${book.title}.")
                    }
                }
                else if (book is DigitalBook) {
                    println("\nO livro ${book.title} é digital. Emprestado com sucesso.")
                }
                return
            }
        }
        if(!found) {
            println("Nenhum livro com esse título foi encontrado.")
        }
    }

    fun returnBook(title: String) {
        for (book in books) {
            if(book.title.equals(title, ignoreCase = true)) {
                if (book is PhysicalBook) {
                    book.availableCopies++
                    println("Livro ${book.title} devolvido com sucesso.")
                } else {
                    println("O livro ${book.title} é digital.")
                }

                return
            }
        }
        println("Livro não encontrado.")
    }
}