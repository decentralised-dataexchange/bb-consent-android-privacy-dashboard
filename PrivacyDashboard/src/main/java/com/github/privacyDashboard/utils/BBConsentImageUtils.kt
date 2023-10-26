package com.github.privacyDashboard.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions

object BBConsentImageUtils {
    fun setImage(imageView: ImageView, url: String?) {
        if (url != null && !url.isEmpty()) {
            val builder = LazyHeaders.Builder()
                .addHeader(
                    "Authorization",
                    "ApiKey " + BBConsentDataUtils.getStringValue(
                        imageView.context,
                        BBConsentDataUtils.EXTRA_TAG_TOKEN
                    )
                )
            val glideUrl = GlideUrl(url, builder.build())
            //
            Glide.with(imageView.context).load(glideUrl).into(imageView)
        }
    }

    fun setImage(imageView: ImageView, url: String?, placeHolder: Int) {
        if (url != null && !url.isEmpty()) {
            val builder = LazyHeaders.Builder()
                .addHeader(
                    "Authorization",
                    "ApiKey " + BBConsentDataUtils.getStringValue(
                        imageView.context,
                        BBConsentDataUtils.EXTRA_TAG_TOKEN
                    )
                ).addHeader(
                    "X-ConsentBB-IndividualId",
                    BBConsentDataUtils.getStringValue(
                        imageView.context,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    ) ?: ""
                )
            val glideUrl = GlideUrl(url, builder.build())
            val requestOptions = RequestOptions()
            requestOptions.placeholder(placeHolder)
            //            requestOptions.centerInside();
            requestOptions.error(placeHolder)
            Glide.with(imageView.context)
                .setDefaultRequestOptions(requestOptions)
                .load(glideUrl)
                .into(imageView)
        }
    }
}
