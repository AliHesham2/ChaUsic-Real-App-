package com.example.chausic.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatList (val mixId:String?=null, val id:String?=null, var lastMsg:String?=null, val seen:Boolean?=null):
    Parcelable