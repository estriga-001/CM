package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val library = Library("Central Library")

    val digitalBook = DigitalBook (
        "Kotlin in Action",
        "Dmitry Jemerov",
        2017,
        4.5,
        "PDF"
    )

    library.addBook(digitalBook)
    library.listBooks()
    library.borrowBook("Kotlin in action")
    library.borrowBook("aaa")
}