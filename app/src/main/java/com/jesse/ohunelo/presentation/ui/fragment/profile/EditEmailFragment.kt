package com.jesse.ohunelo.presentation.ui.fragment.profile

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentEditEmailBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.EditEmailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditEmailFragment : Fragment() {

    private var _binding: FragmentEditEmailBinding? = null
    private val binding: FragmentEditEmailBinding get() = _binding!!

    private val viewModel by viewModels<EditEmailViewModel>()

    private var loader: LoaderDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate<FragmentEditEmailBinding?>(inflater, R.layout.fragment_edit_email, container, false).apply {
            viewModel = this@EditEmailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = LoaderDialogFragment()

        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.apply {
            editEmailToolBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.enterEmailAddressEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onEmailTextChanged(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loader = null
        _binding = null
    }
}