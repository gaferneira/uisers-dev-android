package co.tuister.uisers.utils.extensions

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import co.tuister.uisers.utils.view.GlideApp
import com.androdocs.circleimagelibrary.CircleImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun ImageView.setImageFromUrl(imageUrl: String) {
    GlideApp.with(context).load(imageUrl).into(this)
}

fun CircleImageView.setImageFromUri(uri: Uri?) {
    GlideApp.with(context).asBitmap().load(uri).into(object : CustomTarget<Bitmap>() {

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            setImageBitmap(resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            // No op
        }
    })
}
