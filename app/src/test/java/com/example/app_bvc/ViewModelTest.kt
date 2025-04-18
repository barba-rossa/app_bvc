package com.example.app_bvc

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import com.google.common.truth.Truth.assertThat
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: FirebaseRepository

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadStudentProfile should update student when successful`() = runTest {
        // Given
        val testUserId = "1234" // Adicionando o userId para passar para o método
        val testStudent = StudentProfile("John Doe", "johndoe@example.com")
        `when`(repository.getStudentProfile(testUserId)).thenReturn(testStudent)

        // When
        viewModel.loadStudentProfile(testUserId)  // Passando o userId aqui
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.student.value).isEqualTo(testStudent)
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun `loadStudentProfile should handle null student`() = runTest {
        // Given
        val testUserId = "1234" // Defina o ID do usuário
        `when`(repository.getStudentProfile(testUserId)).thenReturn(null) // Mock com userId

        // When
        viewModel.loadStudentProfile(testUserId) // Passa o userId
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.student.value).isNull()
        assertThat(viewModel.isLoading.value).isFalse()
    }
}


class ProfileViewModel(private val repository: FirebaseRepository) : ViewModel() {
    val student = mutableStateOf<StudentProfile?>(null) // Altere para StudentProfile
    val isLoading = mutableStateOf(true)

    fun loadStudentProfile(userId: String) {
        isLoading.value = true
        viewModelScope.launch {
            student.value = repository.getStudentProfile(userId) // Esperando userId
            isLoading.value = false
        }
    }
}
