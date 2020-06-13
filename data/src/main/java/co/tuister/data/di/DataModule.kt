package co.tuister.data.di

import co.tuister.data.repositories.*
import co.tuister.domain.repositories.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module

val dataModule = module {
    single { FirebaseAuth.getInstance() }
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
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }
    single<SemesterRepository> { SemesterRepositoryImpl() }
    single<SubjectRepository> { SubjectRepositoryImpl() }
    single<TasksRepository> { TasksRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get(), get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl() }
    single<MapRepository> { MapRepositoryImpl() }
    single<CalendarRepository> { CalendarRepositoryImpl() }
    single<InternalOnlyRepository> { InternalOnlyRepositoryImpl(get(), get()) }
}
