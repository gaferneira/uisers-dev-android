package co.tuister.uisers.di

import co.tuister.uisers.modules.home.HomeViewModel
import co.tuister.uisers.modules.institutional.InstitutionalViewModel
import co.tuister.uisers.modules.institutional.calendar.CalendarViewModel
import co.tuister.uisers.modules.institutional.map.MapViewModel
import co.tuister.uisers.modules.internal.InternalUseViewModel
import co.tuister.uisers.modules.login.LoginViewModel
import co.tuister.uisers.modules.login.forgot_password.ForgotPasswordViewModel
import co.tuister.uisers.modules.login.register.RegisterViewModel
import co.tuister.uisers.modules.login.splash.SplashViewModel
import co.tuister.uisers.modules.main.MainViewModel
import co.tuister.uisers.modules.my_career.MyCareerViewModel
import co.tuister.uisers.modules.my_career.schedule.ScheduleViewModel
import co.tuister.uisers.modules.my_career.semesters.SemestersViewModel
import co.tuister.uisers.modules.my_career.subjects.SubjectsViewModel
import co.tuister.uisers.modules.my_career.subjects.add_subject.AddSubjectViewModel
import co.tuister.uisers.modules.my_career.subjects.subject_details.SubjectDetailsViewModel
import co.tuister.uisers.modules.profile.ProfileViewModel
import co.tuister.uisers.modules.task_manager.TasksViewModel
import co.tuister.uisers.modules.task_manager.add_task.AddTaskViewModel
import co.tuister.uisers.utils.maps.GoogleMapsController
import co.tuister.uisers.utils.maps.MapController
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // login
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get(), get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }

    // Main
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get()) }

    // My Career
    viewModel { MyCareerViewModel(get()) }
    viewModel { SubjectsViewModel(get()) }
    viewModel { SubjectDetailsViewModel(get(), get()) }
    viewModel { AddSubjectViewModel(get(), get()) }
    viewModel { ScheduleViewModel(get(), get()) }
    viewModel { SemestersViewModel(get(), get(), get()) }

    // Tasks
    viewModel { TasksViewModel(get()) }
    viewModel { AddTaskViewModel(get()) }

    // Institutional
    viewModel { InstitutionalViewModel() }
    viewModel { CalendarViewModel(get()) }
    viewModel { MapViewModel(get(), get()) }

    // Internal Use Only
    viewModel { InternalUseViewModel(get()) }
}

val presentationModule = module {
    single<MapController> { GoogleMapsController() }
}
