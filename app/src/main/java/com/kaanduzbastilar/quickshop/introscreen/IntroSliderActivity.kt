package com.kaanduzbastilar.quickshop.introscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.kaanduzbastilar.quickshop.view.LoginActivity
import com.kaanduzbastilar.quickshop.databinding.ActivityIntroSliderBinding


class IntroSliderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroSliderBinding

    private val fragmentList = ArrayList<Fragment>()

    private lateinit var sharedPref : SharedPreferences

    private var showIndicator : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref = getSharedPreferences("com.kaanduzbastilar.quickshop", Context.MODE_PRIVATE)
        showIndicator = sharedPref.getBoolean("indicator", false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

         */

            val adapter = IntroSliderAdapter(this)
            binding.vpIntroSlider.adapter = adapter

            fragmentList.addAll(listOf(
                Intro1Fragment(), Intro2Fragment(), Intro3Fragment()
            ))
            adapter.setFragmentList(fragmentList)

            binding.indicatorLayout.setIndicatorCount(adapter.itemCount)
            binding.indicatorLayout.selectCurrentPosition(0)
            if (showIndicator != true){
                registerListeners()
                sharedPref.edit().putBoolean("indicator", true).apply()
            }else{
                val intent = Intent(this@IntroSliderActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

    }
    private fun registerListeners() {

            binding.vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    binding.indicatorLayout.selectCurrentPosition(position)

                    if (position < fragmentList.lastIndex) {
                        binding.tvSkip.visibility = View.VISIBLE
                        binding.tvNext.text = "Next"
                    } else {
                        binding.tvSkip.visibility = View.GONE
                        binding.tvNext.text = "Get Started"
                    }
                }
            })

            binding.tvSkip.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            binding.tvNext.setOnClickListener {
                val position = binding.vpIntroSlider.currentItem

                if (position < fragmentList.lastIndex) {
                    binding.vpIntroSlider.currentItem = position + 1
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            showIndicator = true
    }
}