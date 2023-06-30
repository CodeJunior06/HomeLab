package com.uts.homelab.view.user.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.uts.homelab.databinding.FragmentOptionBinding
import com.uts.homelab.viewmodel.OptionUserViewModel

class OptionUserFragment : Fragment() {

    private var _binding: FragmentOptionBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val optionUserViewModel =
            ViewModelProvider(this).get(OptionUserViewModel::class.java)

        _binding = FragmentOptionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        optionUserViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}