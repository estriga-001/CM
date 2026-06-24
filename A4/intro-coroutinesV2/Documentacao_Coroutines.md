# Documentação de Kotlin Coroutines

Este documento serve como guia de referência técnica, focando-se especificamente nos conceitos de Coroutines abordados no exercício prático de integração com a API do GitHub. 

---

## 1. Conceito Geral de Coroutines

Em Kotlin, as **Coroutines** (co-rotinas) são uma framework de concorrência leve (lightweight threads) que permite escrever código assíncrono e não-bloqueante de forma sequencial e fácil de ler. 

Ao contrário das threads tradicionais, que são geridas pelo sistema operativo e consomem muitos recursos de memória e CPU (pesadas em trocas de contexto), as coroutines são geridas pelo Kotlin a nível aplicacional. É possível ter milhares de coroutines ativas simultaneamente na mesma thread sem que esta fique bloqueada. Quando uma coroutine aguarda por um resultado (ex: resposta de rede), ela **suspende** a sua execução, libertando a thread subjacente para executar outras tarefas. Assim que o resultado estiver pronto, a coroutine é **retomada** (resumed).

---

## 2. Análise de Métodos e Construtos

Durante a refatorização do projeto `s1` para o modelo reativo de coroutines, foram utilizadas diversas funções, construtores e operadores. Abaixo detalha-se cada um deles:

### 2.1. `suspend` (Modificador de Função)
- **O que faz:** É uma palavra-chave aplicada a uma função que indica que esta pode ser pausada e retomada posteriormente.
- **Motivo de aplicação:** Foi aplicado a funções como `loadContributorsSuspend` e aos métodos do Retrofit (`getOrgRepos`, `getRepoContributors`). Isto permite que os pedidos HTTP não bloqueiem a thread em que foram invocados (útil no Android para não congelar a UI Thread).
- **Boas Práticas:** 
  - Uma função `suspend` só pode ser chamada a partir de outra função `suspend` ou de dentro de um "coroutine builder" (ex: `launch` ou `async`).
  - Funções `suspend` devem ser inerentemente "main-safe" (seguras para serem chamadas na thread principal), delegando o trabalho pesado para Dispatchers apropriados quando necessário.

### 2.2. `coroutineScope`
- **O que faz:** É uma função de suspensão que cria um novo escopo para coroutines. Este escopo herda o contexto da coroutine externa, mas só termina quando todas as coroutines filhas lançadas dentro dele também terminarem.
- **Motivo de aplicação:** Foi utilizado no `Request5Concurrent` e `Request7Channels` para englobar os múltiplos pedidos de rede concorrentes e gerir os seus ciclos de vida estruturadamente (Structured Concurrency).
- **Boas Práticas:** 
  - Empregue sempre que precisar de decompor o trabalho em múltiplas coroutines filhas paralelas e quiser ter a certeza de que a função de topo só retorna quando todo o trabalho terminar, garantindo que não ocorrem "leaks" de coroutines esquecidas em execução no background.

### 2.3. `async`
- **O que faz:** Um coroutine builder que lança uma coroutine e retorna um `Deferred<T>` — um tipo especial de "Job" não-bloqueante equivalente a um Future/Promise em outras linguagens.
- **Motivo de aplicação:** No `Request5Concurrent`, utilizámos `async` dentro de um ciclo para fazer múltiplos pedidos HTTP aos vários repositórios ao mesmo tempo (concorrência). Cada `async` encapsulava o pedido de um único repositório, retornando uma promessa da lista de contribuidores a chegar no futuro.
- **Boas Práticas:** 
  - Usado estritamente quando há a intenção de retornar e utilizar um resultado no futuro. 
  - Não deve ser usado se a coroutine for um mero "fire and forget" (nesse caso, usar `launch`).

### 2.4. `awaitAll()`
- **O que faz:** Recebe uma coleção de objetos `Deferred<T>` e aguarda a conclusão de todos eles simultaneamente, restituindo no fim uma coleção dos resultados `List<T>`.
- **Motivo de aplicação:** Usado em conjunto com as coroutines geradas pelo `async` no `Request5Concurrent`. Esta instrução paralisa momentaneamente a continuação do código até que todas as threads assíncronas do mapeamento estejam concluídas, resolvendo as promessas numa lista materializada e sincronizada.
- **Boas Práticas:** 
  - Tem vantagem face ao uso sequencial de `await()` por cada objeto, visto falhar imediatamente se alguma coroutine no meio gerar uma exceção, ajudando a cancelar o restante trabalho inútil precocemente.

### 2.5. `launch`
- **O que faz:** Um coroutine builder semelhante ao `async`, mas que devolve um `Job` sem resultado explícito (fire-and-forget). Lança a coroutine para o fundo.
- **Motivo de aplicação:** No `Request7Channels`, foi empregado para abrir pequenos blocos concorrentes encarregues de extrair os contribuidores e depositá-los no canal. Como o canal serve de intermediário para receber os dados gerados, não precisávamos do valor devolvido diretamente do bloco (daí `launch` em vez de `async`).
- **Boas Práticas:**
  - Evite invocar trabalhos longos em `launch` sem mecanismos de tratamento de erro adequados, dado que as exceções não tratadas num `launch` farão a aplicação crashar ou a coroutine abortar dependendo do tipo de escopo.

### 2.6. `Channel<T>`
- **O que faz:** Representa um "tubo de comunicação" que faculta a passagem sincronizada de um fluxo de dados entre diferentes coroutines. Suporta as funções de suspensão `send()` e `receive()`.
- **Motivo de aplicação:** No `Request7Channels`, usámos um canal de comunicação de forma a processar a lista e refletir a atualização na UI assim que um dos pedidos respondia. Sem ter de esperar pelo mais lento dos pedidos, à medida que cada coroutine produtora empurrava utilizadores para o canal via `.send()`, a nossa rotina consumidora central recebia e agregava essas listas via `.receive()`.
- **Boas Práticas:**
  - Importante fechar os canais invocando `channel.close()` quando tiver a certeza de que não vão ser enviados mais elementos (especialmente em canais ilimitados ou quando a extração for baseada em for-loops passivos), para não originar "deadlocks" à escuta infinitamente. No nosso caso usamos um loop `repeat(n)` com contagem fixa fechada que descarta essa obrigatoriedade.

---

> **Resumo Comparativo:** Ao passarmos de `Thread` normais / `Callbacks` (como usado na secção anterior do Tutorial Kotlin) para **Coroutines**, a legibilidade do código aproxima-se do formato sequencial "tradicional", ao passo que internamente mantém todas as vantagens do assincronismo (evitando loops caóticos de *Callback Hell*). O uso de `Channels` estende a mecânica, suportando comunicações complexas tipo *Pipeline* produtor-consumidor de forma extremamente segura.
