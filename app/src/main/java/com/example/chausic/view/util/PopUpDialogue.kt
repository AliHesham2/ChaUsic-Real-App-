package com.example.chausic.view.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chausic.R
import com.example.chausic.firebase.Initialize
import com.google.android.material.textfield.TextInputLayout

object PopUpDialogue{
    private lateinit var  nProgressDialog : Dialog
    private lateinit var  bProgressDialog : Dialog
    private lateinit var  aProgressDialog : Dialog

    fun showNameDialogue(currentFragment: Fragment,text: String,clickListener: (text: String) -> Unit){
        nProgressDialog = Dialog(currentFragment.requireContext())
        nProgressDialog.setContentView(R.layout.custom_change_name)
        nProgressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        nProgressDialog.setCancelable(false)
        val ok = nProgressDialog.findViewById<TextView>(R.id.ok)
        val cancel = nProgressDialog.findViewById<TextView>(R.id.cancel)
        val name = nProgressDialog.findViewById<TextInputLayout>(R.id.name)
        name.editText!!.setText(text)
        ok.setOnClickListener {
            val nameText = name.editText!!.text.toString()
            if(nameText.isEmpty()){
                name.error = currentFragment.resources.getString(R.string.NAME_ERROR)
            }else{
                clickListener(name.editText!!.text.toString())
                hideNameDialogue()
            }
        }
        cancel.setOnClickListener {
            hideNameDialogue()
        }
        nProgressDialog.show()
    }

    fun showAddDialogue(currentFragment: Fragment,clickListener: (text: String) -> Unit){
        aProgressDialog = Dialog(currentFragment.requireContext())
        aProgressDialog.setContentView(R.layout.custom_add_friend)
        aProgressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        aProgressDialog.setCancelable(false)
        val ok = aProgressDialog.findViewById<TextView>(R.id.add)
        val cancel = aProgressDialog.findViewById<TextView>(R.id.cancel)
        val id = aProgressDialog.findViewById<TextInputLayout>(R.id.id)
        ok.setOnClickListener {
            if(id.editText?.text.toString().trim() == Initialize.getAuthInstance()!!.currentUser?.uid){
                PopUpMsg.toastMsg(currentFragment.requireContext(),currentFragment.resources.getString(R.string.SAME_USER_ID))
                hideAddDialogue()
            }else{
                clickListener(id.editText?.text.toString().trim())
            }
        }
        cancel.setOnClickListener {
            hideAddDialogue()
        }
        aProgressDialog.show()
    }

    fun showBioDialogue(currentFragment: Fragment,text: String,clickListener: (text: String) -> Unit){
        bProgressDialog = Dialog(currentFragment.requireContext())
        bProgressDialog.setContentView(R.layout.custom_change_bio)
        bProgressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bProgressDialog.setCancelable(false)
        val ok = bProgressDialog.findViewById<TextView>(R.id.ok)
        val cancel = bProgressDialog.findViewById<TextView>(R.id.cancel)
        val bio = bProgressDialog.findViewById<TextInputLayout>(R.id.bio)
        bio.editText!!.setText(text)
        ok.setOnClickListener {
            clickListener(bio.editText?.text.toString())
            hideBioDialogue()
        }
        cancel.setOnClickListener {
            hideBioDialogue()
        }
        bProgressDialog.show()
    }

    private fun hideNameDialogue(){
        nProgressDialog.dismiss()
    }

    private fun hideBioDialogue(){
        bProgressDialog.dismiss()
    }
     fun hideAddDialogue(){
        aProgressDialog.dismiss()
    }

}