package co.tuister.uisers.di

import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.login.LoginUseCase
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.domain.usecases.login.RecoverPasswordUseCase
import co.tuister.domain.usecases.login.RegisterUseCase
import co.tuister.domain.usecases.my_career.*
import co.tuister.domain.usecases.tasks.GetMyTasksUseCase
import co.tuister.domain.usecases.tasks.GetTasksByDateUseCase
import co.tuister.domain.usecases.tasks.SaveTaskUseCase
import org.koin.dsl.module

val domainModule = module {

    single { UserUseCase(get()) }

    // login
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { RecoverPasswordUseCase(get()) }
    single { RegisterUseCase(get()) }

    // My Career
    single { GetCurrentSemesterUseCase(get()) }
    single { GetScheduleUseCase(get()) }
    single { GetScheduleByDateUseCase(get()) }
    single { GetMySubjectsUseCase(get()) }
    single { GetNotesUseCase(get()) }
    single { SaveNoteUseCase(get()) }
    single { GetAllSubjectsUseCase(get()) }
    single { SaveSubjectUseCase(get()) }

    // Tasks
    single { GetTasksByDateUseCase(get()) }
    single { GetMyTasksUseCase(get()) }
    single { SaveTaskUseCase(get()) }
}
