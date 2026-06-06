package tasks

import contributors.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * Non‑cancellable version of the concurrent contributors loader.
 *
 * In this variant each repository request is launched in **GlobalScope**, which
 * means the coroutines are *detached* from the UI‑scope started in
 * `Contributors.loadContributors()`.  Consequently, cancelling the UI job will
 * **not** stop these background coroutines – they will run to completion.
 * This is intentionally contrasting with the cancellable version that uses
 * `coroutineScope`.
 *
 * The function returns a **suspend** result because we still need to await the
 * async operations before we can aggregate the final list of users.
 */
suspend fun loadContributorsNotCancellable(
    service: GitHubService,
    req: RequestData
): List<User> {
    // 1️⃣ Get the list of repositories – this is a suspend call that returns
    //    a `Response<List<Repo>>`.  The helper `bodyList()` converts possible null
    //    bodies into an empty list.
    val repos = service.getOrgRepos(req.org).bodyList()

    // 2️⃣ For every repository start an async job in the *global* scope.
    //    `GlobalScope.async` creates a top‑level coroutine that lives until the
    //    JVM shuts down or the job finishes, independent of any parent.
    val deferreds = repos.map { repo ->
        GlobalScope.async {
            // Log the request/response – the UI does the same for debugging.
            logRepos(req, service.getOrgRepos(req.org)) // optional duplicate log, harmless
            // Perform the network request for this repository's contributors.
            val users = service.getRepoContributors(req.org, repo.name).bodyList()
            // Log the contributors received for this repo.
            logUsers(repo, service.getRepoContributors(req.org, repo.name))
            // Return the list for this repository.
            users
        }
    }

    // 3️⃣ Wait for **all** async jobs to finish and flatten the resulting list
    //    of lists into a single list of `User` objects.
    val allUsers = deferreds.awaitAll().flatten()

    // 4️⃣ Aggregate duplicate users (same login) and sort by total contributions.
    return allUsers.aggregate()
}
