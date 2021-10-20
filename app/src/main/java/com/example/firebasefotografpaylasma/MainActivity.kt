package com.example.firebasefotografpaylasma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        val guncelKullanici =auth.currentUser
        if (guncelKullanici!=null) {
            val intent = Intent(this, HaberActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun giris(view: View){
        auth.signInWithEmailAndPassword(emailText.text.toString(),sifreText.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val guncelKullanici=auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldiniz",Toast.LENGTH_LONG).show()
                val intent = Intent(this,HaberActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }
    fun kayit(view: View){
        val email=emailText.text.toString()
        val sifre = sifreText.text.toString()
        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val intent = Intent(this,HaberActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }


    }
}