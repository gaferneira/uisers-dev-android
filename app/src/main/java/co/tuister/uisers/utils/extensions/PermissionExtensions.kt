package co.tuister.uisers.utils.extensions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import co.tuister.uisers.R

fun Fragment.isGranted(permission: AppPermission) = run {
    context?.let {
        (
            PermissionChecker.checkSelfPermission(
                it, permission.permissionName
            ) == PermissionChecker.PERMISSION_GRANTED
            )
    } ?: false
}

fun Fragment.shouldShowRationale(permission: AppPermission) = run {
    shouldShowRequestPermissionRationale(permission.permissionName)
}

fun Fragment.requestPermission(permission: AppPermission) {
    requestPermissions(
        arrayOf(permission.permissionName), permission.requestCode
    )
}

fun Fragment.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onDenied: (AppPermission) -> Unit,
    onRationaleNeeded: ((AppPermission) -> Unit)? = null
) {
    when {
        isGranted(permission) -> onGranted.invoke(permission)
        shouldShowRationale(permission) -> onRationaleNeeded?.invoke(permission)
        else -> onDenied.invoke(permission)
    }
}

fun Fragment.handlePermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    onPermissionGranted: (AppPermission) -> Unit,
    onPermissionDenied: ((AppPermission) -> Unit)? = null,
    onPermissionDeniedPermanently: ((AppPermission) -> Unit)? = null
) {

    AppPermission.permissions.find {
        it.requestCode == requestCode
    }?.let { appPermission ->
        val permissionGrantResult = mapPermissionsAndResults(
            permissions, grantResults
        )[appPermission.permissionName]
        when {
            PermissionChecker.PERMISSION_GRANTED == permissionGrantResult -> {
                onPermissionGranted(appPermission)
            }
            shouldShowRationale(appPermission) -> onPermissionDenied?.invoke(appPermission)
            else -> {
                goToAppDetailsSettings()
                onPermissionDeniedPermanently?.invoke(appPermission)
            }
        }
    }
}

private fun Fragment.goToAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context?.packageName, null)
    }
    activity?.startActivityForResult(intent, 0)
}

private fun mapPermissionsAndResults(
    permissions: Array<out String>,
    grantResults: IntArray
): Map<String, Int> = permissions.mapIndexedTo(
    mutableListOf()
) { index, permission -> permission to grantResults[index] }.toMap()

sealed class AppPermission(
    val permissionName: String,
    val requestCode: Int,
    val deniedMessageId: Int,
    val explanationMessageId: Int
) {
    companion object {
        val permissions: List<AppPermission> by lazy {
            listOf(
                AccessFineLocation,
                AccessCoarseLocation
            )
        }

        const val REQUEST_CODE_ACCESS_FINE_LOCATION = 42
        const val REQUEST_CODE_ACCESS_COARSE_LOCATION = 43
    }

    object AccessFineLocation : AppPermission(
        Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_CODE_ACCESS_FINE_LOCATION,
        R.string.permission_location_required_text, R.string.permission_location_explanation
    )

    object AccessCoarseLocation : AppPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_CODE_ACCESS_COARSE_LOCATION,
        R.string.permission_location_required_text, R.string.permission_location_explanation
    )
}
