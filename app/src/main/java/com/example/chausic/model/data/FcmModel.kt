package com.example.chausic.model.data

data class FcmModel(val to:String,val notification:FcmDetail,val data:FcmData)
data class FcmDetail(val body:String,val title:String)
data class FcmData(val userID:String?=null,val mixID:String?=null,val type:String)