package co.tuister.uisers.utils.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import co.tuister.uisers.utils.extensions.setImageFromUrl

class BindingAdapters private constructor() {
    companion object {
        @BindingAdapter("image_url")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, auctionImageUrl: String) {
            imageView.setImageFromUrl(auctionImageUrl)
        }
    }
}
