# Computação Móvel - Assignments

Este repositório reúne todos os trabalhos práticos (Assignments) desenvolvidos no âmbito da unidade curricular de **Computação Móvel**. 
A estrutura está concebida para integrar múltiplos Assignments (**A1**, **A2**, ..., **An**), facilitando a navegação e a consulta rápida da documentação, código e configuração para cada projeto e/ou tarefa.

## 📌 Índice de Assignments

* [Assignment 1 (A1)](#assignment-1-a1)
* [Assignment 2 (A2)](#assignment-2-a2)

---

## Assignment 1 (A1)

Este primeiro bloco de trabalhos foca-se na introdução e consolidação da linguagem **Kotlin**, bem como na aprendizagem dos fundamentos de desenvolvimento nativo para **Android**.

### Índice de Tarefas (A1)
1. [Tarefa 1 – Kotlin Tutorial Exercises](#1-tarefa-1--kotlin-tutorial-exercises)
2. [Hello World V2](#2-hello-world-v2)
3. [Library Management System (Virtual Library)](#3-library-management-system-virtual-library)
4. [Hello World Optional](#4-hello-world-optional)
5. [City Mood Scanner](#5-city-mood-scanner)

---

### 1. Tarefa 1 – Kotlin Tutorial Exercises

**Explicação do Código:**  
A `Tarefa1` agrupa a resolução de 3 exercícios concebidos para introduzir diferentes conceitos-chave de Kotlin:
- **Exer 1:** Centra-se em coleções. Demonstra diferentes formas de inicializar e popular arrays gerando quadrados (de 1^2 até 50^2) com a utilização de lambdas nos construtores `IntArray`, `Array` e as funções `range` em conjunção com `.map()`.
- **Exer 2:** Consiste no desenvolvimento de uma calculadora em Consola. O código utiliza leitura síncrona com `readln()`, valida e processa entradas utilizando de forma expressiva expressões `when`. Trata eventuais problemas através de blocos `try-catch`, isolando exceções como a divisão por zero (`ArithmeticException`) e introduções inválidas (`IllegalArgumentException`).
- **Exer 3:** Trabalha com progressões. Utiliza a função `generateSequence` para emular os ressaltos de um objeto (que perde 40% da altura em cada salto), ilustrando o uso do paradigma funcional com `takeWhile`, `take` e `map`.

**Estrutura de Ficheiros:**
```text
A1/Tarefa1/
└── src/main/kotlin/cm/
    ├── exer_1/exer_1.kt
    ├── exer_2/exer_2.kt
    └── exer_3/exer_3.kt
```

---

### 2. Hello World V2

**Explicação do Código:**  
A `Hello World V2` é uma aplicação Android simples, desenvolvida para demonstrar diferentes conceitos fundamentais e o ciclo de vida da `Activity`.
Na lógica contida na `MainActivity`, o código recorre à biblioteca AndroidX integrando o `enableEdgeToEdge()` e gerindo um `WindowInsetsListener` para adaptar o ecrã permitindo ocultar a system bar, de modo a aproveitar o espaço de "edge a edge". 
No que diz respeito à componente visual gerida em UI/XML, usa centralização de textos (`strings.xml`), estilos e cores predefinidas (`colors.xml`), dispondo Views elementares (tais como `TextView`, `CalendarView` e `ImageView`) dentro de um `ConstraintLayout`. Além disso, possui lógica de resposta a orientações diversificadas possuindo layouts quer para retrato, quer para paisagem (`layout-land`).

**Estrutura de Ficheiros:**
```text
A1/CM/
├── app/src/main/
│   ├── java/cm_a15044/helloworld/
│   │   └── MainActivity.kt
│   └── res/
│       ├── layout/activity_main.xml         (Design vertical)
│       ├── layout-land/activity_main.xml    (Design horizontal)
│       ├── values/strings.xml, colors.xml, themes.xml
│       └── drawable/smile.png
```

---

### 3. Library Management System (Virtual Library)

**Explicação do Código:**  
Este projeto serve como exercício prático nos principais pilares de *Programação Orientada a Objetos* (POO) em Kotlin, onde se simula a gestão de uma biblioteca.
A classe abstrata fundamental `Book` agrega lógica intemporal (como métodos referentes ao ano de publicação e a própria descrição). Através de conceitos de *Inheritance* (herança), a classe divide-se especificamente para dois contextos:
- **PhysicalBook:** Subclasse que aborda questões como número de cópias físicas (que são atualizadas sempre que existe um empréstimo ou devolução), capa rija e peso em kg.
- **DigitalBook:** Subclasse que define características mais próprias deste meio digital como tamanho em MB e as extensões (PDFs e etc.).
A `Library` atua como agregador global e dispõe ainda de um `companion object` útil para o tracking geral de objetos alocados. Por fim, utilizam-se  `data class` na estruturação e agregação de dados relativos aos diferentes Sócios (`LibraryMember`).

**Estrutura de Ficheiros:**
```text
A1/VirtualLibrary/
└── src/main/kotlin/
    ├── Book.kt
    ├── DigitalBook.kt
    ├── PhysicalBook.kt
    ├── Library.kt
    ├── LibraryMember.kt
    └── Main.kt
```

---

### 4. Hello World Optional

**Explicação do Código:**  
Esta aplicação Android foca-se na obtenção programática de metadados inerentes ao dispositivo onde se encontra atualmente escalada, utilizando as diversas flags expostas pela API `Build` do Android.
Na `MainActivity`, todo o processo de levantamento deste perfil (propriedades `Build.BRAND`, `Build.MODEL`, `Build.VERSION.RELEASE`, `Build.VERSION.SDK_INT`, etc.) ocorre no ciclo `onCreate`, compondo depois progressivamente toda a estrutura numa só variável iterável. Para possibilitar ao utilizador acesso universal a toda essa grande listagem concatenada, no Layout XML injeta-se e insere-se um `TextView` no interior de um `ScrollView`. O ajuste ao layout com o `ViewCompat` garante que a informação nunca colpare ou invada "safe zones".

**Estrutura de Ficheiros:**
```text
A1/helloWorldOptional/
├── app/src/main/
│   ├── java/com/example/helloworldoptional/
│   │   └── MainActivity.kt
│   └── res/
│       ├── layout/activity_main.xml
│       └── values/strings.xml
```

---

### 5. City Mood Scanner

**Explicação do Código:**  
Este é um projeto avançado no âmbito de A1 que se foca em demonstrar o potencial da stack de aplicações Kotlin contemporâneo (Android). O sistema arquitetura-se maioritariamente num padrão *MVVM (Model-View-ViewModel)*.
A `MainActivity` funciona em estrita ligação com o ViewModel (`MapViewModel`), processando intenções através de repositórios como o `EnvironmentRepository` para coordenar acessos sem expor a interface local aos pedidos. Utiliza o `Retrofit` (configurado em `RetrofitClient`) para invocar APIs Web e solicitar dados sobre poluição, geolocalização exata, clima e ruído do respetivo local. As entidades obtidas nos endpoints REST são seguidamente refletidas para Data Classes concretas adaptadas a estas APIs (como `AirQualityResponse`, `WeatherResponse`, etc.).

**Estrutura de Ficheiros:**
```text
A1/CityMoodScanner/
├── app/src/main/
│   ├── java/com/example/citymoodscanner/
│   │   ├── MainActivity.kt
│   │   ├── ui/MapViewModel.kt, UiState.kt
│   │   ├── data/
│   │   │   ├── model/ (ex: AirQualityResponse.kt, EnvironmentData.kt...)
│   │   │   ├── remote/ (ApiServices.kt, RetrofitClient.kt)
│   │   │   └── repository/EnvironmentRepository.kt
```

---

## Assignment 2 (A2)

Este segundo bloco de trabalhos introduz e aprofunda conceitos avançados de **Kotlin** e estende a construção de aplicações nativas em **Android** utilizando arquiteturas e padrões como MVVM e DataBinding.

### Índice de Tarefas (A2)
1. [Section1 - Kotlin Events](#1-section1---kotlin-events)
2. [Section1.2 - Kotlin Cache Generics](#2-section12---kotlin-cache-generics)
3. [Section1.3 - Kotlin DSL Pipeline](#3-section13---kotlin-dsl-pipeline)
4. [Section1.4 - Kotlin Operator Overloading](#4-section14---kotlin-operator-overloading)
5. [Section2 - Cool Weather App](#5-section2---cool-weather-app)
6. [Section3 - Dog Images App](#6-section3---dog-images-app)

---

### 1. Section1 - Kotlin Events

**Explicação do Código:**  
A `Section1-Kotlin` demonstra a modelação de um sistema de Gestão de Eventos orientado a objetos com coleções. O código tira partido de Classes de Dados (`Login`, `Purchase`, `Logout` derivadas da interface genérica/classe abstraída `Event`) para mapear os dados em memória. Utiliza funções de ordem superior (`processEvents`) que iteram sob as coleções dos eventos aplicando processamento personalizado por via de lambdas e manipulação de fluxos (filtragens com `.filter()`).

**Estrutura de Ficheiros:**
```text
A2/Section1-Kotlin/
├── src/main/kotlin/
│   ├── Event.kt
│   └── Main.kt
└── pom.xml
```

---

### 2. Section1.2 - Kotlin Cache Generics

**Explicação do Código:**  
A `Section1_2-Kotlin` tem como foco o uso de **Genéricos (Generics)** aplicados a estruturas de dados personalizadas, em específico numa estrutura de Memória Cache (Chave-Valor) flexível. A classe `Cache<K, V>` demonstra métodos fundamentais de acesso como `getOrPut` (utilizando lambdas de inicialização diferida), mutações dinâmicas através de funções como `transform` e limpezas controladas de memória com operações como `evict()`.

**Estrutura de Ficheiros:**
```text
A2/Section1_2-Kotlin/
├── src/main/kotlin/
│   ├── Cache.kt
│   └── Main.kt
└── pom.xml
```

---

### 3. Section1.3 - Kotlin DSL Pipeline

**Explicação do Código:**  
A `Section1_3-Kotlin` foca-se na elaboração de uma arquitetura limpa de Processamento em Lote (Pipeline) alavancada pela criação de uma **DSL (Domain-Specific Language)** fluida combinada com padrões estendidos baseados em *Builder Pattern*. A função `buildPipeline` constrói encadeamentos com diversas "fases" configuráveis (stages, trimming, filters) que transformam logs e erros. A solução ilustra ainda a gestão avançada de bifurcações e paralelismo estrutural contendo forks dinâmicos de sub-pipelines.

**Estrutura de Ficheiros:**
```text
A2/Section1_3-Kotlin/
├── src/main/kotlin/
│   ├── Main.kt
│   └── Pipeline.kt
└── pom.xml
```

---

### 4. Section1.4 - Kotlin Operator Overloading

**Explicação do Código:**  
A `Section1_4-Kotlin` foi exclusivamente programada para demonstrar **Sobrecarga de Operadores (Operator Overloading)** em Kotlin, estendendo-se através do desenho de Vetores Bi-Dimensionais (`Vec2`). Redefine métodos elementares (utilizando a keyword `operator fun`) simulando comportamentos algébricos diretos: adição (`+`), subtração (`-`), multiplicação escalar (`*`), indexador posicional dinâmico (`a[x]`) e as respetivas rotinas lógicas subjacentes à interface `Comparable` (tais como `<`, `>`).

**Estrutura de Ficheiros:**
```text
A2/Section1_4-Kotlin/
├── src/main/kotlin/
│   ├── Main.kt
│   └── Vec2.kt
└── pom.xml
```

---

### 5. Section2 - Cool Weather App

**Explicação do Código:**  
Este projeto Android constitui uma aplicação meteorológica (`dam_A15044coolweatherapp`) que alavanca vigorosamente a arquitetura **MVVM** suportada de ponta a ponta. Através da `MainActivity`, orquestra o layout visual por intermédio de `DataBinding` observando estados emitidos a partir do seu ViewModel. A app tem particular relevância por tratar variações nativas de modo de ecrã (Dark/Light Mode) com manipulação de tonalidades (*Tint List* combinada com resoluções de sufixo no `getIdentifier`), adaptação fluida com `WindowInsetsListener` (edge-to-edge), e processamento granular de permissões de *Geolocalização* (`Manifest.permission.ACCESS_FINE_LOCATION`) durante o runtime.

**Estrutura de Ficheiros:**
```text
A2/Section2-Android/
└── dam_A15044coolweatherapp/
    └── app/src/main/
        ├── java/com/example/dam_a15044coolweatherapp/
        │   ├── MainActivity.kt
        │   ├── WeatherData.kt
        │   └── WeatherViewModel.kt
        └── res/
            └── layout/activity_main.xml
```

---

### 6. Section3 - Dog Images App

**Explicação do Código:**  
A `Section3-Android` exibe um cenário primário virado para a comunicação síncrona/assíncrona baseada em pedidos HTTP. Enaltece metodologias modernas para consumo de APIs remotas através da biblioteca **Retrofit**, requisitando imagens aleatórias. Todas as entidades extraídas são acomodadas uniformemente em memória sob a alçada de um `ImageRepository` orquestrado num `MainViewModel`. Em termos visuais, a aplicação serve-se de uma `RecyclerView` unida intrinsecamente ao seu `ImageAdapter`, providenciando reações interativas através da gestão de `SwipeRefreshLayout` (pull-to-refresh) e elementos correlacionados.

**Estrutura de Ficheiros:**
```text
A2/Section3-Android/
└── Android/app/src/main/
    ├── java/com/example/section3_android/
    │   ├── MainActivity.kt
    │   ├── adapter/ImageAdapter.kt
    │   ├── api/ (DogApiService.kt, RetrofitClient.kt)
    │   ├── model/ImageItem.kt
    │   ├── repository/ImageRepository.kt
    │   └── viewmodel/MainViewModel.kt
    └── res/
        └── layout/ (activity_main.xml, item_image.xml)
```
