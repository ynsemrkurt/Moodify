package com.example.facedetection.ui.utils

import android.app.Activity
import android.widget.Toast
import com.example.facedetection.BuildConfig.AD_UNIT_ID
import com.example.facedetection.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val activity: Activity) {
    private var rewardedAd: RewardedAd? = null

    fun loadRewardedAd(onAdLoaded: () -> Unit) {
        val adRequest = AdRequest.Builder().build()
        Toast.makeText(activity, activity.getString(R.string.ad_loading), Toast.LENGTH_SHORT).show()
        RewardedAd.load(activity, AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                showRewardedAd {
                    onAdLoaded.invoke()
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                onAdLoaded.invoke()
            }
        })
    }

    private fun showRewardedAd(onRewarded: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(activity) {
                onRewarded.invoke()
            }
        } ?: run {
            onRewarded.invoke()
        }
    }
}