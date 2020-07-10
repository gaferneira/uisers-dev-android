package co.tuister.uisers.utils.extensions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseActivity.Companion.RESULT_LOAD_IMAGE
import co.tuister.uisers.utils.view.GlideApp
import com.androdocs.circleimagelibrary.CircleImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun ImageView.setImageFromUrl(imageUrl: String?, placeHolder: Int = R.color.grey_100) {
    GlideApp.with(context).load(imageUrl).placeholder(placeHolder).into(this)
}

fun FragmentActivity.launchImagePicker() {
    val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
    photoPickerIntent.type = "image/*"

    startActivityForResult(
        Intent.createChooser(photoPickerIntent, "Select Picture"),
        RESULT_LOAD_IMAGE
    )
}

fun Fragment.launchImagePicker() {
    val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
    photoPickerIntent.type = "image/*"

    startActivityForResult(
        Intent.createChooser(photoPickerIntent, "Select Picture"),
        RESULT_LOAD_IMAGE
    )
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
