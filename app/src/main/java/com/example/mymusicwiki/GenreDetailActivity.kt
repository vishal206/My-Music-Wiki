package com.example.mymusicwiki

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mymusicwiki.adapter.GenreDetailFragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import org.json.JSONException


class GenreDetailActivity : AppCompatActivity() {

    val TAG="GenreDetTag"
    lateinit var tag_name:String
    lateinit var tag_name_tv:TextView
    lateinit var tag_description:TextView
    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre_detail)

        initializeViews()
        val intent=intent
        tag_name = intent.getStringExtra("tag_name").toString()
        tag_name_tv.text=tag_name

        fetchTagInfo()

        tabLayout.addTab(tabLayout.newTab().setText("albums"))
        tabLayout.addTab(tabLayout.newTab().setText("artists"))
        tabLayout.addTab(tabLayout.newTab().setText("tracks"))

        val adapter=GenreDetailFragmentAdapter(this, supportFragmentManager,tabLayout.tabCount)
        viewPager2.adapter = adapter

        tabLayout.setupWithViewPager(viewPager2);
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun initializeViews() {
        tag_name_tv = findViewById(R.id.tag_name)
        tag_description=findViewById(R.id.tag_description)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager2)
    }

    private fun fetchTagInfo() {
        val apiUrl= "https://ws.audioscrobbler.com/2.0/?method=tag.getinfo&tag="+tag_name+"&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchTags: here")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: "+response)
                try {
                    tag_description.text=response.getJSONObject("tag").getJSONObject("wiki").getString("summary")
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
}