package co.tuister.uisers.modules.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Semester
import co.tuister.domain.usecases.GetFeedUseCase
import co.tuister.domain.usecases.career.GetCurrentSemesterUseCase
import co.tuister.domain.usecases.career.GetScheduleByDateUseCase
import co.tuister.domain.usecases.institutional.GetUpcomingEventsUseCase
import co.tuister.domain.usecases.tasks.GetMainTasks
import co.tuister.uisers.TestCoroutineRule
import co.tuister.uisers.relaxedMockk
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var getCurrentSemesterCase: GetCurrentSemesterUseCase

    @RelaxedMockK
    private lateinit var getMainTasks: GetMainTasks

    @RelaxedMockK
    private lateinit var scheduleByUseCase: GetScheduleByDateUseCase

    @RelaxedMockK
    private lateinit var upcomingEventsUseCase: GetUpcomingEventsUseCase

    @RelaxedMockK
    private lateinit var getFeedUseCase: GetFeedUseCase

    @Before
    fun setUp() {

        MockKAnnotations.init(this, relaxUnitFun = true)

        getCurrentSemesterCase = relaxedMockk {
            coEvery { run() } returns Either.Right(Semester(period = "2020-2"))
        }

        getMainTasks = relaxedMockk {
            coEvery { this@relaxedMockk.run(any()) } returns Either.Right(listOf())
        }

        scheduleByUseCase = relaxedMockk {
            coEvery { this@relaxedMockk.run(any()) } returns Either.Right(listOf())
        }

        upcomingEventsUseCase = relaxedMockk {
            coEvery { this@relaxedMockk.run(any()) } returns Either.Right(listOf())
        }

        getFeedUseCase = relaxedMockk {
            coEvery { run() } returns Either.Right(listOf())
        }
    }

    @Test
    fun `When refresh, it loads each state successfully`() {
        testCoroutineRule.runBlockingTest {

            // given
            val viewModel = HomeViewModel(getCurrentSemesterCase, getMainTasks, scheduleByUseCase, upcomingEventsUseCase, getFeedUseCase)

            val job = launch {
                var count = 0

                viewModel.state.collect { state ->
                    println(state.toString())
                    when (state) {
                        is HomeViewModel.State.LoadHeader -> assertNotNull(state.data)
                        is HomeViewModel.State.LoadTasks -> assertNotNull(state.data)
                        is HomeViewModel.State.LoadSubjects -> assertNotNull(state.data)
                        is HomeViewModel.State.LoadCalendar -> assertNotNull(state.data)
                        is HomeViewModel.State.LoadFeed -> assertNotNull(state.data)
                    }

                    count++
                    if (count == 5) {
                        this.cancel()
                    }
                }
            }

            // when
            viewModel.refresh()

            // should
            runBlocking {
                withTimeout(TIME_OUT) {
                    job.join()
                }
            }
        }
    }

    @Test
    fun `When current semester fails, it should return error`() {
        testCoroutineRule.runBlockingTest {
            // given
            getCurrentSemesterCase = relaxedMockk {
                coEvery { run() } returns Either.Left(Failure.UnknownException())
            }

            val viewModel = HomeViewModel(getCurrentSemesterCase, getMainTasks, scheduleByUseCase, upcomingEventsUseCase, getFeedUseCase)

            val job = launch {
                viewModel.state.collect { state ->
                    println(state.toString())
                    when (state) {
                        is HomeViewModel.State.LoadHeader ->
                            state.handleResult(
                                onError = {
                                    this.cancel()
                                },
                                onSuccess = {
                                    fail("It shouldn't be success")
                                }
                            )
                    }
                }
            }

            // when
            viewModel.refresh()

            // should
            runBlocking {
                withTimeout(TIME_OUT) {
                    job.join()
                }
            }
        }
    }

    @Test
    fun `When get tasks fails, it should return error`() {
        testCoroutineRule.runBlockingTest {
            // given
            getMainTasks = relaxedMockk {
                coEvery { this@relaxedMockk.run(any()) } returns Either.Left(Failure.UnknownException())
            }

            val viewModel = HomeViewModel(getCurrentSemesterCase, getMainTasks, scheduleByUseCase, upcomingEventsUseCase, getFeedUseCase)

            val job = launch {
                viewModel.state.collect { state ->
                    println(state.toString())
                    when (state) {
                        is HomeViewModel.State.LoadTasks ->
                            state.handleResult(
                                onError = {
                                    this.cancel()
                                },
                                onSuccess = {
                                    fail("It shouldn't be success")
                                }
                            )
                    }
                }
            }

            // when
            viewModel.refresh()

            // should
            runBlocking {
                withTimeout(TIME_OUT) {
                    job.join()
                }
            }
        }
    }

    @Test
    fun `When get schedule fails, it should return error`() {
        testCoroutineRule.runBlockingTest {
            // given
            scheduleByUseCase = relaxedMockk {
                coEvery { this@relaxedMockk.run(any()) } returns Either.Left(Failure.UnknownException())
            }

            val viewModel = HomeViewModel(getCurrentSemesterCase, getMainTasks, scheduleByUseCase, upcomingEventsUseCase, getFeedUseCase)

            val job = launch {
                viewModel.state.collect { state ->
                    println(state.toString())
                    when (state) {
                        is HomeViewModel.State.LoadSubjects ->
                            state.handleResult(
                                onError = {
                                    this.cancel()
                                },
                                onSuccess = {
                                    fail("It shouldn't be success")
                                }
                            )
                    }
                }
            }

            // when
            viewModel.refresh()

            // should
            runBlocking {
                withTimeout(TIME_OUT) {
                    job.join()
                }
            }
        }
    }

    @Test
    fun `When get events fails, it should return error`() {
        testCoroutineRule.runBlockingTest {
            // given
            upcomingEventsUseCase = relaxedMockk {
                coEvery { this@relaxedMockk.run(any()) } returns Either.Left(Failure.UnknownException())
            }

            val viewModel = HomeViewModel(getCurrentSemesterCase, getMainTasks, scheduleByUseCase, upcomingEventsUseCase, getFeedUseCase)

            val job = launch {
                viewModel.state.collect { state ->
                    println(state.toString())
                    when (state) {
                        is HomeViewModel.State.LoadCalendar ->
                            state.handleResult(
                                onError = {
                                    this.cancel()
                                },
                                onSuccess = {
                                    fail("It shouldn't be success")
                                }
                            )
                    }
                }
            }

            // when
            viewModel.refresh()

            // should
            runBlocking {
                withTimeout(TIME_OUT) {
                    job.join()
                }
            }
        }
    }

    @Test
    fun `When get feed fails, it should return error`() {
        testCoroutineRule.runBlockingTest {
            // given
            getFeedUseCase = relaxedMockk {
                coEvery { run() } returns Either.Left(Failure.UnknownException())
            }

            val viewModel = HomeViewModel(getCurrentSemesterCase, getMainTasks, scheduleByUseCase, upcomingEventsUseCase, getFeedUseCase)

            val job = launch {
                viewModel.state.collect { state ->
                    println(state.toString())
                    when (state) {
                        is HomeViewModel.State.LoadFeed ->
                            state.handleResult(
                                onError = {
                                    this.cancel()
                                },
                                onSuccess = {
                                    fail("It shouldn't be success")
                                }
                            )
                    }
                }
            }

            // when
            viewModel.refresh()

            // should
            runBlocking {
                withTimeout(TIME_OUT) {
                    job.join()
                }
            }
        }
    }

    companion object {
        const val TIME_OUT = 10_000L
    }
}
