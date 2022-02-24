package com.example.chausic.view.registration.login

import android.app.Application
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.firebase.Registration

class LoginFragmentViewModel(app: Application):AndroidViewModel(app) {

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?>
        get() = _loading

    private val _isSuccess = MutableLiveData<Boolean?>()
    val isSuccess : LiveData<Boolean?>
        get() = _isSuccess



    fun getData(email: String, password: String, requireActivity: FragmentActivity, requireView: View) {
        _loading.value = true
        sendUserData(email,password,requireActivity,requireView)
    }

    private fun sendUserData(email: String, password: String, requireActivity: FragmentActivity, requireView: View) {
         Registration.sendSignInRequest(email,password,requireActivity,requireView){ response ->
            if(response){
                whenSuccess()
            }else{
                whenFail()
            }
        }
    }

    private fun whenFail(){
        _loading.value = false
    }

    private fun whenSuccess(){
        _isSuccess.value = true
        _loading.value = false
    }

}