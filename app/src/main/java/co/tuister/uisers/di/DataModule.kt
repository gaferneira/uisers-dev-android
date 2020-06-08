package co.tuister.uisers.di

import co.tuister.data.repositories.*
import co.tuister.domain.repositories.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module

val dataModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }
    single<SemesterRepository> { SemesterRepositoryImpl() }
    single<SubjectRepository> { SubjectRepositoryImpl() }
    single<TasksRepository> { TasksRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl() }
    single<MapRepository> { MapRepositoryImpl() }
}
