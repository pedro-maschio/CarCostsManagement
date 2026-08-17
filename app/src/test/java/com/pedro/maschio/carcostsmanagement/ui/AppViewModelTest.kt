package com.pedro.maschio.carcostsmanagement.ui

import com.pedro.maschio.carcostsmanagement.data.repository.CarCostsRepository
import com.pedro.maschio.carcostsmanagement.rules.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CarCostsRepository = mockk(relaxed = true)
    private lateinit var viewModel: AppViewModel

    private val introShownFlow = MutableStateFlow(false)
    private val isLoggedInFlow = MutableStateFlow(false)

    @Test
    fun `viewModel reflects repository flows`() = runTest {
        every { repository.introShown } returns introShownFlow
        every { repository.isLoggedIn } returns isLoggedInFlow
        
        viewModel = AppViewModel(repository)
        
        introShownFlow.value = true
        isLoggedInFlow.value = false
        
        assertEquals(true, viewModel.introShown.value)
        assertEquals(false, viewModel.isLoggedIn.value)
    }
}
