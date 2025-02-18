package com.alphaomardiallo.handydocs.common.presentation.ad

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.alphaomardiallo.handydocs.BuildConfig
import com.alphaomardiallo.handydocs.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import timber.log.Timber

@Composable
fun AdmobGenericBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId =
                    if (BuildConfig.DEBUG) {
                        context.getString(R.string.ad_banner_debug)
                    } else context.getString(
                        R.string.ad_banner_release
                    )
                loadAd(AdRequest.Builder().build())

                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Timber.d("AdmobGenericBanner : Ad loaded")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        Timber.d("AdmobGenericBanner : Ad clicked")
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        Timber.d("AdmobGenericBanner : Ad closed")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        Timber.d("AdmobGenericBanner : Ad Impression")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Timber.d("AdmobGenericBanner : Ad opened")
                    }
                }
            }
        }
    )
}
