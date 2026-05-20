package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    // 1. Obtemos a lista de repositórios da organização. (Chamada suspensa)
    val repos = service.getOrgRepos(req.org).bodyList()
    
    // Lista acumuladora para guardar o progresso atualizado à medida que 
    // os contribuidores de cada repositório chegam.
    var allUsers = emptyList<User>()

    // 2. Iteramos sequencialmente sobre cada repositório, mantendo o índice para saber quando terminamos.
    // Usar um ciclo for normal num contexto de coroutine faz com que o fluxo
    // seja suspenso sequencialmente, sem bloquear a thread principal.
    for ((index, repo) in repos.withIndex()) {
        
        // Chamada de suspensão para obter os contribuidores deste repositório específico.
        val users = service.getRepoContributors(req.org, repo.name).bodyList()
        
        // Juntamos os novos utilizadores à lista total e agregamos as suas contribuições.
        allUsers = (allUsers + users).aggregate()
        
        // 3. Atualizamos a UI com a lista parcial atual. O callback 'updateResults' também é 'suspend'
        // garantindo que as atualizações na thread de interface sejam feitas de forma segura pela coroutine.
        // O argumento 'completed' verifica se é o último elemento do ciclo.
        val isCompleted = index == repos.lastIndex
        updateResults(allUsers, isCompleted)
    }
}
