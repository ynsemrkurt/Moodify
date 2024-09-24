package com.example.facedetection.ui.utils

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val activity: Activity) {
    private var rewardedAd: RewardedAd? = null

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
            }
        })
    }

    fun showAdIfAvailable(onRewarded: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(activity) {
                onRewarded.invoke()
            }
        } ?: run {
            onRewarded.invoke()
        }
    }
}