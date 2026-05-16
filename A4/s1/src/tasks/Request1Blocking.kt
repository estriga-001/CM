package tasks

import contributors.*
import retrofit2.Response

/*
porque é que a UI congela?

em apps android, existe uma thread principal chamada UI Thread.
a responsabilidade desta thread é desenhar janelas, botões e reagir a cliques
Como o loadCotributorsBlocking é chamado nessa thread, e ele usa o .execute() (que obriga a thread a esperar pela internet,
a thread fica ocupada à espera dos dados
* */

fun loadContributorsBlocking(service: GitHubService, req: RequestData) : List<User> {
    val repos = service
        .getOrgReposCall(req.org) // prepara o pedido, mas ainda n envia nada
        .execute() // o problema
        // quando chamamos o metodo .execute(), e função é síncrona. isso significa que o programa para tudo o que está a fazer
        // e fica a espera que o servidor do github responda.
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    return repos.flatMap { repo ->
        service
            .getRepoContributorsCall(req.org, repo.name)
            .execute() // depois de obter os repos, o codigo entra num flatMap que percorre
            // cada repo e faz outra chamada bloqueante para obter os contribuidores
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

fun <T> Response<List<T>>.bodyList(): List<T> {
    return body() ?: emptyList()
}