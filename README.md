# CM_TP1

# Tarefa 1 – Kotlin Tutorial Exercises

Este exercício reúne a resolução de três exercícios desenvolvidos em **Kotlin**, com base no enunciado do **Tutorial 1** da unidade curricular. A tarefa foi pensada para consolidar os primeiros conceitos da linguagem, explorando diferentes formas de resolver problemas simples com recurso a estruturas próprias de Kotlin, como arrays, ranges, funções de ordem superior, geração de sequências e tratamento de exceções.

Ao longo dos três exercícios, o projeto demonstra uma abordagem progressiva à aprendizagem da linguagem. O primeiro exercício foca-se na criação e manipulação de coleções numéricas, o segundo introduz interação com o utilizador e controlo de erros através de uma calculadora em linha de comandos, e o terceiro explora a geração de sequências matemáticas com base numa regra de recorrência.

## Estrutura da tarefa

A tarefa está organizado em três ficheiros Kotlin independentes, cada um correspondente a um exercício específico:

```text
Tarefa1
│
├── exer_1.kt
├── exer_2.kt
└── exer_3.kt

# Hello World V2

## Descrição

A aplicação **Hello World V2** é um projeto Android desenvolvido em **Kotlin** com o objetivo de explorar a estrutura base de uma aplicação nativa, a utilização de layouts em XML e a adaptação da interface a diferentes orientações do dispositivo. Apesar de simples, a app já demonstra vários conceitos importantes do desenvolvimento Android, como o ciclo de vida de uma `Activity`, o uso de `ConstraintLayout`, a organização de recursos em ficheiros próprios e a criação de interfaces responsivas para modo vertical e horizontal.

O projeto apresenta um ecrã principal com uma mensagem de boas-vindas, um subtítulo, uma imagem e, no modo vertical, um calendário. Para além disso, utiliza temas e cores personalizados, bem como strings centralizadas em ficheiros de recursos, seguindo as boas práticas do ecossistema Android.

---

## Objetivo

O principal objetivo desta aplicação é servir como introdução prática ao desenvolvimento Android em Kotlin. Através dela, é possível compreender como se organiza uma aplicação Android, como se define a interface gráfica com XML e como se ligam os recursos visuais ao código Kotlin de uma `Activity`.

Ao mesmo tempo, o projeto permite praticar conceitos como:

- criação de uma `Activity`
- utilização de layouts XML
- posicionamento de componentes com `ConstraintLayout`
- separação de recursos em `strings.xml`, `colors.xml` e `themes.xml`
- adaptação da interface a diferentes orientações do ecrã
- utilização de componentes gráficos básicos como `TextView`, `ImageView` e `CalendarView`

---

## Estrutura principal da aplicação

A aplicação é composta por vários ficheiros responsáveis por diferentes partes do projeto:

```text
app/
└── src/
    └── main/
        ├── java/cm_a15044/helloworld/
        │   └── MainActivity.kt
        ├── res/
        │   ├── layout/
        │   │   └── activity_main.xml
        │   ├── layout-land/
        │   │   └── activity_main.xml
        │   ├── values/
        │   │   ├── strings.xml
        │   │   ├── colors.xml
        │   │   └── themes.xml
        │   └── drawable/
        │       └── smile

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

# Virtual Library

A tarefa está dividido nas seguintes classes:

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
