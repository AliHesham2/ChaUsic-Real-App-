package com.example.chausic.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData (val id :String?=null,
                     var name:String?=null,
                     val img:String?=null,
                     val email:String?=null,
                     val status:Boolean?=null,
                     val token:String?=null,
                     val bio:String?=null,
                     val themeID:String?=null): Parcelable

//status  -> offline (false)   ->online(true)
