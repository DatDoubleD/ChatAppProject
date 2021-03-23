package com.doanducdat.chatapp.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.animation.ZoomOutPageTransformer
import com.doanducdat.chatapp.databinding.ActivityOnBoardingScreenBinding
import com.doanducdat.chatapp.ui.adapter.ScreenSlidePagerAdapter
import com.doanducdat.chatapp.utils.CheckCurrentThemeApp

class OnBoardingScreenActivity : AppCompatActivity() {

    private lateinit var oBSBinding: ActivityOnBoardingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oBSBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding_screen)

        val slideAdapter = ScreenSlidePagerAdapter(this)
        oBSBinding.viewPager2.adapter = slideAdapter
        oBSBinding.indicator3.setViewPager(oBSBinding.viewPager2)
        setColorIndicator()
        oBSBinding.viewPager2.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun setColorIndicator() {
        if (CheckCurrentThemeApp.checkCurrentTheme()) {
            oBSBinding.indicator3.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }
}