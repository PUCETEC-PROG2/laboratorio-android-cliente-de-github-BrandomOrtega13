package ec.edu.uisek.githubclient.models

import android.accessibilityservice.GestureDescription
import org.intellij.lang.annotations.Language

data class Repo(
    val id: Long,
    val name: String,
    val language: String?,
    val description: String?,
    val owner: RepoOwner //deserializamos el owner del el objeto que nos da al hacer la llamada al repo de github
)

data class RepoRequest(
    //nos ayuda a enviar request desde el formulario
    val name: String,
    val description: String?
)
