package co.tuister.uisers.di

import co.tuister.uisers.modules.career.schedule.ScheduleViewModel
import co.tuister.uisers.modules.career.semesters.SemestersViewModel
import co.tuister.uisers.modules.career.subjects.SubjectsViewModel
import co.tuister.uisers.modules.career.subjects.add.AddSubjectViewModel
import co.tuister.uisers.modules.career.subjects.details.SubjectDetailsViewModel
import co.tuister.uisers.modules.home.HomeViewModel
import co.tuister.uisers.modules.institutional.InstitutionalViewModel
import co.tuister.uisers.modules.institutional.calendar.CalendarViewModel
import co.tuister.uisers.modules.institutional.map.MapViewModel
import co.tuister.uisers.modules.internal.InternalUseViewModel
import co.tuister.uisers.modules.login.LoginViewModel
import co.tuister.uisers.modules.login.forgotpassword.ForgotPasswordViewModel
import co.tuister.uisers.modules.login.register.RegisterViewModel
import co.tuister.uisers.modules.login.splash.SplashViewModel
import co.tuister.uisers.modules.main.MainViewModel
import co.tuister.uisers.modules.profile.ProfileViewModel
import co.tuister.uisers.modules.tasks.TasksViewModel
import co.tuister.uisers.modules.tasks.add.AddTaskViewModel
import co.tuister.uisers.utils.analytics.Analytics
import co.tuister.uisers.utils.analytics.FirebaseAnalytics
import co.tuister.uisers.utils.maps.GoogleMapsController
import co.tuister.uisers.utils.maps.MapController
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // login
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }

    // Main
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get()) }

    // My Career
    viewModel { SubjectsViewModel(get(), get(), get(), get()) }
    viewModel { SubjectDetailsViewModel(get(), get(), get(), get()) }
    viewModel { AddSubjectViewModel(get(), get()) }
    viewModel { ScheduleViewModel(get(), get(), get(), get()) }
    viewModel { SemestersViewModel(get(), get(), get()) }

    // Tasks
    viewModel { TasksViewModel(get(), get()) }
    viewModel { AddTaskViewModel(get()) }

    // Institutional
    viewModel { InstitutionalViewModel() }
    viewModel { CalendarViewModel(get()) }
    viewModel { MapViewModel(get(), get()) }

    // Internal Use Only
    viewModel { InternalUseViewModel(get(), get(), get(), get(), get()) }
}

val presentationModule = module {
    single<MapController> { GoogleMapsController() }
    single<Analytics> { FirebaseAnalytics(get()) }
}
