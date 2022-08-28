package com.melikearikan.yemektarifleri_projesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // mainActivity içerisine 2 tane fragment koyucaz.bir tanesi kaydettiğimiz yemek tariflerine listelediğimiz liste(recyclerView kullanıcaz.)
        // 2. fragment'da yemek tariflerinin detaylarını gösteren kısım olucak.
        // kaydettiğimiz yemek listesinin listeFragment da göstericez.
        // tarifFragment içerisinde yemek tariflerimizin detaylı anlatımı olucak.
        // listeyi oluşturduğum yerde birtek recyclerView olucak.recyclerView'un id'si mutlaka olmalı.
        // mainActivity içerisine navHost koyucaz yani fragmentları buraya bağlıcaz.
        // tarifFragment da 2 tane editText bir tane de yemek görselini koymak için imageView kullanmamız lazım.
        // kullanıcı görsele tıklaması gerektiğini anlaması lazım.sıfırdan bir görsel oluşturmak için kullanabileceğimiz bir sürü araç var.
        // görsel yapma aracı olarak ya da tasarım aracı olarak 'adobe xd' kullanılabilir.
        // activityler arası veri aktarımı yapabilmek için safe arguments'ı kullanıcaz(navigation kütüphanesi.)
        // navigasyon kütüphanesini ekledikten sonra res altına bir navigasyon grafiği oluşturyorduk.
        // bir menu yardımı ile tarifFragment'a ulaşmam lazım.


        /*
        ***************************** menu(obsiyon menüsü) nasıl oluşturulur? **************************************
        res sağ tık >> new >> directory(klasör) ve klasör adı : menu olsun
        * menu klasörü içinde menü olucağını android studyo anlar zaten. klasör adından anlıyor.
        * menu ' ye sağ tık >> new  >> menu resource file' a tıklıyoruz.
        * açılan pencrede root elemant menu olmalı.
        * file name adına menu adı seçmeliyiz. file name : yemek_ekle gibi
        * menu olarak yemek_ekle.xml layout dosyası oluşuyor.
        * menülerle çalışırken xml ile çalışmak daha kolaydır.
        * menüyü activity'e ekliyoruz.ve bunu yapmak için 2 tane fonksiyon çağırmamız gerkiyor.
        *
        *
         */



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInfalter = menuInflater  // bu kodu kullanarak menülerimizi istediğimiz yerlere bağlayaibiliyoruz.
        menuInfalter.inflate(R.menu.yemek_ekle,menu)
        return super.onCreateOptionsMenu(menu)
        //menüyü buraya bağlıcaz
        // options menu zaten bizim menümüz.
        // bir xml ile kodu bağlarken inflater kullanıyorduk:
        //.inflate bizden 2 tane parametre ister : 1)_menu resource'umuz , 2)_hangi menü ile bağlayacağını soruyor,bu bize zaten fonksiyon parametresi olarak verilmiş.
       // eğer sorun olursa bağlamada file >> invalidate caches ' e bas bazen android içinde bug olabiliyor ve çıkmıyor. bu android studyoyu yeniden başlatır.
        // kodlarını return üstüne yaz

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // basılan item'ın idsini kontrol ediyoruz.birden fazla item varsa karışıklık olmasın diye.
        // biz itemımızı oluştururken itemin id'sine yemek_ekleme_item veemiştik.
        //item.itemID kullanıcının tıkladoğı yerin id'si. bizim istediğimiz item'ın id'sine eşit mi bakıyorz.

        if (item.itemId == R.id.yemek_ekleme_item){
            val action = listeFragmentDirections.actionListeFragmentToTarifFragment("menudengeldim",id = 0)  //tarifFragment'a giderken bilgi ekledik.  //id göndermemize gerek yok öylesine id verdim.
            // listeFragmentDirections, navigasyonun bizim için oluşturuduğu talimatlar sınıfıydı.
            Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)
            // view içerisinde olduğumuzda 'finNavController(it)' demiştik ama şuan görünüm içinde değiliz.activity içerisindeyiz oyüzden activity'nin kendisini verebiliriz.
            // activity'nin kendisini verince bir de parametre olarak viewID istedi.
            // viewID olarak activity_main.xml içinde olan navHostFragment'ın ıd'sini vericez.
        }


        return super.onOptionsItemSelected(item)
        //option menuden bir şey seçilirse ne yapayım buraya yazılır.

    }





}