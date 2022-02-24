package com.example.chausic.view.dashboard.profile

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.firebase.User
import com.example.chausic.model.data.UserData
import com.example.chausic.view.util.FullScreenImage

class ProfileFragmentViewModel(private val app:Application):AndroidViewModel(app) {

    private var _data = MutableLiveData<UserData>()
    val data : LiveData<UserData>
        get() = _data

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?>
        get() = _loading

    private val _upload = MutableLiveData<Boolean?>()
    val upload: LiveData<Boolean?>
        get() = _upload

    init {
        _loading.value = true
        receiveUserData()
    }

    private fun receiveUserData(){
        User.getCurrentUserData { data,flag ->
            if (flag){
            _loading.value = false
            _data.value = data
            }else{ _loading.value = false }
        }
    }

    fun getUserNameToBeUpdated(name: String, requireView: View) {
        User.updateUserName(name,requireView)
    }

    fun getUserBioToBeUpdated(bio: String, requireView: View) {
        User.updateUserBio(bio,requireView)
    }

    fun getUserPictureToBeUpdated(filepath: String?, requireView: View) {
        _upload.value = true
        if(filepath != null){
          User.uploadUserImage(filepath,requireView){
                   _upload.value = false
           }
        }
    }

     fun onCLickProfileImage(img:String){
        val fullScreenIntent = Intent(app.applicationContext, FullScreenImage::class.java)
        fullScreenIntent.data = img.toUri()
         fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        app.applicationContext.startActivity(fullScreenIntent)
    }
}