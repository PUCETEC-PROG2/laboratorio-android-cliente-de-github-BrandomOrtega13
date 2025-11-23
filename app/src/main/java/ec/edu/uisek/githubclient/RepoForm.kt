package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "EXTRA_MODE"
        const val EXTRA_REPO_NAME = "EXTRA_REPO_NAME"
        const val EXTRA_REPO_DESCRIPTION = "EXTRA_REPO_DESCRIPTION"
        const val EXTRA_REPO_OWNER = "EXTRA_REPO_OWNER"

        const val MODE_CREATE = "MODE_CREATE"
        const val MODE_EDIT = "MODE_EDIT"
    }

    private lateinit var repoFormBinding: ActivityRepoFormBinding

    private var mode: String = MODE_CREATE
    private var originalName: String = ""
    private var ownerLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)

        // Leer modo e info si viene en modo edición
        mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_CREATE
        if (mode == MODE_EDIT) {
            originalName = intent.getStringExtra(EXTRA_REPO_NAME) ?: ""
            ownerLogin = intent.getStringExtra(EXTRA_REPO_OWNER) ?: ""
            val description = intent.getStringExtra(EXTRA_REPO_DESCRIPTION) ?: ""

            // Rellenar campos
            repoFormBinding.nameRepoInputForm.setText(originalName)
            repoFormBinding.nameRepoInputForm.isEnabled = false   // NO editable al editar
            repoFormBinding.descriptionRepoInputForm.setText(description)
        }

        repoFormBinding.buttonCancelRepo.setOnClickListener { finish() }

        repoFormBinding.buttonSaveRepo.setOnClickListener {
            if (mode == MODE_EDIT) {
                updateRepo()
            } else {
                createRepo()
            }
        }
    }

    private fun validateFormRepo(): Boolean {
        val repoName = repoFormBinding.nameRepoInputForm.text.toString()

        // Solo validamos nombre en modo CREAR (en editar está bloqueado)
        if (mode != MODE_EDIT) {
            if (repoName.isBlank()) {
                repoFormBinding.nameRepoInputForm.error =
                    "El nombre del repositorio es obligatorio"
                return false
            }

            if (repoName.contains(" ")) {
                repoFormBinding.nameRepoInputForm.error =
                    "El nombre del repositorio no puede tener espacios"
                return false
            }
        }

        return true
    }

    private fun createRepo() {
        if (!validateFormRepo()) return

        val repoName = repoFormBinding.nameRepoInputForm.text.toString()
        val repoDescription = repoFormBinding.descriptionRepoInputForm.text.toString()

        val repoRequest = RepoRequest(
            name = repoName,
            description = repoDescription
        )

        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.postFormRepo(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    Log.d("RepoForm", "El repositorio $repoName fue creado exitosamente")
                    showMessage("El repositorio $repoName fue creado exitosamente")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Error de autenticacion"
                        403 -> "Recurso no permitido"
                        404 -> "Recurso no encontrado"
                        else -> "Error desconocido ${response.code()}: ${response.message()}"
                    }
                    Log.e("RepoForm", errorMessage)
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                Log.e("RepoForm", "Error de red: ${t.message}")
                showMessage("Error de red: ${t.message}")
            }
        })
    }

    private fun updateRepo() {
        if (!validateFormRepo()) return

        val description = repoFormBinding.descriptionRepoInputForm.text.toString()

        val repoRequest = RepoRequest(
            name = originalName,
            description = description
        )

        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.updateRepo(ownerLogin, originalName, repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    Log.d("RepoForm", "El repositorio $originalName fue actualizado")
                    showMessage("El repositorio $originalName fue actualizado")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Error de autenticacion"
                        403 -> "Recurso no permitido"
                        404 -> "Recurso no encontrado"
                        else -> "Error desconocido ${response.code()}: ${response.message()}"
                    }
                    Log.e("RepoForm", errorMessage)
                    showMessage(errorMessage)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                Log.e("RepoForm", "Error de red: ${t.message}")
                showMessage("Error de red: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
