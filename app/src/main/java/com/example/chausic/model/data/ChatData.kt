package com.example.chausic.model.data


data class ChatData(val id:String?=null,
                    val msg:String?=null,
                    val seen:Boolean?=null,
                    val senderID:String?=null,
                    val receiverID:String?=null,
                    val playedMusic:Boolean?=null)