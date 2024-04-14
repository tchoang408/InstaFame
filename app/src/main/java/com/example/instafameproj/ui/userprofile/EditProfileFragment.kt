package com.example.instafameproj.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.instafameproj.databinding.FragmentEditProfileBinding
import com.example.instafameproj.ui.UserProfileViewModel


class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding : FragmentEditProfileBinding? = null

    private val binding get() = _binding!!
    // SafeArgs plugins

    private val viewModel: UserProfileViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // XXX Write me.  viewModel should observe something
        // When it gets what it is observing, it should index into it
        // remember to call fromHtml on your string
        // You should let MainActivity know to "turn off" the swipe
        // refresh spinner.

        binding.editOkBT.setOnClickListener {
            viewModel.setUserName(binding.editName.text.toString())
            viewModel.setQuotes(binding.editQuotes.text.toString())
            findNavController().popBackStack()
        }

        binding.editCancelBt.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}