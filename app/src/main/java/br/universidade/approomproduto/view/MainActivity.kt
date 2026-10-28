package br.universidade.approomproduto.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.universidade.approomproduto.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Recebe o resultado da tela de login do FirebaseUI
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão Sair: faz logout e volta para a tela de login
        binding.btnSair.setOnClickListener {
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                binding.txtUsuario.text = ""
                iniciarLogin()
            }
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            iniciarLogin()
        } else {
            mostrarUsuario()
        }
    }

    // Abre a tela de login/cadastro do FirebaseUI (e-mail e senha)
    private fun iniciarLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            mostrarUsuario()
        } else {
            val erro = result.idpResponse?.error?.message
            if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
            }
            finish()
        }
    }

    private fun mostrarUsuario() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.txtUsuario.text = "Logado como:\n" + user?.email
    }
}
