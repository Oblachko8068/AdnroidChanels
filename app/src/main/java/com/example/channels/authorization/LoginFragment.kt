package com.example.channels.authorization

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


const val SIGN_IN_GOOD = "SIGN_IN_GOOD"
const val SIGN_UP_GOOD = "SIGN_UP_GOOD"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var inputPhone = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        initDatabase()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputView = binding.loginInput
        inputView.setText("+7")
        binding.phoneEmailButton.setOnClickListener {
            if (inputPhone) {
                inputView.hint = "Введите e-mail"
                inputView.text.clear()
                binding.phoneEmailButton.setImageResource(R.drawable.phone_icon)
            } else {
                inputView.setText("+7")
                binding.phoneEmailButton.setImageResource(R.drawable.email_icon)
            }
            inputPhone = !inputPhone
        }

        binding.nextButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.nextButton.visibility = View.INVISIBLE
            if (binding.loginInput.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Введите данные для входа", Toast.LENGTH_SHORT)
                    .show()
            } else {
                authUser()
            }
        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference().child("users")
            .child("id")
        myRef.setValue("TEST")

        parentFragmentManager.setFragmentResultListener(
            SIGN_IN_GOOD,
            viewLifecycleOwner
        ) { _, res ->
            if (res.getInt("signedIn") == 1) {
                Toast.makeText(requireContext(), "Вы вошли", Toast.LENGTH_SHORT).show()
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "пользователь не вошел", Toast.LENGTH_SHORT).show()
            }
        }
        parentFragmentManager.setFragmentResultListener(
            SIGN_UP_GOOD,
            viewLifecycleOwner
        ) { _, res ->
            if (res.getInt("signedUp") == 1) {
                Toast.makeText(requireContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show()
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "пользователь не зарегестрировался",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun authUser() {
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Log.w(TAG, "onVerificationFailed", e)
                    }

                    is FirebaseTooManyRequestsException -> {
                        Log.w(TAG, "onVerificationFailed", e)
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        Log.w(TAG, "onVerificationFailed", e)
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                resendToken = token
                launchFragment(
                    SignInFragment.newInstance(
                        verificationId,
                        binding.loginInput.text.toString()
                    )
                )
            }
        }
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(binding.loginInput.text.toString())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun launchFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}