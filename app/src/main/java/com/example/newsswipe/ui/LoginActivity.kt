package com.example.newsswipe.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsswipe.R
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (mAuth.currentUser != null){
            val intent = Intent(this, NewsActivity::class.java)
            intent.putExtra("mAuth",mAuth.currentUser)
            startActivity(intent)
        }


        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.create_button)
        val guestButton = findViewById<Button>(R.id.guest_button)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        val appName = findViewById<TextView>(R.id.app_name)
        appName.text = Html.fromHtml("<font color=#1900BA>News</font><font color=#000000>Swipe</font>", Html.FROM_HTML_MODE_LEGACY)

        loginButton.setOnClickListener{
            if(email.text.isNullOrBlank()||password.text.isNullOrBlank()){
                Toast.makeText(this,getString(R.string.try_login_without_full_informations),Toast.LENGTH_SHORT).show()
            }
            else{
                mAuth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, NewsActivity::class.java)
                        intent.putExtra("mAuth", mAuth.currentUser?.email)
                        startActivity(intent)
                    }

                    else {
                        //Log.i("login_exception", task.exception.toString())
                        Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        registerButton.setOnClickListener{
            if(email.text.isNullOrBlank()||password.text.isNullOrBlank()){
                Toast.makeText(this,getString(R.string.try_login_without_full_informations),Toast.LENGTH_SHORT).show()
            }
            else {
                if (mAuth.currentUser != null) {
                    Toast.makeText(this, getString(R.string.register_while_logged_in), Toast.LENGTH_SHORT).show()
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, NewsActivity::class.java)
                            intent.putExtra("mAuth",mAuth.currentUser?.email)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this, getString(R.string.register_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }

        guestButton.setOnClickListener{
            //setContentView(R.layout.activity_news)
            val intent = Intent(this, NewsActivity::class.java)
            intent.putExtra("mAuth","guest")
            startActivity(intent)
        }
    }
}