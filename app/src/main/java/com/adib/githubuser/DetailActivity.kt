package com.adib.githubuser

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adib.githubuser.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout

class DetailActivity : AppCompatActivity() {

    companion object {
        const val DETAIL_USER = "detail_user"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = intent.getParcelableExtra(DETAIL_USER) as User

        supportActionBar?.title = profile.username
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.detailName.text = profile.name
        binding.detailCompany.text = profile.company
        binding.detailLocation.text = profile.location
        binding.detailFollowersNum.text = profile.followers.toString()
        binding.detailFollowingNum.text = profile.following.toString()
        binding.detailRepoNum.text = profile.repository.toString()
        Glide.with(this)
            .load(profile.avatar)
            .into(binding.detailAvatar)

        detailViewPager()
    }

    private fun detailViewPager() {
        val tabLayout = binding.detailTab
        val viewPager = binding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText("Follower"))
        tabLayout.addTab(tabLayout.newTab().setText("Following"))

        val adapter = ViewPagerAdapter(this, supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#000000"))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

        })
    }
}
