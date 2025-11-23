package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecylerView()
        fetchRepositories()
    }

    private fun setupRecylerView() {
        reposAdapter = ReposAdapter(
            onEditRepo = { repo -> displayEditRepoForm(repo) },
            onDeleteRepo = { repo -> deleteRepo(repo) }
        )
        binding.repoRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (!repos.isNullOrEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No existe repositorios a mostrar")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Error de autenticacion"
                        403 -> "Recurso no permitido"
                        404 -> "Recurso no encontrado"
                        else -> "Error desconocido ${response.code()}: ${response.message()}"
                    }
                    Log.e("MainActivity", errorMessage)
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("Error de conexion")
                Log.e("MainActivity", "Error de conexion ${t.message}")
            }
        })
    }

    private fun deleteRepo(repo: Repo) {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.deleteRepo(repo.owner.login, repo.name)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio ${repo.name} eliminado correctamente")
                    fetchRepositories()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Error de autenticacion"
                        403 -> "Recurso no permitido"
                        404 -> "Recurso no encontrado"
                        else -> "Error desconocido ${response.code()}: ${response.message()}"
                    }
                    Log.e("MainActivity", errorMessage)
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error de conexion")
                Log.e("MainActivity", "Error de conexion ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }

    private fun displayEditRepoForm(repo: Repo) {
        Intent(this, RepoForm::class.java).apply {
            putExtra(RepoForm.EXTRA_MODE, RepoForm.MODE_EDIT)
            putExtra(RepoForm.EXTRA_REPO_NAME, repo.name)
            putExtra(RepoForm.EXTRA_REPO_DESCRIPTION, repo.description ?: "")
            putExtra(RepoForm.EXTRA_REPO_OWNER, repo.owner.login)
            startActivity(this)
        }
    }
}
