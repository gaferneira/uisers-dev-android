package co.tuister.uisers.di

import co.tuister.domain.usecases.FeedbackUseCase
import co.tuister.domain.usecases.MigrationUseCase
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.career.*
import co.tuister.domain.usecases.institutional.GetEventsUseCase
import co.tuister.domain.usecases.institutional.GetPlacesUseCase
import co.tuister.domain.usecases.institutional.GetSitesUseCase
import co.tuister.domain.usecases.internal.*
import co.tuister.domain.usecases.login.*
import co.tuister.domain.usecases.profile.DisableFirstTimeUseCase
import co.tuister.domain.usecases.profile.FirstTimeUseCase
import co.tuister.domain.usecases.profile.ProfileUseCase
import co.tuister.domain.usecases.tasks.GetMainTasks
import co.tuister.domain.usecases.tasks.GetMyTasksUseCase
import co.tuister.domain.usecases.tasks.RemoveTaskUseCase
import co.tuister.domain.usecases.tasks.SaveTaskUseCase
import org.koin.dsl.module

val domainModule = module {

    single { UserUseCase(get()) }
    single { FeedbackUseCase(get()) }

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

    single { SaveSemesterUseCase(get()) }
    single { GetCurrentSemesterUseCase(get()) }
    single { GetAllSemestersUseCase(get()) }
    single { ChangeCurrentSemesterUseCase(get()) }

    single { SaveSchedulePeriodUseCase(get()) }
    single { GetScheduleUseCase(get()) }
    single { GetScheduleByDateUseCase(get()) }
    single { RemoveSchedulePeriodUseCase(get()) }

    single { GetAllSubjectsUseCase(get(), get()) }
    single { SaveSubjectUseCase(get()) }
    single { GetMySubjectsUseCase(get()) }
    single { RemoveSubjectUseCase(get()) }

    single { SaveNoteUseCase(get()) }
    single { GetNotesUseCase(get()) }
    single { RemoveNoteUseCase(get()) }

    // Tasks
    single { SaveTaskUseCase(get()) }
    single { GetMainTasks(get()) }
    single { GetMyTasksUseCase(get()) }
    single { RemoveTaskUseCase(get()) }

    // Institutional
    single { GetPlacesUseCase(get()) }
    single { GetSitesUseCase(get()) }
    single { GetEventsUseCase(get()) }

    // Profile
    single { ProfileUseCase(get()) }
    single { FCMUpdateUseCase(get()) }
    single { SendVerifyLinkUseCase(get()) }
    single { DisableFirstTimeUseCase(get()) }
    single { FirstTimeUseCase(get()) }

    // Use case
    single { MigrationUseCase(get()) }

    // Internal
    single { DataUserUseCase(get()) }
    single { UpdateDataSubjectsUseCase(get()) }
    single { UpdateDataCareersUseCase(get()) }
    single { UpdateDataMapUseCase(get()) }
    single { UpdateDataCalendarUseCase(get()) }
}
