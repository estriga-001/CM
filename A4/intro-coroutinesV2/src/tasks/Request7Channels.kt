package tasks

import contributors.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {
        // 1. Obtemos a lista de repositórios da organização sequencialmente (chamada suspensa).
        val repos = service.getOrgRepos(req.org).bodyList()
        
        // 2. Criamos um Channel. Os canais são como tubos de comunicação entre coroutines.
        // Eles permitem que coroutines produtoras enviem dados e coroutines consumidoras
        // os recebam de forma sincronizada e thread-safe.
        val channel = Channel<List<User>>()

        // 3. Para cada repositório, lançamos uma nova coroutine usando "launch".
        // Isto significa que os pedidos HTTP para obter contribuidores serão feitos 
        // de forma totalmente concorrente (ao mesmo tempo).
        for (repo in repos) {
            launch {
                val users = service.getRepoContributors(req.org, repo.name).bodyList()
                // Assim que a resposta de um repositório chega, a lista de utilizadores 
                // é enviada para o canal.
                channel.send(users)
            }
        }

        // 4. Receção de dados do canal:
        // Variável acumuladora para guardar o progresso global.
        var allUsers = emptyList<User>()
        
        // Sabemos exatamente quantas mensagens o canal vai receber (uma por repositório).
        // Usamos um ciclo repeat para iterar exatamente repos.size vezes.
        repeat(repos.size) { index ->
            // A chamada channel.receive() suspende a execução até que haja um elemento
            // disponível no canal. Assim, consumimos os resultados logo que qualquer 
            // um dos pedidos concorrentes termine, independentemente da ordem.
            val users = channel.receive()
            
            // Juntamos os utilizadores recém-recebidos à lista total e agregamos.
            allUsers = (allUsers + users).aggregate()
            
            // Atualizamos a UI através do callback.
            // Consideramos concluído quando recebemos a última mensagem esperada.
            val isCompleted = index == repos.lastIndex
            updateResults(allUsers, isCompleted)
        }
    }
}
