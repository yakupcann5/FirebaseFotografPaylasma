package com.example.firebasefotografpaylasma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_feed.*

class HaberActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var recyclerViewAdapter : HaberRecyclerAdapter
    var postListesi = ArrayList<Post>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        verileriAl()
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = HaberRecyclerAdapter(postListesi)
        recyclerView.adapter=recyclerViewAdapter
    }
    fun verileriAl (){
        database.collection("Post").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exeption ->
            if (exeption != null){
                Toast.makeText(this,exeption.localizedMessage,Toast.LENGTH_LONG)
            }else{
                if (snapshot != null){
                    if (!snapshot.isEmpty){
                        val documents = snapshot.documents

                        postListesi.clear()

                        for (document in documents){
                            val kullaniciEmail =document.get("kullaniciemail") as String
                            val kullaniciYorum =document.get("kullaniciyorum") as String
                            val gorselUrl =document.get("gorselurl") as String

                            val indirilenPost = Post(kullaniciEmail,kullaniciYorum,gorselUrl)
                            postListesi.add(indirilenPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.secenekler_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.foto_paylas){
            //fotoğraf paylaşmaya gidilecek
            val intent = Intent(this,FotografPaylasActivity::class.java)
            startActivity(intent)
        }else if (item.itemId==R.id.cikis){
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}