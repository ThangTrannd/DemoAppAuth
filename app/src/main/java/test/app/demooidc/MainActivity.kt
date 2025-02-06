package test.app.demooidc

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationResponse
import test.app.demooidc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userRepository = UserRepositoryImpl(RetrofitInstance.api)
        val factory = MainViewModelFactory(application,userRepository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.fetchUserInfo(SessionManager.getAccessToken(this))
        binding.btnLogin.setOnClickListener {
            if (SessionManager.getAccessToken(this).isEmpty()){
                viewModel.startLogin(this)
            }else{
                viewModel.logoutUser(this)
            }
        }

        lifecycleScope.launch {
            viewModel.userInfo.collect { userInfo ->
                binding.txtInfo.text = userInfo?.let {
                    "Name: ${it.name} \nEmail: ${it.email}"
                } ?: ""
            }
        }

        lifecycleScope.launch {
            viewModel.isLoggedIn.collect { isLoggedIn ->
                binding.btnLogin.text = if (isLoggedIn) "Logout" else "Login"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainViewModel.AUTH_REQUEST_CODE && data != null) {
            val response = AuthorizationResponse.fromIntent(data)

            if (response != null) {
                viewModel.handleAuthResult(response, this)
            }
        }
    }
}
