package com.example.chausic.firebase

import android.content.res.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

object Initialize {
    private var  auth  : FirebaseAuth? = null
    private var  database : FirebaseDatabase? = null
    private var  storage : FirebaseStorage? = null
    private lateinit var resource: Resources

    //Initialize Firebase Instances
    fun setupFireBase(resources: Resources) {
        if (auth == null){
            auth = Firebase.auth
            database = Firebase.database
            storage = Firebase.storage
            resource = resources
        }
    }

    fun destroyFireBase() {
        auth = null
        database = null
        storage = null
    }

    fun getAuthInstance(): FirebaseAuth? {
        return auth
    }
    fun getDataBaseInstance(): FirebaseDatabase? {
        return database
    }
    fun getStorageInstance(): FirebaseStorage? {
        return storage
    }
    fun getResourceInstance(): Resources {
        return resource
    }
}