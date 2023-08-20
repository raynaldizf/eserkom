package com.app.eserkom.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.eserkom.databinding.ItemListBinding
import com.app.eserkom.model.Warga

class ListDataAdapter( val dataPemilu : ArrayList<Warga>) : RecyclerView.Adapter<ListDataAdapter.ViewHolder>() {
    class ViewHolder(val binding : ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataPemilu.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNama.text = dataPemilu[position].nama
        holder.binding.txtNIK.text = dataPemilu[position].nik.toString()
        holder.binding.card.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nik", dataPemilu[position].nik.toString())
            Navigation.findNavController(it).navigate(com.app.eserkom.R.id.action_lihatDataFragment_to_detailFragment, bundle)
        }
    }

    fun setDataPemilu(listWarga: ArrayList<Warga>) {
        dataPemilu.clear()
        dataPemilu.addAll(listWarga)
        notifyDataSetChanged()
    }
}