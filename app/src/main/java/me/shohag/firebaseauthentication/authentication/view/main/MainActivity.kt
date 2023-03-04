package me.shohag.firebaseauthentication.authentication.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.shohag.firebaseauthentication.R
import me.shohag.firebaseauthentication.authentication.view.login.LoginActivity
import me.shohag.firebaseauthentication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var _binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        auth = Firebase.auth

        observeAuthState()


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.sign_out -> {
                auth.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeAuthState() {
        viewModel.authenticationState.observe(this) {
            when (it) {
                AuthenticationState.UNAUTHENTICATED -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else -> {
                    auth.currentUser?.let { currentUser -> updateUI(currentUser) }
                }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser) {
        _binding.tvEmail.text = currentUser.email
    }
}