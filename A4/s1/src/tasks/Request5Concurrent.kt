package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    // 1. Inicia o pedido de repositórios. Esta chamada é suspensa até a resposta chegar.
    val repos = service.getOrgRepos(req.org).bodyList()

    // 2. Usamos o map (em vez de flatMap) para transformar a lista de repositórios 
    // numa lista de Deferred (promessas de resultados futuros geradas pelo async).
    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        // O bloco async lança uma nova coroutine para cada repositório, executando-as em paralelo.
        // Assim, múltiplos pedidos HTTP correm simultaneamente, reduzindo o tempo total drásticamente.
        async {
            // Chamada de suspensão dentro da nova coroutine (independente das outras).
            service.getRepoContributors(req.org, repo.name).bodyList()
        }
    }

    // 3. awaitAll() espera que todas as coroutines do bloco async terminem e extrai os seus resultados.
    // Retorna uma List<List<User>>. O método flatten() junta tudo numa única List<User>.
    // O aggregate() agrupa e soma as contribuições de utilizadores repetidos em vários repos.
    deferreds.awaitAll().flatten().aggregate()
}