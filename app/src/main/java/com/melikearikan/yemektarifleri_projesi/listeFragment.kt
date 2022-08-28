package com.melikearikan.yemektarifleri_projesi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class listeFragment : Fragment() {

    var yemekİsmiListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : listeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = listeRecyclerAdapter(yemekİsmiListesi,yemekIdListesi)
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = listeAdapter
        sqlVeriAlma()
    }

    fun sqlVeriAlma(){
        try {
            activity?.let {
                var dataBase = it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
                var cursor = dataBase.rawQuery("SELECT * FROM yemek",null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val yemekIndIndex = cursor.getColumnIndex("id")

                yemekİsmiListesi.clear()
                yemekIdListesi.clear()

                while (cursor.moveToNext()){

                    yemekİsmiListesi.add(cursor.getString(yemekIsmiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIndIndex))
                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e: Exception){
            e.printStackTrace()

        }
    }

    }














