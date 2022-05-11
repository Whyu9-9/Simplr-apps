package com.example.registerandloginappswithappwrite

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val client = Client(applicationContext)
            .setEndpoint("http://[localhost]/v1") // Your API Endpoint
            .setProject("6270f31225c14c3205c6") // Your project ID
            .setSelfSigned(true)

        val account = Account(client)
        register.setOnClickListener {
            val nameS = name.text.toString()
            val email = emailRegister.text.toString()
            val password = passwordRegister.text.toString()
            createAccount(account, nameS, email, password)
        }
        loginActivity.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Suppress("DEPRECATION")
    @OptIn(DelicateCoroutinesApi::class)
    private fun createAccount(account: Account, names: String, emails: String, passwords: String) {
        val progressDialog = ProgressDialog(this@RegisterActivity)
        progressDialog.setMessage("Registering Account..")
        progressDialog.show()
        GlobalScope.launch {
            try {
                val response = account.create(
                    userId = "unique()",
                    name = names,
                    email = emails,
                    password = passwords,
                )
                this@RegisterActivity.runOnUiThread(Runnable {
                    Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()
                })
                progressDialog.dismiss()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                Log.d("Appwrite response", response.toString())
            } catch(e : AppwriteException) {
                this@RegisterActivity.runOnUiThread(Runnable {
                    Toast.makeText(this@RegisterActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                })
                progressDialog.dismiss()
            }
        }
    }
}