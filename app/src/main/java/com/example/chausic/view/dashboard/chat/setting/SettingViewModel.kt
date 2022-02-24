package com.example.chausic.view.dashboard.chat.setting

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chausic.firebase.Themes
import com.example.chausic.firebase.User
import com.example.chausic.model.data.Theme
import com.example.chausic.model.data.UserData


class SettingViewModel(app: Application, private val themeID: String):AndroidViewModel(app){

    private var _data = MutableLiveData<List<Theme>>()
    val data : LiveData<List<Theme>>
        get() = _data

    private var _user = MutableLiveData<UserData>()
    val user : LiveData<UserData>
        get() = _user

    private var _currentTheme = MutableLiveData<Theme>()
    val currentTheme : LiveData<Theme>
        get() = _currentTheme

    private var _updateCurrentTheme = MutableLiveData<Theme>()
    val updateCurrentTheme : LiveData<Theme>
        get() = _updateCurrentTheme

    init {
        getUser()
        getThemes()
    }

    //get the id of the Theme From the list
    fun getThemeID(position: Int, requireView: View){
        val id = _data.value?.get(position)?.id
        _updateCurrentTheme.value = _data.value?.get(position)
        if(id != null){
            updateUserTheme(id,requireView)
        }
    }
    //Update User ThemeID
    private fun updateUserTheme(id: String, requireView: View) {
        User.updateUserTheme(id,requireView)
    }

    //Get CurrentUser  Theme Information
    private fun getUser() {
        Themes.getTheme(themeID) { theme ->
            _currentTheme.value = theme
        }
    }
    //Get all the themes
    private fun getThemes() {
        Themes.getThemes {
            if(it !=null &&it.isNotEmpty()){
                _data.value = it
            }
        }
    }

    override fun onCleared() {
        Themes.removeValueListener()
        super.onCleared()
    }

}