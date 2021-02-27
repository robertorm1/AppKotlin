package com.example.pruebakotlin

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.pruebakotlin.Adapters.ViePagerAdapter2
import com.example.pruebakotlin.Adapters.ViewPagerAdapter
import com.example.pruebakotlin.OnboardingFragments.FirstFragment
import com.example.pruebakotlin.OnboardingFragments.SecondFragment
import com.example.pruebakotlin.OnboardingFragments.ThirdFragment
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPagerAdapter2: ViePagerAdapter2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var BtnFin:MaterialButton

    var viewPager:ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById<ViewPager2>(R.id.PagerOnboarding)
        BtnFin = findViewById<MaterialButton>(R.id.BtnFinalizar)
        BtnFin.visibility = View.INVISIBLE

        val fragmentList= arrayListOf<Fragment>(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment()
        )
        val adapterPager = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )
       // viewPager?.adapter=adapterPager
        //viewPager.currentItem = 1

        setOnbardingItems()
        setupIndicator()
        setCurrentIndicator(0)

        BtnFin.setOnClickListener{
            val intent = Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == viewPagerAdapter2.itemCount - 1) {
                    BtnFin.visibility = VISIBLE
                }else {
                    BtnFin.visibility = INVISIBLE
                }

            }
        })

        (viewPager?.getChildAt(0) as RecyclerView).overScrollMode =RecyclerView.OVER_SCROLL_NEVER

    }

    private fun setOnbardingItems(){

       viewPagerAdapter2 = ViePagerAdapter2(
           listOf(
               OnboardingItem(
                   onboardingImage = R.raw.lottie_navigation,
                   onboardingText = "Localiza y navega a tus negocios facilmente"
               ),
               OnboardingItem(
                   onboardingImage = R.raw.lottie_check,
                   onboardingText = "Confirma y actualiza ubicaciones guardadas"
               ),
               OnboardingItem(
                   onboardingImage = R.raw.lottie_search,
                   onboardingText = "Busca y guarda la ubicacion de tus negocios"
               )
           )
       )

        viewPager?.adapter=viewPagerAdapter2

    }

    private  fun setupIndicator(){
        indicatorContainer = findViewById(R.id.IndicatorContainer)
        val indicators= arrayOfNulls<ImageView>(viewPagerAdapter2.itemCount)
        val layoutParams: LinearLayout.LayoutParams=
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices){
            indicators[i]= ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive)
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int){
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount ) {
        val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == position){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_active
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}