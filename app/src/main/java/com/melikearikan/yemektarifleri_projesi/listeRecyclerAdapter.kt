package com.melikearikan.yemektarifleri_projesi

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scaleMatrix
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerrow.view.*

class listeRecyclerAdapter(val yemekListe : ArrayList<String>,val IdList :ArrayList<Int>) : RecyclerView.Adapter<listeRecyclerAdapter.yemekHolder>() {
    class yemekHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): yemekHolder {
       val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyclerrow,parent,false)
        return yemekHolder(view)
    }

    override fun onBindViewHolder(holder: yemekHolder, position: Int) {
        holder.itemView.recycler_row_Text.text = yemekListe.get(position)
        holder.itemView.setOnClickListener {
             var action = listeFragmentDirections.actionListeFragmentToTarifFragment("recyclerdangeldim",IdList.get(position))
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return yemekListe.size

    }
}