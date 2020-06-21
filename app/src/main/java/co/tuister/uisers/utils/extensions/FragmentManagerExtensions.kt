package co.tuister.uisers.utils.extensions

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import co.tuister.uisers.R
import co.tuister.uisers.utils.view.UisersDialogFragment

fun FragmentManager.showConfirmDialog(
    context: Context,
    message: String,
    title: String,
    negativeMessage: Int = android.R.string.cancel,
    unitNegative: (() -> Unit)? = null,
    unitPositive: (() -> Unit)? = null
) {
    UisersDialogFragment.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(
            R.string.ok,
            View.OnClickListener {
                unitPositive?.invoke()
            }
        )
        .setNegativeButton(
            negativeMessage,
            View.OnClickListener {
                unitNegative?.invoke()
            }
        )
        .create().show(this, UisersDialogFragment.UISERS_DIALOG_TAG)
}

fun FragmentManager.showDialog(
    context: Context,
    message: String,
    title: String,
    unit: (() -> Unit)? = null
) {
    UisersDialogFragment.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(
            R.string.ok,
            View.OnClickListener {
                unit?.invoke()
            }
        )
        .create().show(this, UisersDialogFragment.UISERS_DIALOG_TAG)
}

fun FragmentManager.showConfirmDialog(
    context: Context,
    message: Int,
    title: Int,
    negativeMessage: Int = android.R.string.cancel,
    unitNegative: (() -> Unit)? = null,
    unitPositive: (() -> Unit)? = null
) {
    showConfirmDialog(
        context,
        context.getString(message),
        context.getString(title),
        negativeMessage,
        unitNegative,
        unitPositive
    )
}

fun FragmentManager.showDialog(
    context: Context,
    message: Int,
    title: Int,
    unit: (() -> Unit)? = null
) {
    showDialog(context, context.getString(message), context.getString(title), unit)
}
