package com.plcoding.testingcourse.part7.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.plcoding.testingcourse.part7.domain.UserRepository
import com.plcoding.testingcourse.part7.domain.UserRepositoryFake
import com.plcoding.testingcourse.util.MainCoroutineExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Dispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@ExtendWith(MainCoroutineExtension::class)
class ProfileViewModelTest{

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var repository: UserRepositoryFake

    @BeforeEach
    fun setUp(){

        repository = UserRepositoryFake()
        profileViewModel = ProfileViewModel(
            repository = repository,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    "userId" to repository.profileToReturn.user.id
                )
            )
        )
    }

    @Test
    fun `test loading profile success`() = runTest{
        profileViewModel.loadProfile()

        advanceUntilIdle()

        assertThat(profileViewModel.state.value.profile).isEqualTo(repository.profileToReturn)
        assertThat(profileViewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `test loading profile error`() = runTest {
        repository.errorToReturn = Exception("Test exception")

        profileViewModel.loadProfile()

        advanceUntilIdle()

        assertThat(profileViewModel.state.value.profile).isNull()
        assertThat(profileViewModel.state.value.errorMessage).isEqualTo("Test exception")
        assertThat(profileViewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `test loading state updates`() = runTest {
        profileViewModel.state.test {
            val emission1 = awaitItem()
            assertThat(emission1.isLoading).isFalse()

            profileViewModel.loadProfile()
            val emmision2 = awaitItem()
            assertThat(emmision2.isLoading).isTrue()

            val emmision3 = awaitItem()
            assertThat(emmision3.isLoading).isFalse()
        }
    }
}