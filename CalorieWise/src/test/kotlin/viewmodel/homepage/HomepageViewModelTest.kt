package viewmodel.homepage

import model.UserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import viewmodel.HomepageViewModel
import kotlin.test.Test

class HomepageViewModelTest {
    private lateinit var userModel: UserModel
    private lateinit var homepageViewModel: HomepageViewModel

    @BeforeEach
    fun setUp() {
        userModel = mock(UserModel::class.java)
        homepageViewModel = HomepageViewModel(userModel)
    }

    @Test
    fun testCalorieEatenUpdate() {
        `when`(userModel.calorieTaken).thenReturn(100)
        homepageViewModel.update()
        assertEquals(100, homepageViewModel.calorieEaten.value)
    }

}