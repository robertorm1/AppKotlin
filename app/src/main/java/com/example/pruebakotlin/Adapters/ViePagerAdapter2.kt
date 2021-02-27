package com.example.pruebakotlin.Adapters

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.pruebakotlin.OnboardingItem
import com.example.pruebakotlin.R

class ViePagerAdapter2(private val onboardingItems:List<OnboardingItem>):
    RecyclerView.Adapter<ViePagerAdapter2.OnboardingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
      val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager,parent,false)

        return OnboardingItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    inner class OnboardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val ImageOnboarding= view.findViewById<LottieAnimationView>(R.id.lottieItem)
        private val TextPage = view.findViewById<TextView>(R.id.TextPager)

        fun bind (onboardingItem:OnboardingItem){
            ImageOnboarding.setAnimation(onboardingItem.onboardingImage)
            ImageOnboarding.playAnimation()
            ImageOnboarding.repeatCount = LottieDrawable.INFINITE
            TextPage.text = onboardingItem.onboardingText
        }
    }
}