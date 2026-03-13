---

# Computação Móvel - 15044

## Índice

* [Assignment 1 - Kotlin](https://www.google.com/search?q=%23assignment-1---kotlin)
* [Tarefa 1 – Kotlin Tutorial Exercises](https://www.google.com/search?q=%23tarefa-1--kotlin-tutorial-exercises)
* [Hello World V2](https://www.google.com/search?q=%23hello-world-v2)
* [Library Management System in Kotlin](https://www.google.com/search?q=%23library-management-system-in-kotlin)
* [Hello World Optional](https://www.google.com/search?q=%23hello-world-optional)



---

## Assignment 1 - Kotlin

Este bloco reúne os primeiros projetos desenvolvidos na unidade curricular, com foco na aprendizagem da linguagem Kotlin e nos fundamentos do desenvolvimento nativo para Android.

### Tarefa 1 – Kotlin Tutorial Exercises

Este exercício reúne a resolução de três exercícios desenvolvidos em **Kotlin**, com base no enunciado do **Tutorial 1** da unidade curricular. A tarefa foi pensada para consolidar os primeiros conceitos da linguagem, explorando diferentes formas de resolver problemas simples com recurso a estruturas próprias de Kotlin, como arrays, ranges, funções de ordem superior, geração de sequências e tratamento de exceções.

Ao longo dos três exercícios, o projeto demonstra uma abordagem progressiva à aprendizagem da linguagem. O primeiro exercício foca-se na criação e manipulação de coleções numéricas, o segundo introduz interação com o utilizador e controlo de erros através de uma calculadora em linha de comandos, e o terceiro explora a geração de sequências matemáticas com base numa regra de recorrência.

#### Estrutura da tarefa

A tarefa está organizada em três ficheiros Kotlin independentes, cada um correspondente a um exercício específico:

```text

```

Tarefa1
│
├── exer_1.kt
├── exer_2.kt
└── exer_3.kt

---

### Hello World V2

#### Descrição

A aplicação **Hello World V2** é um projeto Android desenvolvido em **Kotlin** com o objetivo de explorar a estrutura base de uma aplicação nativa, a utilização de layouts em XML e a adaptação da interface a diferentes orientações do dispositivo. Apesar de simples, a app já demonstra vários conceitos importantes do desenvolvimento Android, como o ciclo de vida de uma Activity, o uso de `ConstraintLayout`, a organização de recursos em ficheiros próprios e a criação de interfaces responsivas para modo vertical e horizontal.

O projeto apresenta um ecrã principal com uma mensagem de boas-vindas, um subtítulo, uma imagem e, no modo vertical, um calendário. Para além disso, utiliza temas e cores personalizados, bem como strings centralizadas em ficheiros de recursos, seguindo as boas práticas do ecossistema Android.

#### Objetivo

O principal objetivo desta aplicação é servir como introdução prática ao desenvolvimento Android em Kotlin. Através dela, é possível compreender como se organiza uma aplicação Android, como se define a interface gráfica com XML e como se ligam os recursos visuais ao código Kotlin de uma `Activity`.

O projeto permite praticar conceitos como:

* Criação de uma `Activity`
* Utilização de layouts XML
* Posicionamento de componentes com `ConstraintLayout`
* Separação de recursos em `strings.xml`, `colors.xml` e `themes.xml`
* Adaptação da interface a diferentes orientações do ecrã
* Utilização de componentes gráficos básicos como `TextView`, `ImageView` e `CalendarView`

#### Estrutura principal da aplicação

A aplicação é composta por vários ficheiros responsáveis por diferentes partes do projeto:

```text

```

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

#### Main Activity

A `MainActivity` é a atividade principal da aplicação e representa o ponto de entrada do projeto. Esta classe herda de `AppCompatActivity`, beneficiando das funcionalidades modernas da biblioteca AndroidX.

No método `onCreate`, a atividade é inicializada e o layout principal é carregado com `setContentView(R.layout.activity_main)`. Antes disso, é chamado `enableEdgeToEdge()`, permitindo que a aplicação utilize toda a área disponível do ecrã, incluindo as zonas próximas das barras do sistema. Além disso, é definido um listener com `ViewCompat.setOnApplyWindowInsetsListener`, que ajusta automaticamente o padding do contentor principal em função das barras do sistema.

#### Interface gráfica em modo vertical

O ficheiro `activity_main.xml`, localizado na pasta `layout`, define a interface utilizada quando o dispositivo está em orientação vertical. O layout base escolhido foi `ConstraintLayout`. São apresentados quatro componentes principais: dois `TextView`, uma `ImageView` e uma `CalendarView`. O primeiro `TextView` funciona como título principal da aplicação, seguido de um subtítulo. Abaixo surge uma `ImageView` e, na parte inferior, uma `CalendarView` que adiciona um elemento interativo ao ecrã.

#### Interface gráfica em modo horizontal

Para suportar a orientação horizontal, foi criado um segundo layout com o mesmo nome na pasta `layout-land`. Nesta versão, a disposição dos elementos é diferente: a `ImageView` passa a ocupar a zona esquerda do ecrã, enquanto os dois `TextView` são apresentados no lado direito. O `CalendarView` não está presente no modo horizontal para manter a interface equilibrada, demonstrando uma preocupação com design responsivo.

#### Recursos (Texto, Cor e Tema)

* **Texto:** Todos os textos estão definidos em `strings.xml` (ex: `app_name`, `hello_string`, `my_first_app`, `image_desc`).
* **Cor:** As cores estão definidas em `colors.xml`, incluindo o roxo principal (`purple_500`) e um tom laranja.
* **Tema:** O aspeto global é controlado através de `themes.xml`, utilizando como base o tema `Theme.MaterialComponents.DayNight.DarkActionBar`.

---

### Library Management System in Kotlin

#### Objetivo

Este programa foi desenvolvido para praticar conceitos fundamentais de **Programação Orientada a Objetos (POO)** em Kotlin, incluindo:

* Classes e objetos
* Herança e classes abstratas
* `data class` e `companion object`
* Encapsulamento e polimorfismo
* Sobrescrita de métodos (`override`)

#### Estrutura do Projeto (Virtual Library)

O projeto está dividido nas seguintes classes principais:

* **Book.kt**: Classe abstrata que representa um livro genérico.
* *Atributos:* `title`, `author`, `publicationYear`.
* *Métodos:* `getPublicationCategory()` (Clássico, Moderno, Contemporâneo), `getStorageInfo()` (abstrato) e `toString()`.


* **PhysicalBook.kt**: Herda de `Book` e representa um livro físico. Adiciona controlo de cópias disponíveis, peso e indicação de capa rija.
* **DigitalBook.kt**: Herda de `Book` e representa um livro digital. Guarda o tamanho do ficheiro e o formato (ex: PDF).
* **Library.kt**: Classe principal responsável por gerir a biblioteca. Permite adicionar, emprestar, devolver e pesquisar livros. Possui um `companion object` com o contador global `getTotalBooksCreated()`.
* **LibraryMember.kt**: `data class` que representa um membro da biblioteca (preparada para futura integração de empréstimos).
* **Main.kt**: Ficheiro principal onde o programa é executado e testado.

#### Funcionalidades Implementadas

* Registo de livros físicos e digitais.
* Classificação automática por época de publicação.
* Listagem de catálogo.
* Empréstimo e devolução de livros físicos com verificação de stock.
* Pesquisa de livros por autor.

---

### Hello World Optional

#### Descrição

A aplicação **Hello World Optional** é um projeto Android desenvolvido em **Kotlin** com o objetivo de apresentar informações detalhadas sobre o dispositivo onde a aplicação está a ser executada. Recolhe dados do sistema através da classe `Build` e apresenta-os no ecrã de forma organizada e legível. Demonstra boas práticas como o uso de `ConstraintLayout`, `ScrollView` e o ajuste automático do conteúdo às barras do sistema.

#### Objetivo

Explorar a recolha e apresentação de informações do dispositivo nativo em Android, praticando conceitos como:

* Criação de uma `Activity` em Kotlin
* Acesso a propriedades do sistema através da classe `Build`
* Apresentação dinâmica de texto num `TextView`
* Organização da interface com `ScrollView` e `WindowInsets`

#### Estrutura da tarefa

```text

```

app/
└── src/
└── main/
├── java/com/example/helloworldoptional/
│   └── MainActivity.kt
├── res/
│   ├── layout/
│   │   └── activity_main.xml
│   └── values/
│       └── strings.xml

#### MainActivity

A `MainActivity` herda de `AppCompatActivity`. No `onCreate`, carrega o layout principal, obtém a referência para o `TextView` identificado como `deviceInfo` e constrói uma string multilinha com os dados recolhidos através da classe `Build` e `Build.VERSION`. É também configurado um listener (`ViewCompat.setOnApplyWindowInsetsListener`) para garantir que o conteúdo não fica escondido atrás das barras do sistema.

#### Informação apresentada sobre o dispositivo

Os dados são obtidos diretamente através da API do sistema e incluem:

* **Manufacturer** – fabricante do dispositivo
* **Model** – modelo do equipamento
* **Brand** – marca comercial
* **Type** – tipo de build do sistema
* **User** – utilizador associado à compilação do sistema
* **Base** – sistema operativo base
* **Incremental** – versão incremental da build
* **SDK** – nível de API do Android
* **Version Code** – versão do Android em formato legível
* **Display** – identificador da build instalada

#### Interface Gráfica e Organização

A interface foi concebida para ser simples e legível. Um cabeçalho no topo identifica a finalidade da aplicação, enquanto um `ScrollView` garante que toda a informação pode ser consultada mesmo em ecrãs mais pequenos. Dentro do `ScrollView`, um `LinearLayout` organiza verticalmente os elementos, sendo a escolha ideal para priorizar a clareza da apresentação desta listagem dinâmica de dados.

---

