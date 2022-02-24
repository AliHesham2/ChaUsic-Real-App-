package com.example.chausic.view.dashboard.profile


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.chausic.databinding.FragmentProfileBinding
import com.example.chausic.view.util.PopUpMsg
import com.example.chausic.view.util.PopUpDialogue
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chausic.R
import com.example.chausic.view.util.URIPathHelper


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialization
        binding = FragmentProfileBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val viewModelFactory = ProfileFragmentViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[ProfileFragmentViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.upload.observe(this.viewLifecycleOwner) {
            if (it == true) {
                PopUpMsg.showDialogue(this.requireContext())
            } else {
                PopUpMsg.hideDialogue()
            }
        }

        //ClickListeners
        binding.uploadImage.setOnClickListener {
           PopUpMsg.checkPermissions(this){ upload ->
               if(upload){
                   val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                   intent.type = "image/*"
                   intent.putExtra("crop", "true")
                   intent.putExtra("aspectX", 1)
                   intent.putExtra("aspectY", 1)
                   intent.putExtra("return-data", true)
                   resultLauncher.launch(intent)
               }
           }
        }



        binding.toCopy.setOnClickListener {
           val copiedText =  binding.id.text.toString()
            if(copiedText.isNotEmpty()){
                PopUpMsg.textCopy(copiedText,this.requireActivity(),this.requireContext(),this.resources)
            }else{
                PopUpMsg.toastMsg(this.requireContext(),this.resources.getString(R.string.NO_COPIED))
            }
        }

        binding.constraintLayout2.setOnClickListener {
            PopUpDialogue.showNameDialogue(this,binding.name.text.toString()){
                //new name text
                viewModel.getUserNameToBeUpdated(it,this.requireView())
            }
        }

        binding.constraintLayout3.setOnClickListener {
            PopUpDialogue.showBioDialogue(this,binding.about.text.toString()){
                //new bio text
                viewModel.getUserBioToBeUpdated(it,this.requireView())
            }
        }


        return binding.root
    }

    // Image data result
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data != null){
                val  filepath  = URIPathHelper.getPath(this.requireContext(),data.data!!)
                viewModel.getUserPictureToBeUpdated(filepath,this.requireView())
            }else{
                PopUpMsg.toastMsg(this.requireContext(),this.resources.getString(R.string.FAILED_UPLOAD_IMG))
            }
        }
    }



}