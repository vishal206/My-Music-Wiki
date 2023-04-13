package com.example.mymusicwiki.adapter


import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mymusicwiki.fragments.AlbumsFragment
import com.example.mymusicwiki.fragments.ArtistsFragment
import com.example.mymusicwiki.fragments.TracksFragment


@Suppress("DEPRECATION")
internal class GenreDetailFragmentAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int,
    var tag_name: String
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                AlbumsFragment(tag_name)
            }
            1 -> {
                ArtistsFragment(tag_name)
            }
            2 -> {
                TracksFragment(tag_name)
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //this is where you set the titles
        when (position) {
            0 -> return "albums"
            1 -> return "artists"
            2 -> return "tracks"
        }
        return null
    }
}