package com.adib.githubuser

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class ViewPagerAdapter(var context: Context, fragmentManager: FragmentManager, private var totalTabs: Int):
    FragmentPagerAdapter(fragmentManager){
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {FollowerFragment()}
            1 -> {FollowingFragment()}
            else -> getItem(position)
        }
    }
    override fun getCount(): Int = totalTabs
}