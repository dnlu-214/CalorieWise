import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import model.UserModel
import userinterface.UserView
import viewmodel.UserViewModel
import userinterface.composables.Screens
import userinterface.theme.MyApplicationTheme

fun main() = application {

    val userModel = UserModel()
    val userViewModel = UserViewModel(userModel)

    Window(
        title = "CalorieWise",
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(1366.dp, 768.dp)
        ),
        resizable = true,
        onCloseRequest = ::exitApplication,
        onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown && keyEvent.isCtrlPressed &&
                userViewModel.currentScreen.value != Screens.Login.screen
            ) {
                when (keyEvent.key) {
                    Key.H -> {
                        //println("H key pressed")
                        userViewModel.currentScreen.value = Screens.Homepage.screen
                    }

                    Key.R -> {
                        userViewModel.currentScreen.value = Screens.Records.screen
                    }

                    Key.A -> {
                        userViewModel.currentScreen.value = Screens.Analysis.screen
                    }

                    Key.P -> {
                        userViewModel.currentScreen.value = Screens.BasicInfo.screen
                    }

                    Key.S -> {
                        userViewModel.currentScreen.value = Screens.Settings.screen
                    }
                }
            }
            false
        }
    ) {
        MyApplicationTheme(isInDarkTheme = userModel.isInDarkTheme) {
            UserView(userViewModel)
        }
    }

}




