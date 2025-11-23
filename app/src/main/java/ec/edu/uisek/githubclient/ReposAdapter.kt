package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposAdapter(
    private val onEditRepo: (Repo) -> Unit,
    private val onDeleteRepo: (Repo) -> Unit
) : RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

    private var repositories: List<Repo> = emptyList()

    inner class RepoViewHolder(
        private val binding: FragmentRepoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.repoName.text = repo.name
            binding.repoDescripcion.text =
                repo.description ?: "El repositorio no tiene descripcion"
            binding.repoLang.text = repo.language ?: "Lenguaje no especificado"

            Glide.with(binding.root.context)
                .load(repo.owner.avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(binding.repoOwnerImage)

            // Botones editar / eliminar
            binding.editRepoButton.setOnClickListener {
                onEditRepo(repo)
            }
            binding.deleteRepoButton.setOnClickListener {
                onDeleteRepo(repo)
            }
        }
    }

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepos: List<Repo>) {
        repositories = newRepos
        notifyDataSetChanged()
    }
}
