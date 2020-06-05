package co.tuister.uisers.di

import co.tuister.uisers.modules.home.HomeViewModel
import co.tuister.uisers.modules.institutional.InstitutionalViewModel
import co.tuister.uisers.modules.institutional.calendar.CalendarViewModel
import co.tuister.uisers.modules.institutional.map.MapViewModel
import co.tuister.uisers.modules.institutional.wheels.WheelsViewModel
import co.tuister.uisers.modules.login.LoginViewModel
import co.tuister.uisers.modules.login.forgot_password.ForgotPasswordViewModel
import co.tuister.uisers.modules.login.register.RegisterViewModel
import co.tuister.uisers.modules.login.splash.SplashViewModel
import co.tuister.uisers.modules.main.MainViewModel
import co.tuister.uisers.modules.my_career.SubjectsViewModel
import co.tuister.uisers.modules.my_career.add_subject.AddSubjectViewModel
import co.tuister.uisers.modules.my_career.schedule.ScheduleViewModel
import co.tuister.uisers.modules.my_career.subject_details.SemestersViewModel
import co.tuister.uisers.modules.my_career.subject_details.SubjectDetailsViewModel
import co.tuister.uisers.modules.profile.ProfileViewModel
import co.tuister.uisers.modules.task_manager.TasksViewModel
import co.tuister.uisers.modules.task_manager.add_task.AddTaskViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { InstitutionalViewModel() }
    viewModel { SubjectsViewModel(get(), get()) }
    viewModel { ProfileViewModel() }
    viewModel { TasksViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { AddTaskViewModel() }
    viewModel { CalendarViewModel() }
    viewModel { MapViewModel() }
    viewModel { WheelsViewModel() }
    viewModel { AddSubjectViewModel(get(), get()) }
    viewModel { ScheduleViewModel() }
    viewModel { SemestersViewModel() }
    viewModel { SubjectDetailsViewModel() }
}
