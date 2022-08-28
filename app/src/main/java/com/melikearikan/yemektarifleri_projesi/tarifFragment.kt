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
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button2.setOnClickListener {
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let {
            var gelenbilgi = tarifFragmentArgs.fromBundle(it).bilgi
            if (gelenbilgi == "menudengeldim"){
                yemekİsmiText.setText("")
                yemekMalzemeText.setText("")
                button2.visibility = View.VISIBLE
                var gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.img)
                imageView.setImageBitmap(gorselSecmeArkaPlani)
            }
            else if(gelenbilgi == "recyclerdangeldim"){
               button2.visibility = View.INVISIBLE
                val secilenId= tarifFragmentArgs.fromBundle(it).id
                context?.let {
                    try {
                        var dataBase = it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
                        var cursor = dataBase.rawQuery("SELECT * FROM yemek WHERE id = ?", arrayOf(secilenId.toString()))
                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseliIndex  = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            yemekİsmiText.setText(cursor.getString(yemekIsmiIndex))
                            yemekMalzemeText.setText(cursor.getString(yemekMalzemeIndex))
                            val byteDizisi = cursor.getBlob(yemekGorseliIndex)
                            val bitmapGorsel = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
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
        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            secilenGorsel = data.data
            try {
                context?.let {
                    if (secilenGorsel != null ){
                        if (Build.VERSION.SDK_INT >= 28) {
                            var source = ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        }else {
                            secilenBitmap =
                                MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kaydet(view: View){
        var yemekismi = yemekİsmiText.text.toString()
        var yemekmalzemesi = yemekMalzemeText.text.toString()

        if (secilenBitmap != null){
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
            val outputStream= ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            var byteDizisi = outputStream.toByteArray()

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
        var width = kullaniciniSectiğiBitmap.width
        var height = kullaniciniSectiğiBitmap.height
        val bitmepOrani : Double = width.toDouble() / height.toDouble()
        if (bitmepOrani >1){
            width = maximumBoyut
            val kisaltilmisHeight = width / bitmepOrani
            height = kisaltilmisHeight.toInt()
        }else{
            height = maximumBoyut
            val kisaltilmisWidth = height * bitmepOrani
            width = kisaltilmisWidth.toInt()
        }
       return Bitmap.createScaledBitmap(kullaniciniSectiğiBitmap,width,height,true)
    }
}


