package com.example.firebasefotografpaylasma

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_fotograf_paylas.*
import java.util.*

class FotografPaylasActivity : AppCompatActivity() {
    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null
    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotograf_paylas)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }
    fun paylas(view: View){
        //depo işlemleri
        val uudi = UUID.randomUUID()
        val gorselIsmi = "${uudi}.jpg"
        val referance = storage.reference
        val gorselReference=referance.child("images").child(gorselIsmi)
        if (secilenGorsel != null){
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener{taskSnapshot ->
            val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val guncelKullanıcıEmail = auth.currentUser!!.email.toString()
                    val kullaniciYorumu = yorumText.text.toString()
                    val tarih = Timestamp.now()
                //veritabanı işlemleri
                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("gorselurl",downloadUrl)
                    postHashMap.put("kullaniciyorum",kullaniciYorumu)
                    postHashMap.put("kullaniciemail",guncelKullanıcıEmail)
                    postHashMap.put("tarih",tarih)
                    database.collection("Post").add(postHashMap).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener { exaption ->
                        Toast.makeText(applicationContext,exaption.localizedMessage,Toast.LENGTH_LONG)

                    }

                }

            }
        }
    }
    fun gorselSec (view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //izin istenmemiş
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else {
            val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){
            if (grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==2 && resultCode == Activity.RESULT_OK && data!= null){
           secilenGorsel = data.data
            if (secilenGorsel!=null){
                if (Build.VERSION.SDK_INT>=28){
                    val source=ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    imageView2.setImageBitmap(secilenBitmap)

                }else{
                    secilenBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    imageView2.setImageBitmap(secilenBitmap)
                }

            }
        }

            super.onActivityResult(requestCode, resultCode, data)
    }
}