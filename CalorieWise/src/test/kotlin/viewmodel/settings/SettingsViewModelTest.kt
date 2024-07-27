package viewmodel.settings

import model.UserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import userinterface.settings.SettingsViewEvent
import viewmodel.SettingsViewModel
import kotlin.test.Test

class SettingsViewModelTest {
    private lateinit var model: UserModel
    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        model = UserModel()
        viewModel = SettingsViewModel(model)
    }


    @Test
    fun testPasswordUpdate() {
        val newPassword = "newpassword"

        viewModel.invoke(SettingsViewEvent.ChangePasswordEvent, newPassword, null)
        assertEquals(newPassword, model.password)
    }

    @Test
    fun testUpdateUnits() {
        val newHeightUnits = "inches"
        val newWeightUnits = "pounds"

        viewModel.invoke(SettingsViewEvent.UnitsConversionEvent, "height", newHeightUnits)
        viewModel.invoke(SettingsViewEvent.UnitsConversionEvent, "weight", newWeightUnits)

        assertEquals(newHeightUnits, model.heightUnits)
        assertEquals(newWeightUnits, model.weightUnits)
    }

    @Test
    fun testClearingDataOnSignout() {
        viewModel.invoke(SettingsViewEvent.SignOutEvent, "", "")

        assert(!model.loggedIn)
        assertEquals(model.email, "")
        assertEquals(model.password, "")

    }

}