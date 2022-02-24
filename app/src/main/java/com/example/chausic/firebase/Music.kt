package com.example.chausic.firebase

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri
import com.example.chausic.R
import com.example.chausic.model.data.Music
import com.example.chausic.view.util.PopUpMsg
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import java.lang.Exception

object Music {
    private  var length:Int? = null
    private var mediaPlayer:MediaPlayer?= null


    //Storage for pictures
    private fun musicReference(): DatabaseReference {
        return Initialize.getDataBaseInstance()!!.getReference(Initialize.getResourceInstance().getString(R.string.MUSIC))
    }
    //Instance of music
     fun musicInstance() {
         if(mediaPlayer == null){
             mediaPlayer =  MediaPlayer().apply {
                 setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build())
         }
        }
    }
    //clear instance of current music
    fun clearInstance() {
        mediaPlayer  = null
    }
    //Play Music
    private fun playMusic(app: Application, uri: Uri) {
        try {
            mediaPlayer.apply {
            if(this!!.isPlaying){
                releaseMusic(app)
                setDataSource(app.applicationContext,uri)
                prepare()
                start()
            }else{
                setDataSource(app.applicationContext,uri)
                prepare()
                start()
            }
            setOnCompletionListener {
                releaseMusic(app)
            }
        }
        }catch (e:Exception){
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.WRONG_URL))
        }
    }

    //pause the current Music
    fun pauseMusic(app: Application){
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
            length =  mediaPlayer!!.currentPosition
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.PAUSED))
        }else{
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.NO_MUSIC))
        }
    }
    fun resumeMusic(app: Application){
        if(length != null){
            mediaPlayer!!.seekTo(length!!)
            mediaPlayer!!.start()
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.RESUME))
        }else{
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.NO_MUSIC))
        }
    }

    //Release the current Music
    fun releaseMusic(app: Application){
        if(mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
            length = null
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.STOP))
        }
        else{
            PopUpMsg.toastMsg(app.applicationContext,app.resources.getString(R.string.NO_MUSIC))
        }
    }
    //Get Music Url from database
    fun getMusicUrl(songName: String, app: Application) {
        if (songName.isNotBlank()  && songName.isNotEmpty() && mediaPlayer?.isPlaying != true) {
            musicReference().child(songName).get()
                .addOnSuccessListener {
                    if (it != null) {
                        val uri = it.getValue<Music>()
                        if (uri != null) {
                            playMusic(app, uri.uri!!.toUri())
                            PopUpMsg.toastMsg(app.applicationContext, app.resources.getString(R.string.MUSIC_PLAYING))
                        }else{
                            PopUpMsg.toastMsg(app.applicationContext, app.resources.getString(R.string.MUSIC_NOT_FOUND))
                        }
                    }else{
                        PopUpMsg.toastMsg(app.applicationContext, app.resources.getString(R.string.MUSIC_NOT_FOUND))
                    }
                }.addOnFailureListener {
                PopUpMsg.toastMsg(app.applicationContext, app.resources.getString(R.string.WRONG))
            }
        }else{
            if(mediaPlayer?.isPlaying == true){
                PopUpMsg.toastMsg(app.applicationContext, app.resources.getString(R.string.RELEASE_CURRENT))
            }else{
                PopUpMsg.toastMsg(app.applicationContext, app.resources.getString(R.string.MUSIC_NOT_FOUND))
            }
        }
    }
}