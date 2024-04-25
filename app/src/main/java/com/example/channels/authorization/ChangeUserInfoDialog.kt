package com.example.channels.authorization

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.channels.R
import com.example.channels.USER_VIEW_MODEL
import com.example.channels.databinding.FragmentChangeUserInfoDialogBinding
import com.example.channels.viewModels.FOLDER_PROFILE_IMAGE

const val NAME = "NAME"
const val IMAGE = "IMAGE"
const val UID = "UID"

class ChangeUserInfoDialog : DialogFragment() {

    private var _binding: FragmentChangeUserInfoDialogBinding? = null
    private val binding get() = _binding!!
    override fun getTheme() = R.style.RoundedCornersDialog
    private var changedImageUri: Uri? = null
    private var changeImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            changedImageUri = it.data?.data
            changedImageUri?.let { uri -> setImageView(uri) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeUserInfoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserInfo()
        val uid = arguments?.getString(UID)

        binding.changeProfileImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }

        binding.submit.setOnClickListener {
            if (binding.changeProfileName.text.isEmpty()){
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show()
            } else {
                updateStorageImage(uid.toString())
                dismiss()
            }
        }

        binding.exit.setOnClickListener {
            USER_VIEW_MODEL.signOutFromAccount()
            requireActivity().supportFragmentManager.popBackStack()
            dismiss()
        }
    }

    private fun updateStorageImage(uid: String) {
        val path = USER_VIEW_MODEL.getStorage().child(FOLDER_PROFILE_IMAGE).child(uid)
        changedImageUri?.let { it1 -> path.putFile(it1) }
    }

    private fun setUserInfo() {
        val displayName = arguments?.getString(NAME).toString()
        val img = arguments?.getString(IMAGE)
        val parts = displayName.split(" ")
        val name = parts[0]
        val sirname = parts[1]
        binding.changeProfileName.setText(name)
        binding.changeProfileSirname.setText(sirname)
        if (img != "" && img != null){
            setImageView(img)
        }
    }

    private fun setImageView(image: Any) {
        Glide.with(requireContext())
            .load(image)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.profilePic)
    }

    companion object{
        fun newInstance(displayName: String, image: String, id: String): ChangeUserInfoDialog {
            return ChangeUserInfoDialog().apply {
                arguments = Bundle().apply {
                    putString(NAME, displayName)
                    putString(IMAGE, image)
                    putString(UID, id)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}