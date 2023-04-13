package com.example.mymusicwiki

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.mymusicwiki.adapter.HomeTagsAdapter
import com.example.mymusicwiki.model.Album
import com.example.mymusicwiki.model.HomeTag
import org.json.JSONException
import org.w3c.dom.Text

class AlbumDetailActivity : AppCompatActivity() {
    lateinit var album_name: String
    lateinit var album_artist: String
    lateinit var album_name_tv: TextView
    lateinit var album_artist_tv: TextView
    lateinit var album_description: TextView
    lateinit var tagsRecyclerView: RecyclerView
    lateinit var album_image: ImageView
    lateinit var tagList: ArrayList<HomeTag>
    lateinit var progressDialog: ProgressDialog
    val TAG = "AlbumDet"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)

        initializeView();

        val intent = intent
        album_name = intent.getStringExtra("album_name").toString()
        album_artist = intent.getStringExtra("album_artist").toString()

        album_name_tv.text = album_name
        album_artist_tv.text = album_artist

        fetchAlbumInfo()
    }

    private fun fetchAlbumInfo() {
        val apiUrl =
            "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=f1bb284143153afdd97fe783fc354ef1&artist=" + album_artist + "&album=" + album_name + "&format=json"
        Log.d(TAG, "fetchAlbums:" + apiUrl)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: " + response)
                try {
                    try {
                        album_description.text = response.getJSONObject("album")
                            .getJSONObject("wiki").getString("summary")
                    } catch (e: JSONException) {
                        album_description.visibility = View.GONE;
                    }
                    val imageUrl =
                        response.getJSONObject("album").getJSONArray("image").getJSONObject(3)
                            .getString("#text")
                    Glide.with(this).load(imageUrl).into(album_image)

                    val tagArray =
                        response.getJSONObject("album").getJSONObject("tags").getJSONArray("tag");
                    for (i in 0 until tagArray.length()) {
                        val tag = tagArray.getJSONObject(i)
                        val tempTag = HomeTag(
                            tag.getString("name"),
                            0, 0
                        )
                        tagList.add(tempTag)
                    }
                    setRecyclerView()
                    progressDialog.dismiss()
                } catch (e: JSONException) {
                    Log.d(TAG, "fetchAlbumsJsErs: " + e)
                    e.printStackTrace()
                    progressDialog.dismiss()
                }
            },
            Response.ErrorListener { error ->
                Log.d(TAG, "fetchAlbumsEr: " + error)
                progressDialog.dismiss()
            }) {
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }

    private fun setRecyclerView() {
        tagsRecyclerView = findViewById(R.id.tagsRecyclerView)
        tagsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = HomeTagsAdapter(
            tagList,
            tagList.size,
            "album"
        ) { position -> onListItemClick(position) }
        tagsRecyclerView.adapter = adapter

    }

    private fun onListItemClick(position: Int) {
        val intent = Intent(this, GenreDetailActivity::class.java)
        intent.putExtra("tag_name", tagList[position].name)
        startActivity(intent)
    }

    private fun initializeView() {
        album_name_tv = findViewById(R.id.album_name)
        album_artist_tv = findViewById(R.id.album_artist)
        album_description = findViewById(R.id.album_description)
        tagsRecyclerView = findViewById(R.id.tagsRecyclerView)
        album_image = findViewById(R.id.album_image)

        tagList = ArrayList()
        progressDialog = ProgressDialog(this, R.style.CustomProgressDialog)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
}