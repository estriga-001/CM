package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Loads all contributors for a given organization using Retrofit's callback API.
 *
 * This implementation does **not** use coroutines. Each network request is
 * executed asynchronously via `Call.enqueue`, which runs the request on a
 * background thread and invokes the supplied callbacks when the response is
 * ready. The function itself returns immediately – it does **not** block the
 * calling (UI) thread.
 *
 * @param service the generated Retrofit service for the GitHub API.
 * @param req request data containing the GitHub username, password and
 *            organization name.
 * @param updateResults a lambda that receives the final aggregated list of
 *            contributors. The UI layer normally invokes this on the Swing
 *            event‑dispatch thread (`SwingUtilities.invokeLater`).
 */
fun loadContributorsCallbacks(
    service: GitHubService,
    req: RequestData,
    updateResults: (List<User>) -> Unit
) {
    // 1️⃣ Get the list of repositories for the organisation. This call is
    //    asynchronous – the supplied Callback will be executed later.
    service.getOrgReposCall(req.org).enqueue(object : Callback<List<Repo>> {
        override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
            // Log the HTTP response (useful for debugging the tutorial UI).
            logRepos(req, response)
            val repos = response.bodyList()

            // Container for all contributors gathered from each repository.
            val allUsers = mutableListOf<User>()
            // Counter to know when the last repository request has finished.
            var remaining = repos.size

            // Edge‑case: organisation without repositories → immediately return.
            if (remaining == 0) {
                updateResults(emptyList())
                return
            }

            // For every repository start another asynchronous request to fetch its
            // contributors.
            for (repo in repos) {
                service.getRepoContributorsCall(req.org, repo.name).enqueue(object : Callback<List<User>> {
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        // Log the contributors for this repo.
                        logUsers(repo, response)
                        // Add the received users to the accumulator.
                        allUsers += response.bodyList()
                        // Decrease the counter – when it reaches zero we have all data.
                        remaining--
                        if (remaining == 0) {
                            // All repository calls finished – aggregate duplicate users.
                            updateResults(allUsers.aggregate())
                        }
                    }

                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        // If a single repo request fails we still count it down to avoid dead‑lock.
                        remaining--
                        if (remaining == 0) {
                            updateResults(allUsers.aggregate())
                        }
                    }
                })
            }
        }

        override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
            // If we cannot obtain the list of repositories at all, report an empty list.
            updateResults(emptyList())
        }
    })
}

/**
 * Helper extension that turns a Retrofit `Response<List<T>>` into a plain Kotlin `List<T>`.
 * Returns an empty list when the body is null (e.g. HTTP error). This mirrors the
 * helper used elsewhere in the project.
 */
private fun <T> Response<List<T>>.bodyList(): List<T> = body() ?: emptyList()
