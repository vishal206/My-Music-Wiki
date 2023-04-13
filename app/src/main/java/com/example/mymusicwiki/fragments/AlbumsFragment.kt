package com.example.mymusicwiki.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mymusicwiki.GenreDetailActivity
import com.example.mymusicwiki.R
import com.example.mymusicwiki.adapter.AlbumsAdapter
import com.example.mymusicwiki.model.Album
import com.example.mymusicwiki.model.HomeTag
import org.json.JSONException


class AlbumsFragment(val tag_name:String) : Fragment() {

    lateinit var album_recycler_view:RecyclerView
    val TAG="AlbumFrag"
    lateinit var albumList:ArrayList<Album>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_albums, container, false)

        album_recycler_view=view.findViewById(R.id.album_recycler_view);
        albumList=ArrayList()

        fetchAlbums()
        return view
    }

    private fun fetchAlbums() {
        val apiUrl= "https://ws.audioscrobbler.com/2.0/?method=tag.gettopalbums&tag="+tag_name+"&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchAlbums: here")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: "+response)
                try {
                    val albumArray=response.getJSONObject("albums").getJSONArray("album");
                    for (i in 0 until albumArray.length()){
                        val album = albumArray.getJSONObject(i)
                        val tempAlbum = Album(album.getString("name"),
                        album.getJSONArray("image").getJSONObject(3).getString("#text"),
                        album.getJSONObject("artist").getString("name"))
                        albumList.add(tempAlbum)
                    }
                    setRecyclerView()
                } catch (e: JSONException) {
                    Log.d(TAG, "fetchAlbumsJsErs: "+e)
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Log.d(TAG, "fetchAlbumsEr: "+error) }) {
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(jsonObjectRequest)
    }

    private fun setRecyclerView() {
        album_recycler_view.layoutManager=GridLayoutManager(context,2)
        val adapter=AlbumsAdapter(albumList){position -> onListItemClick(position)}
        album_recycler_view.adapter=adapter
    }

    private fun onListItemClick(position: Int) {
        Toast.makeText(context, albumList[position].name, Toast.LENGTH_SHORT).show()
//        val intent = Intent(this, GenreDetailActivity::class.java)
//        intent.putExtra("tag_name",tagList[position].name)
//        startActivity(intent)
    }
}