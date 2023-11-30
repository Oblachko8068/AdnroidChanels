package com.example.channels.ads.bannerAds

import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

const val adMyTargetBannerId = "demo-banner-mytarget"

class MyTargetBannerAd(val context: Context) {

    private var bannerAd: BannerAdView? = null
    private var bannerReady = false

    fun loadBannerAd() {
        bannerReady = false
        bannerAd = BannerAdView(context)
        bannerAd?.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                bannerReady = true
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.e(
                    "BannerAdErrorMyTarget",
                    "Failed to load banner ad. Error code: ${p0.code}, Message: ${p0.description}"
                )
            }

            override fun onAdClicked() {

            }

            override fun onLeftApplication() {

            }

            override fun onReturnedToApplication() {

            }

            override fun onImpression(p0: ImpressionData?) {

            }
        })
        bannerAd?.setAdUnitId(adMyTargetBannerId)
        bannerAd?.setAdSize(BannerAdSize.inlineSize(context, 600, 50))
        bannerAd?.loadAd(AdRequest.Builder().build())
    }

    fun showBannerAd(): BannerAdView? {
        return bannerAd
    }

    fun isAdLoaded(): Boolean {
        return bannerReady
    }
}