package co.tuister.uisers

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import co.tuister.uisers.di.dataModule
import co.tuister.uisers.di.domainModule
import co.tuister.uisers.di.presentationModule
import co.tuister.uisers.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class UisersApplication : MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // TODO Implement Crashlytics
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
