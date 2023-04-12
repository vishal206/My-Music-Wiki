package com.example.mymusicwiki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mymusicwiki.adapter.HomeTagsAdapter
import com.example.mymusicwiki.model.HomeTag
import org.json.JSONException

const val TAG="MainTag"
class MainActivity : AppCompatActivity() {

    lateinit var tagList: ArrayList<HomeTag>
    lateinit var tagsRecyclerView: RecyclerView
    lateinit var expand_button:Button
    var expand=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tagList= ArrayList()
        fetchTags()

        expand_button=findViewById(R.id.expand_button)
        expand_button.setOnClickListener{
            expand=!expand
            if(expand==false){
                expand_button.text="show more genre"
            }else{
                expand_button.text="show less genre"
            }
            setRecyclerView()
        }
    }

    private fun fetchTags() {
        val apiUrl= "https://ws.audioscrobbler.com/2.0/?method=tag.getTopTags&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchTags: here")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: "+response)
                try {
                    val tagArray=response.getJSONObject("toptags").getJSONArray("tag");
                    for (i in 0 until tagArray.length()){
                        val tag=tagArray.getJSONObject(i)
                        val tempTag= HomeTag(tag.getString("name"),
                            tag.getInt("count"),
                            tag.getInt("reach"))
                        tagList.add(tempTag)
                    }
                    setRecyclerView()
                } catch (e: JSONException) {
                    Log.d(TAG, "fetchTagJsErs: "+e)
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Log.d(TAG, "fetchTagsEr: "+error) }) {
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }

    private fun setRecyclerView() {
        tagsRecyclerView=findViewById(R.id.tagsRecyclerView)
        tagsRecyclerView.layoutManager=GridLayoutManager(this,2)
        if(!expand){
            val adapter=HomeTagsAdapter(tagList,10){position -> onListItemClick(position)}
            tagsRecyclerView.adapter=adapter
        }else {
            val adapter=HomeTagsAdapter(tagList,tagList.size){position -> onListItemClick(position)}
            tagsRecyclerView.adapter=adapter
        }
    }

    private fun onListItemClick(position: Int) {
//        Toast.makeText(this, tagList[position].name, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, GenreDetailActivity::class.java)
        startActivity(intent)
    }
}