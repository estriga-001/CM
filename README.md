# CM_TP1

# Tarefa 1 – Kotlin Tutorial Exercises

Este exercício reúne a resolução de três exercícios desenvolvidos em **Kotlin**, com base no enunciado do **Tutorial 1** da unidade curricular. A tarefa foi pensada para consolidar os primeiros conceitos da linguagem, explorando diferentes formas de resolver problemas simples com recurso a estruturas próprias de Kotlin, como arrays, ranges, funções de ordem superior, geração de sequências e tratamento de exceções.

Ao longo dos três exercícios, o projeto demonstra uma abordagem progressiva à aprendizagem da linguagem. O primeiro exercício foca-se na criação e manipulação de coleções numéricas, o segundo introduz interação com o utilizador e controlo de erros através de uma calculadora em linha de comandos, e o terceiro explora a geração de sequências matemáticas com base numa regra de recorrência.

## Estrutura do projeto

O projeto está organizado em três ficheiros Kotlin independentes, cada um correspondente a um exercício específico:

```text
Tarefa1
│
├── exer_1.kt
├── exer_2.kt
└── exer_3.kt

# Library Management System in Kotlin

## Objetivo

Este programa foi desenvolvido para praticar conceitos fundamentais de **Programação Orientada a Objetos (POO)** em Kotlin, incluindo:

- classes e objetos
- herança
- classes abstratas
- `data class`
- `companion object`
- encapsulamento
- polimorfismo
- sobrescrita de métodos (`override`)

---

## Estrutura do Projeto

O projeto está dividido nas seguintes classes:

### `Book.kt`
Classe abstrata que representa um livro genérico.

#### Atributos:
- `title: String`
- `author: String`
- `publicationYear: Int`

#### Métodos:
- `getPublicationCategory()`  
  Classifica o livro como:
  - **Clássico** → antes de 1980
  - **Moderno** → entre 1980 e 2010
  - **Contemporâneo** → depois de 2010

- `getStorageInfo()`  
  Método abstrato implementado nas subclasses.

- `toString()`  
  Devolve uma descrição textual do livro.

---

### `PhysicalBook.kt`
Classe que herda de `Book` e representa um livro físico.

#### Atributos adicionais:
- `availableCopies: Int`
- `weight: Double`
- `hasHardCover: Boolean`

#### Funcionalidades:
- controla o número de cópias disponíveis
- impede que o número de cópias fique negativo
- informa se o livro tem capa rija

---

### `DigitalBook.kt`
Classe que herda de `Book` e representa um livro digital.

#### Atributos adicionais:
- `fileSize: Double`
- `format: String`

#### Funcionalidades:
- guarda o tamanho do ficheiro
- guarda o formato do livro digital (ex: PDF)

---

### `Library.kt`
Classe principal responsável por gerir a biblioteca.

#### Atributos:
- `name: String`
- `books: MutableList<Book>` (privado)

#### Funcionalidades:
- adicionar livros
- mostrar todos os livros
- emprestar livros
- devolver livros
- pesquisar livros por autor

#### `companion object`
Mantém um contador global do total de livros criados:
- `getTotalBooksCreated()`

---

### `LibraryMember.kt`
`data class` que representa um membro da biblioteca.

#### Atributos:
- `name: String`
- `membershipId: Int`
- `borrowedBooks: MutableList<String>`

> Nota: nesta versão do projeto, a classe existe mas ainda não está integrada na lógica principal dos empréstimos.

---

### `Main.kt`
Ficheiro principal onde o programa é executado.

#### O que faz:
- cria uma biblioteca
- cria livros físicos e digitais
- adiciona os livros à biblioteca
- mostra o catálogo
- simula empréstimos
- simula devoluções
- faz pesquisa por autor

---

## Funcionalidades Implementadas

- Registo de livros físicos e digitais
- Classificação automática por época de publicação
- Listagem de catálogo
- Empréstimo de livros físicos
- Verificação de stock
- Devolução de livros físicos
- Pesquisa de livros por autor
- Contador total de livros adicionados

---
