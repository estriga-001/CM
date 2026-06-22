# Computação Móvel - Assignments

Este repositório reúne todos os trabalhos práticos (Assignments) desenvolvidos no âmbito da unidade curricular de **Computação Móvel**. 
A estrutura está concebida para integrar múltiplos Assignments (**A1**, **A2**, ..., **An**), facilitando a navegação e a consulta rápida da documentação, código e configuração para cada projeto e/ou tarefa.

## 📌 Índice de Assignments

* [Assignment 1 (A1)](#assignment-1-a1)
* [Assignment 2 (A2)](#assignment-2-a2)
* [Assignment 3 (A3)](#assignment-3-a3)
* [Assignment 4 (A4)](#assignment-4-a4)
* [Final Project - DrivePulse](#final-project---drivepulse)

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
Esta aplicação Android foca-se na obtenção programática de metadados inerentes ao dispositivo onde se encontra atualmente em execução, utilizando as diversas flags expostas pela API `Build` do Android.
Na `MainActivity`, todo o processo de levantamento deste perfil (propriedades `Build.BRAND`, `Build.MODEL`, `Build.VERSION.RELEASE`, `Build.VERSION.SDK_INT`, etc.) ocorre no ciclo `onCreate`, compondo depois progressivamente toda a estrutura numa só variável iterável. Para possibilitar ao utilizador acesso universal a toda essa listagem concatenada, no Layout XML injeta-se um `TextView` no interior de um `ScrollView`. O ajuste ao layout com o `ViewCompat` garante que a informação nunca se sobrepõe ou invade as "safe zones".

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
A `MainActivity` funciona em estrita ligação com o ViewModel (`MapViewModel`), processando intenções através de repositórios como o `EnvironmentRepository` para coordenar acessos sem expor a interface local aos pedidos. Utiliza o `Retrofit` (configurado em `RetrofitClient`) para invocar APIs Web e solicitar dados sobre poluição, geolocalização exata, clima e ruído do respetivo local. As entidades obtidas nos endpoints REST são seguidamente convertidas para Data Classes concretas adaptadas a estas APIs (como `AirQualityResponse`, `WeatherResponse`, etc.).

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

---

## Assignment 3 (A3)

Este terceiro bloco de trabalhos foca-se na exploração profunda de metaprogramação através de **Annotation Processors** no ecossistema **Kotlin**, e avança de forma significativa na modernização de aplicações **Android** recorrendo a arquiteturas e paradigmas contemporâneos como **Jetpack Compose** e **StateFlow** para reatividade declarativa.

### Índice de Tarefas (A3)
1. [Greeting Processor Project](#1-greeting-processor-project)
2. [Section3 - Cool Weather App (Compose Refactor)](#2-section3---cool-weather-app-compose-refactor)

---

### 1. Greeting Processor Project

**Explicação do Código:**  
Este projeto constitui uma demonstração prática e robusta de processadores de anotações (*Annotation Processors*) customizados em Kotlin, organizado numa arquitetura modular que compreende três módulos interdependentes:

- **Módulo `annotations`:** Define as anotações `@Greeting` e `@Extract` que atuam como marcadores para elementos de código (especificamente funções) que devem ser processadas em tempo de compilação. Ambas anotações recebem **parâmetros configuráveis** (respetivamente `message` e `regex`) que alimentam a lógica geradora de código.

- **Módulo `processor`:** Implementa dois processadores distintos que herdam de `AbstractProcessor` e registam-se automaticamente com o serviço de compilação através da anotação `@AutoService`:
  - **GreetingProcessor:** Interceita métodos marcados com `@Greeting` e, aproveitando a biblioteca **KotlinPoet**, gera dinamicamente classes *wrapper* de encapsulamento. Estas classes delegam invocações, emitindo a saudação customizável (parametrizada em `message`) antes de executarem a lógica original. A geração ocorre exclusivamente durante a compilação através de **KAPT (Kotlin Annotation Processing Tool)**.
  - **RegexProcessor:** Processa métodos anotados com `@Extract`, gerando classes que estendem (via `superclass`) a classe original. Os métodos gerados aplicam a Expressão Regular instanciada (recebida como parâmetro) a um campo `input` herdado, capturando e retornando o primeiro grupo assinalado.

- **Módulo `app`:** Compreende a aplicação cliente que declara `MyClass` com métodos candidatos a processamento. Após compilação, o KAPT executa os processadores definidos, injetando as classes geradas automaticamente no classpath.

A solução ilustra conceitos avançados: **reflexão em tempo de compilação** (compile-time reflection), **geração de código** (code generation), e o padrão **Service Provider Interface (SPI)** do Java.

**Estrutura de Ficheiros:**
```text
A3/GreetingProcessorProject/
├── annotations/src/main/kotlin/org/example/annotations/
│   ├── Greeting.kt
│   └── Extract.kt
├── processor/src/main/kotlin/org/example/processor/
│   ├── GreetingProcessor.kt
│   └── RegexProcessor.kt
├── app/src/main/kotlin/app/
│   ├── MyClass.kt
│   └── DataProcessorExtractor.kt
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

### 2. Section3 - Cool Weather App (Compose Refactor)

**Explicação do Código:**  
Esta versão modernizada da aplicação meteorológica constitui uma refatorização completa e integral da abordagem apresentada em A2, migrando integralmente do paradigma imperativo baseado em *XML Layouts* e **DataBinding** para a mais recente arquitetura **Jetpack Compose** com UI declarativa.

A `MainActivity` transforma-se essencialmente num contentor agnóstico, delegando toda a composição visual através de `setContent()` que alimenta uma hierarquia de funções `@Composable`. O sistema UI descentraliza-se em componentes modulares e reutilizáveis (armazenados em `ui/`): `WeatherScreen.kt` atua como orquestrador raíz, enquanto `WeatherCard.kt`, `WeatherRow.kt`, e `CoordinatesCard.kt` encapsulam especificidades de apresentação. A `WeatherMapScreen.kt` oferece ainda funcionalidades de visualização geolocalizada.

A gestão de estado sofre uma transição decisiva: abandona-se completamente o modelo **LiveData** a favor da biblioteca reativa **StateFlow**, garantindo emissão contínua de estados (`WeatherUiState`) que a camada de apresentação observa e sincroniza de forma reativa através do operador `collectAsState()`. O `WeatherViewModel`, herança de `AndroidViewModel`, expõe `_uiState` privado e `uiState` público (transformado através de `asStateFlow()`), permitindo mutações controladas com `update { }`.

A integração com permissões de localização (`Manifest.permission.ACCESS_FINE_LOCATION`) foi modernizada recorrendo a `ActivityResultContracts.RequestMultiplePermissions()` em detrimento de fragmentos obsoletos. O `WeatherApiClient` (em `data/`) continua a orquestrar requisições REST através de **Retrofit**, alimentando o `WeatherViewModel` com dados meteorológicos em tempo real.

A aplicação mantém suporte robusto para múltiplos idiomas (ficheiros em `values-pt/`, `values-night/`) e orientações de ecrã diversificadas (`layout-land/`), garantindo adaptabilidade universal através de **Material Design 3** e ajustes de insets com `WindowInsetsListener`.

**Estrutura de Ficheiros:**
```text
A3/Section3-Android/
└── dam_a15044coolweatherapp/
    └── app/src/main/
        ├── java/com/example/dam_a15044coolweatherapp/
        │   ├── MainActivity.kt
        │   ├── data/
        │   │   ├── WeatherData.kt
        │   │   └── WeatherApiClient.kt
        │   ├── ui/
        │   │   ├── WeatherScreen.kt
        │   │   ├── WeatherCard.kt
        │   │   ├── WeatherRow.kt
        │   │   ├── CoordinatesCard.kt
        │   │   └── WeatherMapScreen.kt
        │   └── viewmodel/
        │       └── WeatherViewModel.kt
        └── res/
            ├── layout/
            ├── layout-land/
            ├── values/
            ├── values-night/
            ├── values-pt/
            ├── drawable/
            ├── mipmap-*/
            └── xml/
```

---

## Assignment 4 (A4)

Este quarto bloco de trabalhos foca-se em três áreas centrais do desenvolvimento móvel moderno: **Kotlin Flows/Coroutines**, integração com **LLMs** através de chamadas HTTP em Kotlin, e utilização de **Firebase** para autenticação e persistência remota de dados.

De acordo com o enunciado extraído do PDF, o objetivo era ganhar experiência prática em código assíncrono (`threads`, `callbacks`, `coroutines`, `flows` e `channels`), configurar chamadas a modelos de linguagem como OpenAI/Gemini/Groq, controlar parâmetros de geração como `temperature` e `maxTokens`, realizar análise de sentimento e aplicar Firebase num projeto Android.

### Índice de Tarefas (A4)
1. [Kotlin Flows e Coroutines](#1-kotlin-flows-e-coroutines)
2. [Acesso a LLMs com Kotlin](#2-acesso-a-llms-com-kotlin)
3. [Firebase - Notes Pro XMLViews](#3-firebase---notes-pro-xmlviews)

---

### 1. Kotlin Flows e Coroutines

**Explicação do Código:**

A pasta `A4/s1` contém a implementação do tutorial oficial **Introduction to Coroutines and Channels**, adaptado para comparar diferentes estratégias de acesso assíncrono à API do GitHub. A aplicação usa uma interface Swing (`ContributorsUI`) que permite escolher variantes de execução e observar o impacto de cada abordagem na responsividade da UI.

O código começa com uma versão bloqueante (`Request1Blocking.kt`), que usa chamadas síncronas de Retrofit com `.execute()` e demonstra porque a UI congela quando o trabalho de rede corre na thread principal. A seguir são implementadas alternativas com thread de background, callbacks e funções `suspend`, permitindo perceber a evolução de um modelo imperativo bloqueante para um modelo assíncrono mais legível e seguro.

A versão com coroutines (`Request4Suspend.kt`) transforma os pedidos HTTP em chamadas suspensas, mantendo um fluxo sequencial de leitura fácil sem bloquear a thread. A versão concorrente (`Request5Concurrent.kt`) usa `coroutineScope`, `async` e `awaitAll()` para lançar pedidos de contribuidores em paralelo, reduzindo o tempo total quando existem vários repositórios. A função `aggregate()` centraliza a consolidação dos resultados, agrupando utilizadores repetidos e somando as contribuições antes de ordenar por ordem decrescente.

Para progresso incremental, `Request6Progress.kt` atualiza a UI à medida que cada repositório é processado. Já `Request7Channels.kt` usa `Channel<List<User>>` para separar produtores e consumidor: cada coroutine produtora envia resultados assim que termina, e a coroutine consumidora agrega e publica o progresso sem esperar pela ordem original dos pedidos. Foi também criada a estrutura `LoadingStateData`/`LoadingStateHolder` com `MutableStateFlow` e `StateFlow`, deixando uma base reativa para representar estados de loading (`isLoading`, `progress`, `message`) de forma observável.

**Porque foi feito assim:**

A sequência das variantes mostra, de forma controlada, os problemas de bloqueio, complexidade e cancelamento que aparecem em código assíncrono tradicional. As coroutines tornam o fluxo mais previsível, os `async` permitem paralelizar trabalho independente, e os `channels` resolvem melhor o caso de progresso incremental porque aceitam resultados assim que estes ficam disponíveis.

**Estrutura de Ficheiros:**
```text
A4/s1/
├── src/
│   ├── contributors/
│   │   ├── Contributors.kt
│   │   ├── ContributorsUI.kt
│   │   ├── GitHubService.kt
│   │   ├── Params.kt
│   │   └── main.kt
│   ├── samples/
│   │   ├── ChannelsSample.kt
│   │   └── ConcurrencySample.kt
│   └── tasks/
│       ├── Aggregation.kt
│       ├── LoadingStateData.kt
│       ├── Request1Blocking.kt
│       ├── Request2Background.kt
│       ├── Request3Callbacks.kt
│       ├── Request4Suspend.kt
│       ├── Request5Concurrent.kt
│       ├── Request6Progress.kt
│       └── Request7Channels.kt
├── test/
│   ├── contributors/
│   ├── samples/
│   └── tasks/
├── Documentacao_Coroutines.md
├── build.gradle
└── settings.gradle
```

**Como executar/verificar:**
```powershell
cd A4\s1
.\gradlew test
.\gradlew run
```

---

### 2. Acesso a LLMs com Kotlin

**Explicação do Código:**

A pasta `A4/AISimpleCalls` contém uma aplicação Kotlin de linha de comandos para comunicar com diferentes fornecedores de LLM através de uma interface comum (`AIAssistant`). A aplicação lê configuração local a partir de `config.properties`, cria a implementação correta através de `AIAssistantFactory` e permite interagir com o modelo num ciclo de perguntas e respostas.

A interface `AIAssistant` concentra o contrato comum: leitura da API key, construção de prompts, chamada HTTP com `OkHttp`, parsing da resposta, configuração de logs e retry com **exponential backoff** quando ocorre rate limit (`HTTP 429`). Também foram adicionadas propriedades configuráveis para `TEMPERATURE` e `MAX_TOKENS`, com valores de fallback (`0.7` e `800`) para evitar falhas quando a configuração não existe ou está inválida.

Foram implementadas variantes para **OpenAI**, **Gemini** e **Groq**. A implementação `AIAssistantGroq` usa o endpoint OpenAI-compatible da Groq, permitindo testar o trabalho com um provider de free tier. As classes `AIAssistantOpenAIClasses.kt` e `AIAssistantGeminiClasses.kt` usam `data class` e `Gson` para estruturar pedidos/respostas JSON de forma mais robusta do que montar strings manualmente.

A análise de sentimento foi implementada em `analyzeSentiment(input: String)`, que força o modelo a responder apenas com JSON contendo `rating` de 1 a 7 e `justification`. O teste `SentimentTest.kt` valida respostas positivas, neutras e negativas, verificando se o JSON é parseável, se contém as chaves esperadas e se a escala devolvida está dentro dos intervalos aceitáveis. O teste `TemperatureTest.kt` compara respostas para o mesmo prompt com temperatura baixa (`0.1`) e alta (`1.8`), demonstrando o efeito prático deste parâmetro na criatividade/variabilidade da resposta.

**Porque foi feito assim:**

A interface comum evita duplicação entre providers e permite trocar de modelo apenas através de configuração. O uso de `Properties`, `Gson` e `OkHttp` mantém a solução simples, explícita e testável. Os testes não tentam comparar texto gerado de forma rígida, porque respostas de LLM são não determinísticas; em vez disso validam propriedades observáveis, como resposta não vazia, JSON válido e rating dentro da escala definida.

**Estrutura de Ficheiros:**
```text
A4/AISimpleCalls/
├── src/main/kotlin/dam/
│   ├── AIAssistant.kt
│   ├── AIAssistantFactory.kt
│   ├── AIAssistantGemini.kt
│   ├── AIAssistantGeminiClasses.kt
│   ├── AIAssistantGroq.kt
│   ├── AIAssistantOpenAI.kt
│   ├── AIAssistantOpenAIClasses.kt
│   ├── Main.kt
│   └── Utils.kt
├── src/test/kotlin/dam/
│   ├── SentimentTest.kt
│   └── TemperatureTest.kt
├── src/main/resources/
│   └── logback.xml
├── commands.txt
├── build.gradle.kts
└── settings.gradle.kts
```

**Configuração esperada (`config.properties` local):**
```properties
OPENAI_API_KEY=your-openai-key
GEMINI_API_KEY=your-gemini-key
GROQ_API_KEY=your-groq-key
AI_LLM=GROQ
LOG_LEVEL=OFF
TEMPERATURE=0.7
MAX_TOKENS=800
```

> O ficheiro `config.properties` é ignorado pelo Git para não expor chaves privadas.

**Como executar/verificar:**
```powershell
cd A4\AISimpleCalls
.\gradlew run
.\gradlew cleanTest test --info
.\gradlew cleanTest test --tests "dam.TemperatureTest" --info
.\gradlew cleanTest test --tests "dam.SentimentTest" --info
```

---

### 3. Firebase - Notes Pro XMLViews

**Explicação do Código:**

A pasta `A4/NotesProXMLViews3` contém uma aplicação Android em Kotlin/Java baseada em **XML Views** para demonstrar autenticação e persistência com Firebase. O projeto integra `FirebaseAuth`, `FirebaseFirestore`, `Firebase Analytics` e o plugin `com.google.gms.google-services`, com configuração através de `google-services.json`.

O fluxo começa em `SplashActivity`, que espera brevemente e decide se o utilizador segue para `LoginActivity` ou para `MainActivity` consoante exista sessão Firebase ativa. Em `CreateAccountActivity`, o utilizador cria uma conta com email/password, os dados são validados localmente, é enviado email de verificação e a sessão é terminada para obrigar à validação. Em `LoginActivity`, o login só permite avançar quando o Firebase autentica o utilizador e o email já está verificado.

A área de notas é composta por `MainActivity` e `NoteDetailsActivity`. A `MainActivity` apresenta a estrutura principal com `RecyclerView`, botão flutuante para criar notas e menu. A criação/edição/remoção de notas fica em `NoteDetailsActivity`, que constrói um objeto `Note` com `title`, `content` e `timestamp` e grava no Firestore. A classe `Utility` centraliza operações reutilizáveis como `showToast()`, formatação de timestamps e obtenção da coleção correta para o utilizador autenticado:

```text
notes/{uid}/my_notes/{noteId}
```

Esta organização garante que cada utilizador trabalha dentro da sua própria subcoleção, evitando misturar notas de contas diferentes.

**Porque foi feito assim:**

O Firebase Authentication resolve o ciclo de conta/login/verificação sem implementar backend próprio. O Firestore dá uma base documental simples para guardar notas por utilizador, e as XML Views mantêm a implementação próxima do tutorial, separando ecrãs em activities claras: splash, login, criação de conta, lista de notas e detalhes da nota.

**Estrutura de Ficheiros:**
```text
A4/NotesProXMLViews3/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/notes/notesproxmlviews/
│   │   ├── SplashActivity.kt
│   │   ├── LoginActivity.kt
│   │   ├── CreateAccountActivity.kt
│   │   ├── MainActivity.kt
│   │   ├── NoteDetailsActivity.kt
│   │   ├── Note.java
│   │   └── Utility.java
│   └── res/
│       ├── layout/
│       │   ├── activity_splash.xml
│       │   ├── activity_login.xml
│       │   ├── activity_create_account.xml
│       │   ├── activity_main.xml
│       │   └── activity_note_details.xml
│       ├── drawable/
│       ├── values/
│       ├── values-night/
│       └── xml/
├── app/google-services.json
├── gradle/libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

**Como executar/verificar:**
```powershell
cd A4\NotesProXMLViews3
.\gradlew assembleDebug
```

Para executar a app num dispositivo/emulador, é necessário ter o projeto Firebase configurado e o ficheiro `app/google-services.json` válido para o package `notes.pro`.

---

## Final Project - DrivePulse

O `Final_Project` corresponde ao projeto final da unidade curricular e contém a aplicação **DrivePulse**, uma aplicação Android nativa em Kotlin orientada para a comunidade automóvel. A app combina ideias de tracking de percursos, feed social, mapa, perfil de utilizador e funcionalidades de apoio à condução, permitindo ao utilizador entrar, autenticar-se, explorar a aplicação, iniciar uma run, consultar mapa, publicar conteúdo, editar perfil e aceder às áreas de ajuda, premium, definições e informação sobre a app.

A implementação foi construída com **Jetpack Compose**, **MVVM**, **Clean Architecture**, **Repository Pattern**, **Hilt**, **Room**, **DataStore**, **Firebase Authentication/Firestore**, Google Maps e serviços de localização. A separação de pastas foi pensada para que cada parte da aplicação tenha uma responsabilidade clara: a UI fica organizada por funcionalidades, as regras de negócio ficam isoladas no domínio, e os detalhes de persistência, Firebase e APIs ficam concentrados na camada de dados.

### Índice do Final Project
1. [Introdução ao Final Project](#introdução-ao-final-project)
2. [Arquitetura e organização do Final Project](#arquitetura-e-organização-do-final-project)
3. [Estrutura de pastas do Final Project](#estrutura-de-pastas-do-final-project)
4. [Fluxo funcional em Media_Relatorio](#fluxo-funcional-em-media_relatorio)
5. [Como executar/verificar o Final Project](#como-executarverificar-o-final-project)

---

### Introdução ao Final Project

O objetivo do DrivePulse é demonstrar uma aplicação móvel completa, com vários ecrãs e fluxos reais de utilização. A app começa com um ecrã de entrada, permite login/registo, apresenta uma área principal com navegação inferior e organiza as principais funcionalidades em secções como Home, Map, Run, Community e Profile.

O projeto também inclui funcionalidades complementares necessárias para uma experiência mais completa: criação de posts, visualização de detalhes de rotas, edição de perfil, definições, página premium, FAQ/ajuda e página "About". A pasta `Media_Relatorio` documenta estes fluxos através de imagens, permitindo acompanhar visualmente a navegação desde a abertura da aplicação até à execução das suas principais funções.

---

### Arquitetura e organização do Final Project

A organização segue uma abordagem de **Clean Architecture + MVVM**. A aplicação está dividida em camadas para evitar misturar interface, regras de negócio e detalhes técnicos de persistência ou rede.

- **`feature/`** contém a camada de apresentação. Cada funcionalidade tem o seu próprio ecrã Compose, ViewModel e estados de UI quando necessário. Esta separação torna mais simples evoluir um ecrã sem afetar os restantes.
- **`domain/`** contém os modelos centrais, interfaces de repositórios, use cases e validações. Esta camada representa as regras da aplicação e não deve depender diretamente de Android, Compose, Firebase ou Room.
- **`data/`** contém as implementações concretas dos repositórios, os DAOs, entidades Room, DTOs, DataStore, Firebase/Firestore e módulos de injeção de dependências. Esta camada traduz dados externos ou locais para modelos usados pelo domínio.
- **`core/`** contém código transversal usado por várias funcionalidades, como navegação, design system, componentes comuns, localização, sessão e utilitários partilhados.

Esta divisão foi escolhida para melhorar a manutenção do projeto. Ao separar responsabilidades, a UI apenas observa estado e envia eventos para ViewModels; os ViewModels chamam use cases; os use cases usam interfaces do domínio; e os repositórios concretos tratam de Room, Firebase, DataStore ou serviços externos. Assim, a aplicação fica mais testável, mais fácil de navegar e mais resistente a alterações futuras.

Fluxo arquitetural simplificado:

```text
Composable Screen
    -> ViewModel
    -> Domain UseCase
    -> Repository Interface
    -> Repository Implementation
    -> Room / Firebase / DataStore / Location / Maps
```

---

### Estrutura de pastas do Final Project

```text
Final_Project/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/drivepulse/
│       │   │   ├── DrivePulseApplication.kt
│       │   │   ├── core/
│       │   │   ├── data/
│       │   │   ├── domain/
│       │   │   └── feature/
│       │   └── res/
│       ├── test/
│       └── androidTest/
├── docs/
├── gradle/
├── handoffs/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties.example
└── ENIDH_CM_FinalProject_2026.pdf

Media_Relatorio/
└── imagens do fluxo funcional da aplicação
```

**Descrição das pastas principais:**

- **`Final_Project/app/`** é o módulo Android principal. Contém o código da aplicação, recursos, manifesto, dependências e configuração do Firebase.
- **`Final_Project/app/src/main/java/com/drivepulse/`** é o package principal da app. Aqui fica todo o código Kotlin organizado por camadas.
- **`core/common/`** guarda tipos e componentes partilhados, como `Result`, `SessionMode`, constantes e componentes comuns de autenticação.
- **`core/designsystem/`** centraliza a identidade visual da app: tema, cores, tipografia, espaçamentos, shapes e componentes reutilizáveis como botões, cards, top bar e bottom bar.
- **`core/location/`** trata da localização, tracking GPS e serviço em foreground usado durante a gravação de runs.
- **`core/navigation/`** define destinos, itens da bottom navigation e o grafo de navegação principal em Compose.
- **`data/di/`** contém módulos Hilt para injeção de dependências, permitindo fornecer repositórios, base de dados, DAOs e serviços de localização sem acoplamento direto.
- **`data/local/`** contém a persistência local com Room, incluindo DAOs, entidades, base de dados e data sources para runs e coordenadas.
- **`data/preferences/`** usa DataStore para preferências locais, como idioma, tema e opções persistentes da aplicação.
- **`data/remote/`** contém DTOs e modelos usados na comunicação com serviços remotos, principalmente Firebase/Firestore.
- **`data/repository/`** implementa os repositórios definidos no domínio, coordenando dados locais e remotos.
- **`domain/model/`** define as entidades principais da aplicação, como utilizador, post, run, coordenada e comentário.
- **`domain/repository/`** define contratos que a camada de dados implementa. Isto permite que o domínio dependa de abstrações em vez de depender diretamente de Firebase, Room ou Android.
- **`domain/usecase/`** agrupa ações de negócio reutilizáveis, como login, registo, logout, gestão de perfil e início/fim de uma run.
- **`domain/validation/`** concentra regras de validação, por exemplo validações relacionadas com dados do carro/perfil.
- **`feature/start/`** contém o fluxo inicial da app, incluindo splash/start menu e escolha de entrada.
- **`feature/auth/`** contém login, registo, navegação de autenticação, ViewModel e estados associados.
- **`feature/main/`** funciona como host principal da aplicação depois da entrada, integrando a navegação inferior.
- **`feature/home/`** apresenta o ecrã inicial da área autenticada/guest, com resumo e acesso às funcionalidades principais.
- **`feature/map/`** contém o ecrã de mapa e a lógica associada à visualização geográfica.
- **`feature/run/`** contém a gravação de percursos, estatísticas da run, botões de controlo e Activity dedicada ao tracking.
- **`feature/community/`** contém o feed social, posts e componentes reutilizáveis do feed.
- **`feature/createpost/`** permite criar publicações para a comunidade.
- **`feature/profile/`** contém perfil, edição de perfil, onboarding/setup e respetivos ViewModels.
- **`feature/routedetail/`** contém o detalhe de uma rota, separado por ser um fluxo próprio que pode ser aberto a partir de diferentes partes da app.
- **`feature/settings/`** contém o ecrã de definições e preferências do utilizador.
- **`feature/premium/`** contém a área premium/eventos/funcionalidades extra da aplicação.
- **`feature/help/`** e **`feature/about/`** contêm páginas informativas e de apoio ao utilizador.
- **`app/src/main/res/`** contém recursos Android: imagens em `drawable/`, ícones em `mipmap-*`, strings e temas em `values/`, traduções em `values-pt/` e `values-es/`, tema noturno em `values-night/` e configurações XML em `xml/`.
- **`Final_Project/docs/`** guarda documentação técnica, como regras de desenvolvimento, arquitetura e guia de estilo da UI.
- **`Final_Project/handoffs/`** contém documentação de entrega e verificação académica do projeto.
- **`Final_Project/gradle/`** contém o Gradle Wrapper, garantindo que o projeto pode ser compilado com uma versão consistente do Gradle.

Pastas como `.gradle/`, `.idea/`, `.kotlin/`, `build/` e `app/build/` são geradas localmente por Gradle/Android Studio e não fazem parte da arquitetura funcional da aplicação.

---

### Fluxo funcional em Media_Relatorio

A pasta `Media_Relatorio`, localizada na raiz do repositório, contém imagens `.jfif` usadas para documentar visualmente o fluxo da aplicação. Esta pasta foi criada fora de `Final_Project` porque serve como material de relatório/demonstração e não como recurso compilado pela app Android.
As imagens representam a utilização da app por ordem funcional, desde a entrada até à execução das principais funcionalidades. No README, são incluídas com caminhos relativos para a pasta `Media_Relatorio/`.

| Passo | Ecrã / funcionalidade | Imagem |
|------:|---|---|
|     1 | Launch / entrada na aplicação | <img src="Media_Relatorio/Launch.jfif" alt="Launch / entrada na aplicação" width="220" /> |
|     2 | Login | <img src="Media_Relatorio/Login.jfif" alt="Ecrã de login" width="220" /> |
|     3 | Registo | <img src="Media_Relatorio/Registo.jfif" alt="Ecrã de registo" width="220" /> |
|     4 | Home | <img src="Media_Relatorio/Home.jfif" alt="Ecrã Home" width="220" /> |
|     5 | Mapa | <img src="Media_Relatorio/Map.jfif" alt="Ecrã de mapa" width="220" /> |
|     6 | Iniciar run | <img src="Media_Relatorio/Init_Run.jfif" alt="Início de run" width="220" /> |
|     7 | Run | <img src="Media_Relatorio/Run.jfif" alt="Ecrã de run" width="220" /> |
|     8 | Finalizar run | <img src="Media_Relatorio/Finish_Run.jfif" alt="Finalização de run" width="220" /> |
|     9 | Criar publicação | <img src="Media_Relatorio/Create_Post.jfif" alt="Criação de publicação" width="220" /> |
|    10 | Comunidade | <img src="Media_Relatorio/Community.jfif" alt="Ecrã de comunidade" width="220" /> |
|    11 | Perfil | <img src="Media_Relatorio/Profile.jfif" alt="Ecrã de perfil" width="220" /> |
|    12 | Editar perfil | <img src="Media_Relatorio/Edit_Prof.jfif" alt="Edição de perfil" width="220" /> |
|    13 | Definições | <img src="Media_Relatorio/Settings.jfif" alt="Ecrã de definições" width="220" /> |
|    14 | Premium | <img src="Media_Relatorio/Premium_page.jfif" alt="Página premium" width="220" /> |
|    15 | Ajuda / FAQ | <img src="Media_Relatorio/Help_faq.jfif" alt="Ecrã de ajuda e FAQ" width="220" /> |
|    16 | About | <img src="Media_Relatorio/About.jfif" alt="Página About" width="220" /> |

Esta organização permite consultar rapidamente o percurso completo do utilizador: abertura da aplicação, autenticação/registo, navegação pela home, mapa, gravação de percurso, publicação na comunidade, gestão de perfil, definições e páginas informativas.

---

### Como executar/verificar o Final Project

```powershell
cd Final_Project
.\gradlew.bat :app:assembleDebug
```

Para executar a aplicação no Android Studio, é necessário abrir a pasta `Final_Project`, garantir que o SDK Android está configurado e que o ficheiro `local.properties` contém a chave `MAPS_API_KEY` quando for necessário usar Google Maps. O ficheiro `app/google-services.json` deve corresponder ao package `com.drivepulse` para que Firebase Authentication e Firestore funcionem corretamente.
