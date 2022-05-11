package com.example.registerandloginappswithappwrite

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val client = Client(applicationContext)
            .setEndpoint("http://[localhost]/v1") // Your API Endpoint
            .setProject("6270f31225c14c3205c6") // Your project ID
            .setSelfSigned(true)
        login.setOnClickListener {
            val emails = email.text.toString()
            val passwords = password.text.toString()
            loginProcess(client, emails, passwords)
        }
        registerActivity.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Suppress("DEPRECATION")
    @OptIn(DelicateCoroutinesApi::class)
    private fun loginProcess(client: Client, email:String, password:String) {
        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.setMessage("Trying to Login..")
        progressDialog.show()
        GlobalScope.launch {
            try {
                val account = Account(client)
                val response = account.createSession(
                    email = email,
                    password = password
                )

                val json = response
                val ip = json.ip
                val deviceModel = json.deviceModel
                val deviceBrand = json.deviceBrand
                val deviceOS = json.osName
                val deviceOSver = json.osVersion
                val sessionId = json.id
                val userId = json.userId
                goToMain(ip, deviceModel, deviceBrand, deviceOS, deviceOSver, sessionId, userId)
                progressDialog.dismiss()
            } catch(e : AppwriteException) {
                this@LoginActivity.runOnUiThread(Runnable {
                    Toast.makeText(this@LoginActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                })
                progressDialog.dismiss()
            }
        }
    }

    private fun goToMain(ip: String, deviceModel: String, deviceBrand: String, deviceOS: String, deviceOSver: String, sessionId: String, userId: String) {
        val intent = Intent(this, MainActivity::class.java)
        sharedPreferences = getSharedPreferences("is_logged", Context.MODE_PRIVATE)
        val editor        = sharedPreferences.edit()
        editor.apply{
            putString("SESSION_ID", sessionId)
            putString("ip", ip)
            putString("device_model", deviceModel)
            putString("device_brand", deviceBrand)
            putString("device_os", deviceOS)
            putString("device_os_ver", deviceOSver)
            putString("user_id", userId)
        }.apply()
        startActivity(intent)
        finish()
    }
}