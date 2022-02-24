package com.example.chausic.view.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.chausic.R
import com.example.chausic.firebase.Initialize
import com.example.chausic.firebase.User
import com.example.chausic.view.dashboard.DashBoardActivity
import com.example.chausic.view.registration.RegistrationActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Initialize.setupFireBase(this.resources)
        val bundle = intent.extras
        Handler(Looper.getMainLooper()).postDelayed({
            if(User.checkUserAuth()){
                User.setCurrentUser(Initialize.getAuthInstance()?.currentUser)
                val intent = Intent(this@MainActivity, DashBoardActivity::class.java)
                bundle?.let {
                    val dataType = bundle.get(resources.getString(R.string.NOTIFICATION_TYPE))
                    val userID = bundle.get(resources.getString(R.string.DATA_USERID))
                    val mixID = bundle.get(resources.getString(R.string.DATA_MIXID))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra( resources.getString(R.string.NOTIFICATION_TYPE) , dataType as String )
                    userID?.let {  intent.putExtra( resources.getString(R.string.DATA_USERID) , it as String ) }
                    mixID?.let {   intent.putExtra( resources.getString(R.string.DATA_MIXID) , it as String ) }
                }
                startActivity(intent)
                finish()
            }else{
                startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
                finish()
            }
        }, 2000)
    }
}