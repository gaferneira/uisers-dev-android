package co.tuister.uisers.di

import co.tuister.domain.usecases.MigrationUseCase
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.institutional.GetEventsUseCase
import co.tuister.domain.usecases.institutional.GetPlacesUseCase
import co.tuister.domain.usecases.institutional.GetSitesUseCase
import co.tuister.domain.usecases.login.*
import co.tuister.domain.usecases.my_career.*
import co.tuister.domain.usecases.profile.ProfileUseCase
import co.tuister.domain.usecases.tasks.GetMainTasks
import co.tuister.domain.usecases.tasks.GetMyTasksUseCase
import co.tuister.domain.usecases.tasks.SaveTaskUseCase
import org.koin.dsl.module

val domainModule = module {

    single { UserUseCase(get()) }

    // login
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { RecoverPasswordUseCase(get()) }
    single { RegisterUseCase(get()) }
    single { CareersUseCase(get()) }
    single { CampusUseCase(get()) }
    single { UploadImageUseCase(get()) }
    single { DownloadImageUseCase(get()) }

    // My Career

    single { GetCurrentSemesterUseCase(get()) }
    single { SaveSemesterUseCase(get()) }
    single { GetAllSemestersUseCase(get()) }
    single { ChangeCurrentSemesterUseCase(get()) }

    single { GetScheduleUseCase(get()) }
    single { SaveSchedulePeriodUseCase(get()) }
    single { GetScheduleByDateUseCase(get()) }

    single { GetMySubjectsUseCase(get()) }
    single { GetNotesUseCase(get()) }
    single { SaveNoteUseCase(get()) }
    single { GetAllSubjectsUseCase(get(), get()) }
    single { SaveSubjectUseCase(get()) }

    // Tasks
    single { GetMainTasks(get()) }
    single { GetMyTasksUseCase(get()) }
    single { SaveTaskUseCase(get()) }

    // Institutional
    single { GetPlacesUseCase(get()) }
    single { GetSitesUseCase(get()) }
    single { GetEventsUseCase(get()) }

    // Profile
    single { ProfileUseCase(get()) }
    single { FCMUpdateUseCase(get()) }
    single { SendVerifyLinkUseCase(get()) }

    // Use case
    single { MigrationUseCase(get()) }
}
