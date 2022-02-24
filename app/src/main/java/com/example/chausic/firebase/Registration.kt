package com.example.chausic.firebase



import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.chausic.model.data.UserData
import com.example.chausic.view.util.PopUpMsg



object Registration {

    //SignIn
    fun sendSignInRequest(email: String, password: String, requireActivity: FragmentActivity, requireView: View, loginSuccess : (isLogin:Boolean)-> Unit ){
        Initialize.getAuthInstance()!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity) { task ->
                if (task.isSuccessful) {
                    val currentUser = Initialize.getAuthInstance()!!.currentUser
                    User.setCurrentUser(currentUser)
                    loginSuccess(true)
                }
            }.addOnFailureListener {
                loginSuccess(false)
                PopUpMsg.fireBaseError(requireView,it)
            }
    }

    //SignUp
    fun sendSignUpRequest(email: String, password: String, name: String, requireActivity: FragmentActivity, requireView: View,SignUpSuccess : (isSignUp:Boolean)-> Unit) {
        Initialize.getAuthInstance()!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity) { task ->
                if (task.isSuccessful) {
                    val currentUser = Initialize.getAuthInstance()!!.currentUser
                    User.setCurrentUser(currentUser)
                        User.setupUser(UserData(currentUser!!.uid, name, "", email,status = false, "", "","1"), requireView) { isUserAdded ->
                            //If User Added to DB then Pass to DashBoardActivity
                            SignUpSuccess(isUserAdded)
                        }
                }
            }.addOnFailureListener {
                //If  Error in Auth set SignUp False
                SignUpSuccess(false)
                PopUpMsg.fireBaseError(requireView,it)
            }
    }
}
