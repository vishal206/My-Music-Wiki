package com.example.mymusicwiki

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.mymusicwiki.adapter.AlbumsAdapter
import com.example.mymusicwiki.adapter.HomeTagsAdapter
import com.example.mymusicwiki.model.Album
import com.example.mymusicwiki.model.HomeTag
import org.json.JSONException
import kotlin.math.ln
import kotlin.math.pow

class ArtistDetailActivity : AppCompatActivity() {

    lateinit var artist_name_tv: TextView
    lateinit var playcount_tv: TextView
    lateinit var follower_count_tv: TextView
    lateinit var artist_description: TextView
    lateinit var artist_image: ImageView
    lateinit var artist_name: String
    lateinit var progressDialog: ProgressDialog
    lateinit var tagsRecyclerView: RecyclerView
    lateinit var albums_recycler_view: RecyclerView
    lateinit var tracks_recycler_view: RecyclerView

    lateinit var tagList: ArrayList<HomeTag>
    lateinit var albumList: ArrayList<Album>
    lateinit var trackList: ArrayList<Album>
    val TAG = "artistDet"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_detail)

        initializeViews()

        val intent = intent
        artist_name = intent.getStringExtra("artist_name").toString()
        artist_name_tv.text = artist_name

        fetchDetail()
        fetchAlbums()
        fetchTracks()
    }

    private fun fetchTracks() {
        val apiUrl =
            "https://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist=" + artist_name + "&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchAlbums: here")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: " + response)
                try {
                    val trackArray = response.getJSONObject("toptracks").getJSONArray("track");
                    for (i in 0 until trackArray.length()) {
                        val album = trackArray.getJSONObject(i)
                        val tempAlbum = Album(
                            album.getString("name"),
                            album.getJSONArray("image").getJSONObject(3).getString("#text"),
                            album.getJSONObject("artist").getString("name")
                        )
                        trackList.add(tempAlbum)
                    }
                    setTrackRecyclerView()
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

    private fun setTrackRecyclerView() {
        tracks_recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter =
            AlbumsAdapter(this, trackList) { position -> onListItemTrackClick(position) }
        tracks_recycler_view.adapter = adapter
    }

    private fun onListItemTrackClick(position: Int) {
        Log.d(TAG, "onListItemClick: ")
    }

    private fun fetchAlbums() {
        val apiUrl =
            "https://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=" + artist_name + "&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchAlbums: here")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: " + response)
                try {
                    val albumArray = response.getJSONObject("topalbums").getJSONArray("album");
                    for (i in 0 until albumArray.length()) {
                        val album = albumArray.getJSONObject(i)
                        val tempAlbum = Album(
                            album.getString("name"),
                            album.getJSONArray("image").getJSONObject(3).getString("#text"),
                            album.getJSONObject("artist").getString("name")
                        )
                        albumList.add(tempAlbum)
                    }
                    setAlbumRecyclerView()
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

    private fun setAlbumRecyclerView() {
        albums_recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter =
            AlbumsAdapter(this, albumList) { position -> onListAlbumItemClick(position) }
        albums_recycler_view.adapter = adapter
    }

    private fun onListAlbumItemClick(position: Int) {
        val intent = Intent(this, AlbumDetailActivity::class.java)
        intent.putExtra("album_name", albumList[position].name)
        intent.putExtra("album_artist", albumList[position].artist)
        startActivity(intent)
    }


    private fun fetchDetail() {
        val apiUrl =
            "https://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + artist_name + "&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchArtist: " + apiUrl)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: " + response)
                try {
                    val tagArray =
                        response.getJSONObject("artist").getJSONObject("tags").getJSONArray("tag");
                    for (i in 0 until tagArray.length()) {
                        val tag = tagArray.getJSONObject(i)
                        val tempTag = HomeTag(
                            tag.getString("name"),
                            0, 0
                        )
                        tagList.add(tempTag)
                    }
                    setRecyclerView()
                    val artist = response.getJSONObject("artist")
                    val playcount = artist.getJSONObject("stats").getString("playcount").toLong()
                    val follower = artist.getJSONObject("stats").getString("listeners").toLong()
                    playcount_tv.text = getFormatedNumber(playcount)
                    follower_count_tv.text = getFormatedNumber(follower)
                    artist_description.text = artist.getJSONObject("bio").getString("summary")

                    val imageUrl =
                        artist.getJSONArray("image").getJSONObject(3)
                            .getString("#text")
                    Glide.with(this).load(imageUrl).into(artist_image)
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

    fun getFormatedNumber(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
    }

    private fun initializeViews() {
        artist_name_tv = findViewById(R.id.artist_name)
        playcount_tv = findViewById(R.id.playcount)
        follower_count_tv = findViewById(R.id.follower_count)
        artist_description = findViewById(R.id.artist_description)
        artist_image = findViewById(R.id.artist_image)
        tagsRecyclerView = findViewById(R.id.tagsRecyclerView)
        albums_recycler_view = findViewById(R.id.albums_recycler_view)
        tracks_recycler_view = findViewById(R.id.tracks_recycler_view);

        tagList = ArrayList()
        progressDialog = ProgressDialog(this, R.style.CustomProgressDialog)
        progressDialog.setCancelable(false)
        progressDialog.show()
        albumList = ArrayList()
        trackList = ArrayList()
    }
}