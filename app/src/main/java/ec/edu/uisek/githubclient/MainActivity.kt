package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //comienza la magia
    private lateinit var reposAdapter: ReposAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        //lateinit se inicializa despues propio de kotlin
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecylerView()
    }

    private fun setupRecylerView(){
        reposAdapter = ReposAdapter()
        binding.repoRecyclerView.adapter = reposAdapter
    }
}
//asdassd
