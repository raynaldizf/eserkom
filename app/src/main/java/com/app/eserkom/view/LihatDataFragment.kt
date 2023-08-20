package com.app.eserkom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.eserkom.adapter.ListDataAdapter
import com.app.eserkom.databinding.FragmentLihatDataBinding
import com.app.eserkom.datastore.WargaHelper
import com.app.eserkom.model.Warga

class LihatDataFragment : Fragment() {
    lateinit var adapter: ListDataAdapter
    lateinit var binding: FragmentLihatDataBinding
    lateinit var wargaHelper: WargaHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLihatDataBinding.inflate(inflater, container, false)
        wargaHelper = WargaHelper(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListDataAdapter(ArrayList())
        binding.rvlist.adapter = adapter
        binding.rvlist.layoutManager = LinearLayoutManager(context)

        val listWarga: ArrayList<Warga> = wargaHelper.getWarga()
        adapter.setDataPemilu(listWarga)
    }
}
