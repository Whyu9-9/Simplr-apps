package com.example.registerandloginappswithappwrite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val client = Client(applicationContext)
            .setEndpoint("http://[localhost]/v1") // Your API Endpoint
            .setProject("6270f31225c14c3205c6") // Your project ID
            .setSelfSigned(true)

        val account = Account(client)

        sharedPreferences = getSharedPreferences("is_logged", Context.MODE_PRIVATE)
        val userIDs = sharedPreferences.getString("user_id", null)
        val sessionId = sharedPreferences.getString("SESSION_ID", null)
        val ips = sharedPreferences.getString("ip", null)
        val deviceModel = sharedPreferences.getString("device_model", null)
        val deviceBrand = sharedPreferences.getString("device_brand", null)
        val deviceOs = sharedPreferences.getString("device_os", null)
        val deviceOsVer = sharedPreferences.getString("device_os_ver", null)

        ip.text = ips
        userID.text = userIDs
        deviceMod.text = deviceModel
        deviceBra.text = deviceBrand
        OS.text = deviceOs
        OsVer.text = deviceOsVer

        endSession.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("End Session")
                .setMessage("Are You Sure Want to End Your Session?")
                .setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    flushSession(account, sessionId!!)
                }.setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }.show()
        }
    }

    private fun flushSession(account: Account, sessionIds: String) {
        GlobalScope.launch {
            try {
                val response = account.deleteSession(
                    sessionId = sessionIds
                )
                flushSharedPref()
                Log.e("Appwrite", response.toString())
                this@MainActivity.runOnUiThread(Runnable {
                    Toast.makeText(this@MainActivity, "Session Flushed", Toast.LENGTH_SHORT).show()
                })
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }catch(e : AppwriteException) {
                this@MainActivity.runOnUiThread(Runnable {
                    Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    private fun flushSharedPref() {
        sharedPreferences = getSharedPreferences("is_logged", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("SESSION_ID").apply()
        sharedPreferences.edit().remove("ip").apply()
        sharedPreferences.edit().remove("device_model").apply()
        sharedPreferences.edit().remove("device_brand").apply()
        sharedPreferences.edit().remove("device_os").apply()
        sharedPreferences.edit().remove("device_os_ver").apply()
        sharedPreferences.edit().remove("user_id").apply()
    }
    
    private fun isBooleanAccepted(){
        val intent = Intent(this@MainActivity, RegisterActivity::class.java)
        if(1){
            startActivity(intent)
        }else{
            return false
        }
    }
}
