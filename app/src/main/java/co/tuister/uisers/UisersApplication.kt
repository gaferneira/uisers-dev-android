package co.tuister.uisers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import co.tuister.data.di.dataModule
import co.tuister.uisers.di.domainModule
import co.tuister.uisers.di.presentationModule
import co.tuister.uisers.di.viewModelModule
import co.tuister.uisers.utils.CrashlyticsLogTree
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class UisersApplication : MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsLogTree())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.channel_default_notification_id)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId, "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        startKoin {
            // Android context
            androidContext(this@UisersApplication)
            androidLogger()
            // modules
            modules(
                listOf(
                    dataModule,
                    domainModule,
                    viewModelModule,
                    presentationModule
                )
            )
        }
    }
}
