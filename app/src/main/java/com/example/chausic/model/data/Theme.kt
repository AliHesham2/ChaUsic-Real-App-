package com.example.chausic.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Theme(val id:String?=null,val name:String?=null,val url:String?=null): Parcelable
