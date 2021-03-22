package com.doanducdat.chatapp.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.ActivityOnBoardingScreenBinding
import com.doanducdat.chatapp.ui.adapter.ScreenSlidePagerAdapter

class OnBoardingScreenActivity : AppCompatActivity() {

    private lateinit var oBSBinding: ActivityOnBoardingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oBSBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding_screen)

        val slideAdapter = ScreenSlidePagerAdapter(this)
        oBSBinding.viewPager2.adapter = slideAdapter
        oBSBinding.indicator3.setViewPager(oBSBinding.viewPager2)
        setColorIndicator()
    }

    private fun setColorIndicator() {
        val currentNightMode = Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                /*oBSBinding.indicator3.backgr = R.drawable.bg_circle_indicator_black*/
                oBSBinding.indicator3.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            }
        }
    }
}