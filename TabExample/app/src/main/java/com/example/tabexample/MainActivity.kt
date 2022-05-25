package com.example.tabexample

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.tabexample.ui.main.SectionsPagerAdapter
import com.example.tabexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabsNames = this.resources.getStringArray(R.array.characters)
        val picResources = this.resources.obtainTypedArray(R.array.characters_pics)
        val descriptions = this.resources.getStringArray(R.array.characters_description)

        val sectionsPagerAdapter = SectionsPagerAdapter(tabsNames, picResources, descriptions, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
    }
}