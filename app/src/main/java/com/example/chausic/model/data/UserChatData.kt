package com.example.chausic.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserChatData (var user:UserData, var chat:ChatList): Parcelable