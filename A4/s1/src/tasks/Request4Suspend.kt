package tasks

import contributors.*

// Implementação da função de suspensão para obter os contribuidores sem bloquear a thread
suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {
    // 1. Obtemos a lista de repositórios da organização chamando a função suspendida do GitHubService.
    // Como getOrgRepos é uma "suspend fun", a thread atual (UI Thread, por exemplo) não é bloqueada.
    // Em vez disso, a coroutine é suspensa até a rede responder, libertando a thread para outros trabalhos.
    val responseRepos = service.getOrgRepos(req.org)
    
    // Convertendo a resposta do Retrofit para uma lista de repositórios (usando a função de extensão bodyList()).
    val repos = responseRepos.bodyList()

    // 2. Para cada repositório encontrado, fazemos um pedido para obter os contribuidores.
    // Usamos flatMap para que todas as listas de contribuidores (de cada repositório) sejam 
    // achatadas (merged) numa única lista final de utilizadores.
    val allUsers = repos.flatMap { repo ->
        // Chamada de suspensão para obter os contribuidores do repositório específico.
        // O código aguarda aqui (suspende) por cada resposta sequencialmente, o que significa 
        // que o segundo pedido só arranca após o primeiro terminar, etc.
        val responseUsers = service.getRepoContributors(req.org, repo.name)
        
        // Convertemos a resposta para lista
        responseUsers.bodyList()
    }
    
    // 3. Por fim, chamamos aggregate() para somar as contribuições caso o mesmo utilizador
    // tenha contribuído em repositórios diferentes, devolvendo a lista consolidada.
    return allUsers.aggregate()
}