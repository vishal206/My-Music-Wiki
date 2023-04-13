package com.example.mymusicwiki.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mymusicwiki.R
import com.example.mymusicwiki.adapter.AlbumsAdapter
import com.example.mymusicwiki.model.Album
import org.json.JSONException


class TracksFragment(val tag_name: String) : Fragment() {

    lateinit var tracks_recycler_view: RecyclerView
    val TAG = "ArtistFrag"
    lateinit var trackList: ArrayList<Album>
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tracks, container, false)

        progressDialog = ProgressDialog(context, R.style.CustomProgressDialog)
        progressDialog.setCancelable(false)
        progressDialog.show()

        tracks_recycler_view = view.findViewById(R.id.tracks_recycler_view);
        trackList = ArrayList()

        fetchTracks()

        return view
    }

    private fun fetchTracks() {
        val apiUrl =
            "https://ws.audioscrobbler.com/2.0/?method=tag.gettoptracks&tag=" + tag_name + "&api_key=f1bb284143153afdd97fe783fc354ef1&format=json"
        Log.d(TAG, "fetchAlbums: here")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                Log.d(TAG, "fetchTags: " + response)
                try {
                    val trackArray = response.getJSONObject("tracks").getJSONArray("track");
                    for (i in 0 until trackArray.length()) {
                        val album = trackArray.getJSONObject(i)
                        val tempAlbum = Album(
                            album.getString("name"),
                            album.getJSONArray("image").getJSONObject(3).getString("#text"),
                            album.getJSONObject("artist").getString("name")
                        )
                        trackList.add(tempAlbum)
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
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(jsonObjectRequest)
    }

    private fun setRecyclerView() {
        tracks_recycler_view.layoutManager = GridLayoutManager(context, 2)
        val adapter =
            AlbumsAdapter(requireContext(), trackList) { position -> onListItemClick(position) }
        tracks_recycler_view.adapter = adapter
    }

    private fun onListItemClick(position: Int) {
        Log.d(TAG, "onListItemClick: ")
    }

}