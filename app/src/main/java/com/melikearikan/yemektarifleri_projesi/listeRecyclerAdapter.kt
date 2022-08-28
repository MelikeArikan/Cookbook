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
        val view = inflater.inflate(R.layout.recyclerrow,parent,false) //parenta bağlıyorduk
        return yemekHolder(view)
        // recyclerrow xml'ini buraya bağladık.

    }

    override fun onBindViewHolder(holder: yemekHolder, position: Int) {
        //recyclerrowlar içerisine ne koyucaz yazıyoruz
        holder.itemView.recycler_row_Text.text = yemekListe.get(position)
        holder.itemView.setOnClickListener {
          // tarifFragmanına 2 tane bilgi yollicaz:
          // 1)_ yeni yemek eklemek istediğimizde de, ya da recyclerView içerisinde olan yemekİsmini üzerine tıkladığımızda da yemekle alakalı detay vermek için yine tarifFragmenta gidicez.
          // haliyle nereye tıklanarak tariffragment'a gidildi bilgisi göderilicek
          // 2)_ eğer recyclerView içindeki yemek ismine takıldıysa tıklana yemeğe ait ıd bilgisi
          // tarifFragment'a bilgi gödericeksek navigation_graphdan arguman vermen lazım.
             var action = listeFragmentDirections.actionListeFragmentToTarifFragment("recyclerdangeldim",IdList.get(position))  // neryee basarak taifFragment'a geldik onu anlamak için tarifFragment'a giderken bilgi ve seçmek istenilen yemeğin bilgilerini almak için ıd gönderiyoruz
            Navigation.findNavController(it).navigate(action)
            //tarifFragment içinde onviewVreated içinde nasıl oraya gittiğimiz bilgisini alıyorum.
        }

    }

    override fun getItemCount(): Int {
        return yemekListe.size   // yemekListesi ya da IdList ferk etmez aynı sonucu veriir.
        // recyclerrow kaç kere recyclerView içinde kullanılıcak ifade etttik

    }
}