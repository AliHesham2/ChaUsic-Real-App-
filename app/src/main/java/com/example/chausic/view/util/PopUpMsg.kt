package com.example.chausic.view.util

import android.Manifest
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.chausic.R
import com.example.chausic.view.dashboard.profile.ProfileFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.lang.Exception

class PopUpMsg {

    companion object{

        const val BASE_URL = "https://fcm.googleapis.com/"
        const val KEY = "AAAAzRQJRNY:APA91bFowRiulPsk7K6okmG2RUg84tgLhjj4p6YHmtluGVqwVvzylanVQFRmnZPKAr_breP0zyIzezcDGERZk6utp7N-ughRaMhO7_-O6vBx81z5YVnYI_P8CAiRDJAIO95dSGeN6Q63"
         const val NOTIFICATION_ID = 0

        private lateinit var  mProgressDialog : Dialog

        fun alertMsg(view: View, msg: String){
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).also { snackBar ->
                snackBar.setBackgroundTint(ContextCompat.getColor(view.context, R.color.red))
            }.show()
        }


        fun toastMsg(context: Context, msg:String){
            Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
        }

        //Copy ID
         fun textCopy(text: String, requireActivity: FragmentActivity, requireContext: Context, resources: Resources){
            val clipboard: ClipboardManager? = requireActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("label", text)
            clipboard!!.setPrimaryClip(clip)
            toastMsg(requireContext,resources.getString(R.string.COPIED))
        }

        fun showDialogue(context: Context){
            mProgressDialog = Dialog(context)
            mProgressDialog.setContentView(R.layout.loading)
            mProgressDialog.setCancelable(false)
            mProgressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mProgressDialog.show()
        }
        fun hideDialogue(){
            mProgressDialog.dismiss()
        }

        fun showAlertDialogue(context: Context,Msg:String,isOK:(ok:Boolean) -> Unit){
            MaterialAlertDialogBuilder(context)
                .setMessage(Msg)
                .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
                    isOK(true)
                    dialog.dismiss()
                }
                .show()
        }

         fun checkPermissions(currentFragment: ProfileFragment,isSuccess:(upload:Boolean) -> Unit){
            Dexter.withContext(currentFragment.requireContext())
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            isSuccess(true)
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            isSuccess(false)
                            toastMsg(currentFragment.requireContext(),currentFragment.resources.getString(R.string.DENIED))
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        isSuccess(false)
                        toastMsg(currentFragment.requireContext(),currentFragment.resources.getString(R.string.ENABLE_GALLERY))
                    }
                }).onSameThread()
                .check()
        }

         fun fireBaseError(requireView: View, exception: Exception) {
             if(exception is FirebaseNetworkException){
                 alertMsg(requireView, requireView.resources.getString(R.string.NO_INTERNET))
             }else {
                 when ((exception as FirebaseAuthException).errorCode) {
                     "ERROR_USER_NOT_FOUND" -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.error_login_user_not_found)
                     )
                     "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.error_login_accounts_exits_with_different_credential)
                     )
                     "ERROR_INVALID_EMAIL" -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.error_login_invalid_email)
                     )
                     "ERROR_WRONG_PASSWORD" -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.error_login_wrong_password)
                     )
                     "ERROR_EMAIL_ALREADY_IN_USE" -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.error_login_email_already_in_use)
                     )
                     "ERROR_WEAK_PASSWORD" -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.error_login_password_is_weak)
                     )
                     else -> alertMsg(
                         requireView,
                         requireView.resources.getString(R.string.AUTH_FAIL)
                     )
                 }
             }
        }
    }
}