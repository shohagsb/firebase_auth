package me.shohag.firebaseauthentication.authentication.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.shohag.firebaseauthentication.R
import me.shohag.firebaseauthentication.authentication.view.main.MainActivity
import me.shohag.firebaseauthentication.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var _binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        auth = Firebase.auth

        _binding.btnLogin.setOnClickListener {
            validateInputs()
        }

        _binding.btnGoogle.setOnClickListener {
            initGoogleSignIn()
            resultLauncher.launch(Intent(googleSignInClient.signInIntent))
        }

        _binding.buttonFacebookLogin.setReadPermissions("email", "public_profile")
        callbackManager = CallbackManager.Factory.create()

        facebookCallback()
    }


    private fun validateInputs() {
        val email = _binding.etEmail.text.toString()
        val password = _binding.etPassword.text.toString()
        if (email.isNotBlank() && password.isNotBlank()) {
            signWithEmail(email, password)
        } else {
            Toast.makeText(
                applicationContext, "Please enter Email and Password", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun signWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                showWelcomeToast(user)
                navigateToMain()
            } else {
                Toast.makeText(
                    baseContext,
                    "Authentication failed. ${task.exception?.message} ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showWelcomeToast(user: FirebaseUser?) {
        Toast.makeText(applicationContext, "Welcome ${user?.email}", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    /**
     * Configure Google Sign In
     * */
    private fun initGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)

        } catch (e: Exception) {
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * On ActivityResult Launcher Initialization
     * */
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intent: Intent? = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            task.getResult(ApiException::class.java).let { account ->
                Toast.makeText(
                    applicationContext, "Google Account ${account.email}", Toast.LENGTH_SHORT
                ).show()
                val googleTokenId = account.idToken
                val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
                signInWithCredential(authCredential)
            }
        } catch (e: ApiException) {
            Toast.makeText(
                applicationContext, "Sign In Failed! ERROR3: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun facebookCallback() {
        _binding.buttonFacebookLogin.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Toast.makeText(applicationContext, "FB: Canceled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(applicationContext, "FB: Error", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken
                    val authCredential = FacebookAuthProvider.getCredential(token.token)
                    signInWithCredential(authCredential)
                }

            })
    }


    /**
     * SignIn with Credential
     * */
    private fun signInWithCredential(authCredential: AuthCredential) {
        auth.signInWithCredential(authCredential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_SHORT).show()
                val email = auth.currentUser?.email
                email?.let {
                    navigateToMain()
                }

            } else {
                // If sign in fails,
                Toast.makeText(
                    applicationContext,
                    "Google Auth failed, ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMain()
        }
    }


}