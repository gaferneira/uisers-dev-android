package co.tuister.uisers.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.androdocs.circleimagelibrary.CircleImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class ImagesUtils {
    companion object {
        fun downloadImageInto(context: Context, uri: Uri?, circleImageView: CircleImageView) {
            Glide.with(context).asBitmap().load(uri).into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap?,
                    transition: Transition<in Bitmap>?
                ) {
                    circleImageView.setImageBitmap(resource)
                }
            })
        }
    }
}
