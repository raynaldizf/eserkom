package com.app.eserkom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.eserkom.databinding.FragmentInformasiBinding

class InformasiFragment : Fragment() {
    lateinit var binding : FragmentInformasiBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInformasiBinding.inflate(inflater, container, false)
        return binding.root
    }
}