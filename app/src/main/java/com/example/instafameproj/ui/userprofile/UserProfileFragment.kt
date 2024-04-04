package com.example.instafameproj.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.instafameproj.databinding.FragmentUserBinding
import androidx.lifecycle.Observer

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val viewModel: UserProfileViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userProfileViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        userProfileViewModel.updateUserData(
            "lydamian@gmail.com",
            "23oifajsd;fkj20392r30000"
        )

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userProfileViewModel.userIdLabel.observe(viewLifecycleOwner) {
            binding.userIdLabel.text = it
        }

        userProfileViewModel.userEmailLabel.observe(viewLifecycleOwner) {
            binding.userEmailLabel.text = it
        }

        // Observe LiveData for user data
        userProfileViewModel.userEmail.observe(viewLifecycleOwner, Observer { email ->
            binding.userEmail.text = email
        })

        userProfileViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            binding.userId.text = userId
        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}