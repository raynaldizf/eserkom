package com.app.eserkom.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.eserkom.databinding.FragmentDetailBinding
import com.app.eserkom.datastore.WargaHelper

class DetailFragment : Fragment() {
    lateinit var dbHelper : WargaHelper
    lateinit var binding : FragmentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = WargaHelper(requireContext())
        val nik = arguments?.getString("nik")

        if (nik != null) {
            val warga = dbHelper.getWargaByNik(nik)
            warga?.let {
                binding.textViewNIK.text = "NIK : ${it.nik}"
                binding.textViewNama.text = "Nama : ${it.nama}"
                binding.textViewNomorHP.text = "Nomor HP : ${it.nomorHandphone}"
                binding.textViewJenisKelamin.text = "Jenis Kelamin : ${it.jenisKelamin}"
                binding.textViewTanggal.text = "Tanggal Pendataan : ${it.tanggalPendataan}"
                binding.textViewAlamat.text = "Lokasi : ${it.alamat}"
                binding.imageViewGambar.setImageBitmap(BitmapFactory.decodeFile(it.gambar))
                Log.d("DetailFragment", "onViewCreated: ${it.gambar}")
                Log.d("DetailFragment", "onViewCreated: ${it.alamat}")
            }
        }
    }
}