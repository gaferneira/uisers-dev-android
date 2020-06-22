package co.tuister.uisers.di

import co.tuister.domain.usecases.FeedbackUseCase
import co.tuister.domain.usecases.MigrationUseCase
import co.tuister.domain.usecases.UserUseCase
import co.tuister.domain.usecases.career.ChangeCurrentSemesterUseCase
import co.tuister.domain.usecases.career.GetAllSemestersUseCase
import co.tuister.domain.usecases.career.GetAllSubjectsUseCase
import co.tuister.domain.usecases.career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.career.GetMySubjectsUseCase
import co.tuister.domain.usecases.career.GetNotesUseCase
import co.tuister.domain.usecases.career.GetScheduleByDateUseCase
import co.tuister.domain.usecases.career.GetScheduleUseCase
import co.tuister.domain.usecases.career.RemoveNoteUseCase
import co.tuister.domain.usecases.career.RemoveSchedulePeriodUseCase
import co.tuister.domain.usecases.career.RemoveSubjectUseCase
import co.tuister.domain.usecases.career.SaveNoteUseCase
import co.tuister.domain.usecases.career.SaveSchedulePeriodUseCase
import co.tuister.domain.usecases.career.SaveSemesterUseCase
import co.tuister.domain.usecases.career.SaveSubjectUseCase
import co.tuister.domain.usecases.institutional.GetEventsUseCase
import co.tuister.domain.usecases.institutional.GetPlacesUseCase
import co.tuister.domain.usecases.institutional.GetSitesUseCase
import co.tuister.domain.usecases.internal.DataUserUseCase
import co.tuister.domain.usecases.internal.UpdateDataCalendarUseCase
import co.tuister.domain.usecases.internal.UpdateDataCareersUseCase
import co.tuister.domain.usecases.internal.UpdateDataMapUseCase
import co.tuister.domain.usecases.internal.UpdateDataSubjectsUseCase
import co.tuister.domain.usecases.login.CampusUseCase
import co.tuister.domain.usecases.login.CareersUseCase
import co.tuister.domain.usecases.login.DownloadImageUseCase
import co.tuister.domain.usecases.login.FCMUpdateUseCase
import co.tuister.domain.usecases.login.LoginUseCase
import co.tuister.domain.usecases.login.LogoutUseCase
import co.tuister.domain.usecases.login.RecoverPasswordUseCase
import co.tuister.domain.usecases.login.RegisterUseCase
import co.tuister.domain.usecases.login.SendVerifyLinkUseCase
import co.tuister.domain.usecases.login.UploadImageUseCase
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

    // Use case
    single { MigrationUseCase(get()) }

    // Internal
    single { DataUserUseCase(get()) }
    single { UpdateDataSubjectsUseCase(get()) }
    single { UpdateDataCareersUseCase(get()) }
    single { UpdateDataMapUseCase(get()) }
    single { UpdateDataCalendarUseCase(get()) }
}
