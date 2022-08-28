package com.melikearikan.yemektarifleri_projesi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class tarifFragment : Fragment() {

    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // eğerki butona bir fonksiyon atamak istiyorsan bunu setOnClickListener{} ile yap.
        // onViewCreated tüm görünümler oluşturulduktan sonra yapılacakların yazıldığı fonksiyondur.
        button2.setOnClickListener {
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselSec(it)
        }


        // buraya neye basarak geldik bilgisi:
        arguments?.let {
            //argument geldi mi ona bakıyorum
            var gelenbilgi = tarifFragmentArgs.fromBundle(it).bilgi // bu bana gelen stringi verir
            if (gelenbilgi == "menudengeldim"){
                // gelenbilgi == "menudengeldim" ile aynı anlamı taşır.
                // yeni bir yemek eklmeeye geldi. zaten kodlarını ve görünümünü yaptık her şey hazır.
                yemekİsmiText.setText("")
                yemekMalzemeText.setText("")
                // BUTONU GÖRÜNÜR TUTUCAZ:
                button2.visibility = View.VISIBLE
                var gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.img)
                imageView.setImageBitmap(gorselSecmeArkaPlani)  // böylece menuden gelirsek ki görünümlerle ilgili olan kodları buraya yazdık.


            }
            else if(gelenbilgi == "recyclerdangeldim"){
               // daha öncce oluşturulan yemeği görmeye geldi
               // butonu görünür ya da görünmez yapabiliriz.:
               button2.visibility = View.INVISIBLE  //VİSİBİLİTY: GÖRÜNÜRLÜK, İNVİSİBLE= GÖRÜNMEZ
                val secilenId= tarifFragmentArgs.fromBundle(it).id //gelen idyi aldık
                context?.let {
                    try {
                        var dataBase = it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
                        var cursor = dataBase.rawQuery("SELECT * FROM yemek WHERE id = ?", arrayOf(secilenId.toString()))  // seçilen ıd değişkende olduğu için ? koyuyorum, seçilen yemeğin datasını çekebiliriz
                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseliIndex  = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            yemekİsmiText.setText(cursor.getString(yemekIsmiIndex))
                            yemekMalzemeText.setText(cursor.getString(yemekMalzemeIndex))
                            val byteDizisi = cursor.getBlob(yemekGorseliIndex)  // byte dizisi gorselim
                            val bitmapGorsel = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size) // gorseli bitmap yaptık.3 tane parmetre ister. 1. hangi gorseli bitmap yapıcan,2.offset:? 3.byteDizisi içinde ne kadar veri var  onu soruyor. byteArrayi decode ediyoruz.
                            imageView.setImageBitmap(bitmapGorsel)

                        }
                        cursor.close()

                    }catch (e:Exception){
                        e.printStackTrace()

                    }
                }

            }

        }



        }





    fun gorselSec(view: View) {
        // setOnClickListener kullanılmazsa bu fonksiyonlarda kullanılmıyor bu fragmentlara özgü bir yapıdır.
        // setOnClickListener'a ek olarak bu şekilde yazmamızın nedeni düzenli olması için. bu şekilde eksta bir fonskiyon yazmak zorunda değilim sadece setOnClickListener yapsam da çalışır
        // ama sadece fonksiyon oluştursam yanlış olur. fragamanlarda 3


        // ***************************** kullanıcın uygulamaya görsel eklemesi için : ****************************************************
        // kullanıcının uygulamaya görsel ekleyebilmesi için, bizim kullanıcının iznini almalıyız.
        // kullanıcının galerisine giderken aslında kullanıcının özeline giriyoruz.
        // bu izinlerin 2 tipi var = bazılarını manifest dosyasına yazıp geçebiliyoruz. bazılarında ise manifest'e yazmak yeterli olmuyor açık açık sormak gerekiyor.
        // hangi iznin ne olduğunu ve nasıl sormamız gerektiğini nereden bilicez?
        // developer.android.com ' a git ve 'permissions' (izinler)diye aratıyorum.
        // manifestte hangi izinlerin kullanıldığı aslında uygulama indirilirken kullanıcıya bildiriliyor.
        // bazı izinler için kullanıcıya soru sormaya gerek yok, kullanıcı bu uygulamayı indiriyorsa o izinleri kabul ediyor diyebiliriz.ama bazıları için ekstra soru sormaya gerek var.
        // bu ekstra soru sorma olayı androidin belirlediği, kullanıcının özeline girip girmemeye göre değişiyor ve belirleniyor.
        // örnek : internet izni çok yaygın ve sadece manifeste yazıp geçebiliriz.kullanıcıya ben senin internetini kullanıyorum izin veriyor musun diye sormana gerek yok.
        // kullanıcının galerisine yada konumuna ulaşacaksak kullanıcıya açık açık sormamız gerekiyor kullanıcıya ben senin fotoğraflarını görücem okey misin? ya da ben senin yerini bilicem okey misin? .
        // android izinleri 2'ye ayırıyor = tehlikeli izinler ve normal izinler
        // https://developer.android.com/reference/android/Manifest.permission burda tüm izin çeşitlerini görebildiğmiz dökümantasyonlar var ve izin içerilerini nasıl izni kullanacağımızı burdan görebiliriz. örnek :
        // access_media_location :  medyaların saklandığı yere ulaş
        // access_network_state : kablolu internete ulaş
        // answer_phone_calls : gelen telefonları cevaplama izni
        // internet izninin protection level'ı : normal yani bunu manifeste yazıp geçememiz yeterli,kullanıcı uygulamayı inderirken bu izni kabul ediyor ve uygulama içinde internet kullanılabiliyor.
        // read_external_storage : dış hafızayı okuma izni  - protection level : dangerous tehlikeli izinlerde mutlaka kullanıcıya sormamız gerekiyor.
        // read_external_storage izini apı level 19 dan sonra dangerous haline getirilmiş.19'dan önceki telefonlarda bunu sormaya gerek yok,fakat 19'dan sonraki telefonlarda bunu sormaya gerek var gibi detaylar var.
        // yani 19dan önceki  ve sonraki telefonlarda çalışan bir kod yazmamız gerekiyor ki kodlarımız düzgün çalışsın.
        // access_fine_location izni :  yani kullanıcının yerini bulma izni de dangerous'dur.
        // bu izinlerin kendine has durumları ve istisnaları var.
        // izinlerimizi tüm telefonlarda çalışacak şekilde nasıl yazarız ve kullanıcıdan read_external_storge iznini nasıl isteriz onu görücez.
        // manifeste gidip daha application kısmı başlamadan <uses-permission tagi açıyoruz ve istediğimiz izni seçiyoruz. eğer bu izin internet izni olsaydı yeterli olucaktı ama harici depolamayı okumak için manifeste yazmak yetersiz.

        // ******************** izin nasıl sorulur? ************************************************************
        // bu izin sadece 1 kere sorulucak. kullanıcı 1 kere izin verince biz onun galerisini kullanmaya devam edicez.
        // o sebeple bu izin önceden verilmiş mi kontolü yapmamız lazım çünkü her uygulama çalıştığında baştan izin verilmicek.
        // kullanıcı izni kontrol edilirken checkSelfpermission ile kontol edilir. checkSelfPermissin ile tüm izinleri kontrol edebilirz.
        // checkSelfPermissin aktivityde çağırılır, burada direkt çağıramam.
        //  burada çağırabilmem için konteksten çağırmam lazım yadda activity'den
        // checkSelfPermissin'ı biz ContextCompat'den çağırıcaz.çünkü compatiable yani uyumlu olması yani apı19 ve öncesinde mi çalışıscak 19 ve sonrasında mı çalışıcak? bunlarla uğraşmamak için contextCompatten çağırıyorum.
        // activity içerinde checkSelfPermissin'a direkt ulaşabilmemize rağmen yine de uyumluluk açısından daha iyi olduğu için ContextCompat'ten çağırıcaz.


        activity?.let {
            // bu kod bloğu activity varsa var,yoksa yok anlamına gelir.
            // bunu koymasaydık acitivity var mı yok mu bilmediği için sorun yapıyordu.

            if (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // seçili izin verilmediyse anlamına gelir. // izin verilmedi , izin istememiz gerekiyor.
                // context alınırken fragmentActivity'den uygulama context'i alıyoruz.
                // versiyonlar arasındaki uyumsuzluğu gidermek için ContextCompat kullandık.
                // checkSelfPermissin bizden 2 tane parametre ister: 1)_contxt 2)_permission yani hangi izni kontrol ediceğini istiyor.
                // 2. parametrede Manifest ile başlar ve 2 manifest içinden android olan seçilir.
                // PERMISSION_DENIED = İZİN VERİLMEDİ DEMEK
                // PERMISSION_GRANTED = İZİN VERİLDİ DEMEK
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                //requestPermission'ı çağırdığımda parametre olarak  bizden : bir izin dizisi istiyor.dizi oluşturucaz ve içerisine isteidğimiz izinleri yazıcaz.
                // dizi içerisinde birden fazla izin sorabiliriz.
                // requestPermissions 2.parametre olarak requestCode istiyor. yani istek kodu, bu kod bizim istediğimiz bir integer olur.bu kod sonradan bu izni kontrol etmek istediğimde kullancağım bir kod.
                // hangi izine hangi kodu yazdığımızı bilmeliyiz.
            } else {
                // izin zaten verilmiş tekrar istemeden galeriye git işlemi burda yapılır.
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // intent içine bir aksiyon koyucaz.
                // Intent.ACTION yazarsan intentle yapabileceğin aksiyonları görebilirsin
                // ACTION.PICK : bir şey - bir görsel alıcaz.
                // virgül koyduktan sonra nereden görsel alacağımızı ifade diyoruz. bizden URI istiyor.
                // urI nedir: bir şeyin konumunu belirten bir yapıdır. alıcağımız görsellin nerede durduğunu-galerinin yerini- belirten bir yapıdır.
                // geleriye : MediaStore.Images.Media.EXTERNAL_CONTENT_URI böyle gidiyoruz, burada görsel alıcaz.

                startActivityForResult(galeriIntent, 2)
                // sonunda geriye bir şey döndürüceğimiz için startActivityForResult ifadesinin kullanıyıruz.döndürüceğimiz şey geriye bir görsel olucak
                // yani bir sonuç için bu intenti başlatıyoruz biz.
                // startActivityForResult bizden 2 tane parametre ister :1)_intent 2)_requestCode

            }
            // her iki aksiyonunda(if else bloklarınında ) sonuçlarının değerlendirilmesi lazım yani adam galeriye gidiyor,seçti görseli sonra ne olucağını yazmamız lazım
            // ya da izin istedik verdi ne olucak yazmamız laızm, ya da izin vermedi ne yapıcaz sonucunda yaz mesela kullanıcıya izin vermesi gerektiğini ifade eden uyarı mesajı yazabilirisni.

        }
    }
    // fragmanımın içinde tüm fonksiyonların dışındayız.

    override fun onRequestPermissionsResult(
        //OnRequestPermissionsResult   istenilen izinlerin sonuçları olan bir fonksiyondur,hazır bir fonksiyondur.biz bunu override ediyruz yani üstüne yazıyoruz .
        // yapacağımız uygulamada bir sürü izin isteyebiliriz ve hepsi farklı yerdede istenebilir.ama sadece bir tane onRequestPermissinResult fonksiyonu olucağı için..
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1){
            // istenilen grantResult parametresi verilen sonuçlar anlamında kulllanılır.bu bir intArray'i veriyormuş bana.
            if (grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){// geriye bir şey döndü mü bir sonucum var mı ona bakarım ve sonuçlar izin verildiyse kontorlünü yaparız.
                // grantResult'ın içinde kullanıcının izin sormamıza verdiği cevap var. kullanıcı izine bir karşılık verdiyse ve kulanıcının verdiği cevaplardan 0. indeksli cevap onay cevabıysa anlamına gelir
                // izini aldık. ve galeriye gidicez
                // grandResultın 0. indeksi galiba ilk yazdığımız request ile ilgili.
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
                // yukarıda kullanığımız galeriye git kodlarını kopyala yapıştır yaptık zaten requestCode'ların da aynı olması gerekiyordu.
            }

            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // galeriye gidilince ne olucağını ele almamız gerekiyr.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // StartActivityForResult dediğimiz yerin sonuçalrını ele alıyoruz yani galeriye gidildiğinde ne yapılsın.
        // requestCode istek kodudur, resultCode cevap codudur, yanlış yazma
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            // result ok demek kullanıcı işlemi iptal etmedi işleme devam ediyor demek.
            // KULLANICI GALERİYE GİDİP SONRADAN VAZGEÇEBİLİR, RESULT_OK derse galeriden bir şey seçerse bizde bunun sonucunda bir şey yapmak istiyoruz.
            // eğer kullanıcı galeriye gidip resim seçmekten vazgeçerse bir şey yapmıcaz.
             // bu data seçilen görselin nerede durduğu yani  görselin urı'nı bana geri verilecek
            // artık ben seççilen görselin urı'ına ulaşabilirim.
            // urı'ı nasıl alıcaz,bunu fragmanın en başında tanımlıyorum çünk başka yerlerdede kullanıcam
            secilenGorsel = data.data // data.data bana bir uri nullable verir. zaten seçilenGorsel de bir uri nullable. böylece seçilen Görselin telefonda nerede durduğunu almış oldum.
            // görseli işlemem lazım, seçilen yerden görseli bitmap'e çavirmem lazım ki bunu iamjeView içerisinde bunu koyabilim

            try {
                context?.let {
                    //activity içerinde olsaydı context'i this ile hemen alabilirdik context? yapmaya gerek kalmazdı.
                    // datanı null olmaması şartını yukarıda bakmıştık. data null olamyıp seçilne görsel urı bir şekilde null olabilir.kontrol ediyoruz.
                    if (secilenGorsel != null ){
                        // uri'ı bitmap'e çeviricez.
                        if (Build.VERSION.SDK_INT >= 28) {
                        // yani güncel kullandığım telefonumun sdk miktarı 28 ve büyükse anlamına gelir.
                            var source = ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            //createResource bize hata verir çünkü createResource'u en düşük apı 28 de kullanabilrisin.yani api seviyesi 28 ve altında olan telefonlardan bu çalışmaz.
                            // o yüzden api 28 ve üstünde  misin onu kontrol et .
                            // bitmap oluştrucaz ama 2 tane ayrı bitmap oluşturmamak için bitmap'i üstte olşturuyorum.
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            // decodeBitmap parametre olarak  benden kaynak istiyor. neyi bitmap yapacağını bilmeli.
                            // görsel çözümleyici ile görseli bitmap haline getiriyorum
                            imageView.setImageBitmap(secilenBitmap)

                        }else {
                            //sdk 28'den küçükse bitmap'i başka şekilde alıcaz.
                            secilenBitmap= MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                                    // secilenBitmap'i direkt MediaStore'dan alıyorum
                            // metodun üstünün çizik olması demek artık bunu kullanmayın başka metodlar yaptık onları kullanın demek,kullanmaya devam etsen çok da problem olmaz.
                            // getBitmap parametre olarak contentResolver istiyor.2.parametre olarak urı istiyor.
                            //MediaStore.Images.Media galeriye erişmek için olan kodlardı zaten . galerideki urı'i belirtilen görseli alıp, içeriğini çözümlüyor.
                            imageView.setImageBitmap(secilenBitmap)
                        }

                    }

                }



               // neden try and catch içinde yapıyoruz :
                // dosyayı almadık biz sadece konumunu aldık.o konumdan dosyaya çevirmeye çalışırken bir çok hatayla karşılaşabilriz ör:
                // görsel silinir,sd kart bozulur. bu sorunlarda uygulamamızın çökmemesi gerekir.
                // bitmape çevirme işlemi yapıcaz:

                // createSource bizim için bir kaynak oluşturur. bu kaynağı uri'dan oluşturucak (başka şeylerden de oluşturabilir-dosya- gibi)
                // paramtre olarak :URI ile çalışan yerde contentResolver yani içerik çözümleyici diye bir şey istiyor.bizim contentResolver-içerik çözümleyicimiz- yok.
                // contentResolver almak: context.contentResolver  ifadesi ile uygulamamın contentResolverı bana getirilecektir.
                // uygulamada context vaar mı yok mu bilemediği için bana sorun çıkardı ben de context varsa var, yoksa yok anlamına gelicek context?.let{} bloğunu kullandım ve it kullandım
                // 2. parametre olarak seçilen görselin uri'ını veriyorum
                // secilenGorselin altını kızarttı çünkü sistem uri istiyor biz uri nullable veiyorz.
                // context yazdıktan sonra noktayı kızarttı çünkü context var mı yok mu belli değil ya soru işareti koy ya 2 tane  !! koy.
                // bu tarz sıkıntıları yaşamamak için bir kaç tane kontrol yapabliriz
                // seçilenGorselin null gelme durumunu kontrol ediyoruz.
                // context varsa var anlamına gelen context.let komutunu kullandık ve contexti otomatik olarak it içine atadık. bunu yapmazsak context.(it) yazdığımız yerde problem yaratıyor.

            }catch (e:Exception){
                e.printStackTrace() // dersem sorunları bana yazdırıcaktır.
            }

        }

        super.onActivityResult(requestCode, resultCode, data)


    }





    fun kaydet(view: View){
        var yemekismi = yemekİsmiText.text.toString()
        var yemekmalzemesi = yemekMalzemeText.text.toString()
        // SQLite' a kaydetme işlemi yapıcaz
        // sqlite da kaydediceğimiz verilerin 1 megabyte'dan büyük olması bize sorun çıkartır.
        // kullanıcın kamerası  iyiyse çektiği foto 1 mB 'dan büyük olabilir.
        // biz görselin küçük boyutlu olduğundan emin olmalıyız.o yuzden çekilen görseli, Bitmap kullanarak daha küçüklü bir bitmap haline getirmemiz gerekiyor.ondan sonra sqlite'a kaydedicez.
        // bitmap küçültmek için farklı yöntemler var biz kendi fonksiyonumuzu yazıcaz ve bundan sonra yazıcağımız her uygulama da bitmap küültmek içn bu fonksiyonu yazabiliriz.ve bunu sadece sqlite da yapmaa gererk yok cloud sunucuya görsel kelerken de aynı şekilde küçültebiliriz.

        // bitmap'i küçültmek için en altta ayrı br fonksiyon yazıcaz.
        if (secilenBitmap != null){
            // secilenBitmap normalde nullable, null gelmiceğini kesineştirmek için null olmamasını kontrool ediyorum
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)  // max boyutu deneye yanıla yapıcaz, en iyi sonucu alınca dur.
            // görseli son olarak veriye çevirmeiliyiz çünkü-herhangi bir - veritabanına görsel kaydederken, görseli bitmep,jpg gibi formatlarda kaydedemezsin.görseli veriye çevirip o şekilde kaydedersin

            // ********************* bitmap'i veriye çevirmek için olan kodlar : *************************************
            val outputStream= ByteArrayOutputStream()
            // görsel büyük  bir şey biz bunu veriye çevirince bu veri dizisi haline geliyor.
            // ByteArrayOutputStream'de görsellerimizi veriye çevirmemize yardımcı olan sınıf. bu sınıftan outputStream adında nesne oluşturduk.

             kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)   //compress sıkıştırma demek
            // compress bizden 3 tane parametre ister : 1)_hangi formatta çevirme yapacağımızı istiyor.PNG ya da JPEG-ya da naşka formatlarda - formatında yapabiliyoruz.
            // 2). paramtre : QUALİTY. quality 0 ve 100 arasında değişen bir rakam. sıkıştırma işleminde bir kalite seçiyoruz.
            // 3. parametre : ouputStream

            // bunu byte dizisi haline getirmemiz gerkiyor:
            var byteDizisi = outputStream.toByteArray()  // veriye çeirme kodları bitti.

            // görsel verisini geri sqlite'dan alırken veriyi bu sefer bitmap'e çeviricez.
            //  sqlite'a kaydedilmesi gerekenler veriler : byteDizisi,yemek_ismi,yemek_malzemeleri
            // veri tabanı işlemleirmizi try and catch yapısı içinde yaıyoruz.

            try {
                context?.let {
                        var dataBase = it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
                        dataBase.execSQL("CREATE TABLE IF NOT EXISTS yemek (id INTEGER PRIMARY KEY,yemekismi VARCHAR,yemekmalzemesi VARCHAR,gorsel BLOB)")

                        val sqlString = "INSERT INTO yemek(yemekismi,yemekmalzemesi,gorsel) VALUES (?, ?, ?)"
                        val statement = dataBase.compileStatement(sqlString)
                        statement.bindString(1,yemekismi)
                        statement.bindString(2,yemekmalzemesi)
                        statement.bindBlob(3,byteDizisi)
                        statement.execute()



                }

            }catch (e: Exception){
                e.printStackTrace()

            }
            val action = tarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)







        }

    }

    fun kucukBitmapOlustur(kullaniciniSectiğiBitmap :Bitmap , maximumBoyut : Int) :Bitmap{
        var width = kullaniciniSectiğiBitmap.width    // bu kod ile seçilen bitmain güncel genişliğini alabilirsin
        var height = kullaniciniSectiğiBitmap.height // kullanıcının seçtiği bitmap'in güncel uzunluğunu aldık.
        // createScaledBitmap içine height/2 ve weight / 2 yazarsan güncel boyutu küçültür ve fonksiyon tamamalabilir ama hala yeteri kadar küçültmeyebilir.
        //  biz belli bir oranda hem weight hem height'ı düşürmek ve 1 mB altına görsel boyutunu küçültmek istiyoruz.
        // küçültme olayını yaarken boy ve genişliği biriibirne oranlı küçültmek istiyoruz.
        // weight'ı yüzde 30 azaltıyorsak ve height'ı da yüzde 30 azaltmamız gerekir.bu yüzden weight ve heightin oranını bulucam ve kendime ideal oran çıkartıcam:
        val bitmepOrani : Double = width.toDouble() / height.toDouble()   // bolme sonucu den büyükse görsel yatay,  den küçükse görsel dikey demektir
        if (bitmepOrani >1){
            // görssel yatay
            width = maximumBoyut  // kısaltılmış genişlik oldu
            val kisaltilmisHeight = width / bitmepOrani  // boy ve genişlik oranı bitmepOranı kadardı o yzüden de kısaltılmış uzunluğu bulabilmek için kısalılmış genişliğini orana böldüm
            height = kisaltilmisHeight.toInt()

        }else{
            //görel dikey
            height = maximumBoyut
            val kisaltilmisWidth = height * bitmepOrani // çarpı koymamızın nednei oran1/2 height'1 1/2 ye bölmek demek 2 ile çarpmak demektir.
            width = kisaltilmisWidth.toInt()


        }
        // bu fonksiyon için 2 tane parametre istiyorum.
       return Bitmap.createScaledBitmap(kullaniciniSectiğiBitmap,width,height,true)
        // bu fonksiyonu bundan sonra heryerded kullanabilim diye uzunluk ve genişlik değerini sabit şekilde vermiyoruz.
        // maximun boyutu kendi uygulamalarına göre değiştirebilirsin.

        // createScaledBitmap, bitmap'i alıp daha küçük bir bitmap oluşturmayı sağlıyor.paramtre olarak src:Bitmap alıyor. aldığı bitmap'i küçültücek
        // createScaledBitmap 4 tane parametre ister: 1)_kaynak Bitmap (hangi bitmapi küçültücek)
        // 2)_width : genişlik , 3)_ height:uzunluk
        // 4)_ filter: bütün bu işlemleri yaparken uygulanacak filter aslında
    }}



/*  **********************************bu projeden öğrendiklerim *********************************
 1)_ imageView'larında onClick metodları vardır. ve görselin üzerine tıklayarak buton gibi istediğin fonksionu yazabilirsin.
 2)_ imageView için paintten istediğini yapıp koyalayıp drawable içine yapıştırıp onu da imageView olarak kullanabilirsin.
 */

/*
emülatör galerisine nasıl resim kaydedilir:
google'a girip görseller kısmından seçtiğimiz görsele uzun basarsan dawnload seçeneği çıkar ve direkt telefonunun galerisinde download dosyasının içine kaydedilir.
 */