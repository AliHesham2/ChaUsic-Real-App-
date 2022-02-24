package com.example.chausic.firebase


import com.example.chausic.R
import com.example.chausic.model.data.Theme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference


object Themes {
    private lateinit var themeEventListener: ValueEventListener

    //Get Chat DataBase reference
    private fun themesReference(): DatabaseReference? {
        return Initialize.getDataBaseInstance()?.getReference(Initialize.getResourceInstance().getString(R.string.THEMES))
    }

    //Get All Themes
     fun getThemes(theme:(themes:List<Theme>?) ->Unit){
         themeEventListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val themes = arrayListOf<Theme>()
                if(snapshot.exists()){
                    snapshot.children.map {
                        val data = it.getValue<Theme>()
                        if(data != null){
                            themes.add(data)
                        }
                    }
                    theme(themes)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
         themesReference()?.addValueEventListener(themeEventListener)
    }
    //Get User Theme
    fun getTheme(themeID:String,getTheme:(Themes:Theme) ->Unit){
        themesReference()?.child(themeID)?.get()?.addOnSuccessListener {
            if(it.exists()){
                val data = it.getValue<Theme>()
                if(data != null){
                    getTheme(data)
                }
            }
        }
    }


    fun removeValueListener(){
        themesReference()?.removeEventListener(themeEventListener)
    }

}