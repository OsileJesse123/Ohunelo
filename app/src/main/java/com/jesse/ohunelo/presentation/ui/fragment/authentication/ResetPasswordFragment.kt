package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentResetPasswordBinding


class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding: FragmentResetPasswordBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}