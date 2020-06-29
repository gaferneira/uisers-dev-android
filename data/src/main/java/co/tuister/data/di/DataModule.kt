package co.tuister.data.di

import co.tuister.data.migration.MigrationRepositoryImpl
import co.tuister.data.repositories.CalendarRepositoryImpl
import co.tuister.data.repositories.FeedRepositoryImpl
import co.tuister.data.repositories.InternalOnlyRepositoryImpl
import co.tuister.data.repositories.LoginRepositoryImpl
import co.tuister.data.repositories.MapRepositoryImpl
import co.tuister.data.repositories.ScheduleRepositoryImpl
import co.tuister.data.repositories.SemesterRepositoryImpl
import co.tuister.data.repositories.SharePreferencesRepositoryImpl
import co.tuister.data.repositories.SubjectRepositoryImpl
import co.tuister.data.repositories.TasksRepositoryImpl
import co.tuister.data.repositories.UserRepositoryImpl
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.domain.repositories.CalendarRepository
import co.tuister.domain.repositories.FeedRepository
import co.tuister.domain.repositories.InternalOnlyRepository
import co.tuister.domain.repositories.LoginRepository
import co.tuister.domain.repositories.MapRepository
import co.tuister.domain.repositories.MigrationRepository
import co.tuister.domain.repositories.ScheduleRepository
import co.tuister.domain.repositories.SemesterRepository
import co.tuister.domain.repositories.SharedPreferencesRepository
import co.tuister.domain.repositories.SubjectRepository
import co.tuister.domain.repositories.TasksRepository
import co.tuister.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import org.koin.dsl.module

val dataModule = module {
    single { GsonBuilder().create() }
    single { FirebaseAuth.getInstance() }
    single { ConnectivityUtil(get()) }

    single {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings =
                FirebaseFirestoreSettings
                    .Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(
                        CACHE_SIZE_UNLIMITED
                    ).build()
        }
    }
    single { FirebaseStorage.getInstance() }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get(), get()) }
    single<SharedPreferencesRepository> { SharePreferencesRepositoryImpl(get()) }
    single<FeedRepository> { FeedRepositoryImpl(get(), get()) }
    single<SemesterRepository> { SemesterRepositoryImpl(get(), get(), get()) }
    single<SubjectRepository> { SubjectRepositoryImpl(get(), get(), get(), get()) }
    single<TasksRepository> { TasksRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get(), get(), get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get(), get()) }
    single<MapRepository> { MapRepositoryImpl(get(), get(), get()) }
    single<CalendarRepository> { CalendarRepositoryImpl(get(), get(), get()) }
    single<InternalOnlyRepository> { InternalOnlyRepositoryImpl(get(), get(), get(), get()) }
    single<MigrationRepository> { MigrationRepositoryImpl(get(), get(), get(), get()) }
}
