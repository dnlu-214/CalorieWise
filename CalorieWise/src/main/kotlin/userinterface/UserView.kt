package userinterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import userinterface.analysis.AnalysisPageView
import userinterface.basicInfo.BasicInformationPage
import userinterface.basicInfo.recommendation.RecommendationPage
import userinterface.composables.Screens
import userinterface.homepage.HomepageView
import userinterface.login.LoginPageView
import userinterface.records.RecordsView
import userinterface.records.addDrink.AddDrinkView
import userinterface.records.addExercise.AddExerciseView
import userinterface.records.addFood.AddFoodView
import userinterface.settings.SettingsView
import userinterface.theme.MyApplicationTheme
import viewmodel.*

@Composable
fun UserView(userViewModel: UserViewModel) {
    val viewModel by remember { mutableStateOf(userViewModel) }

    val loginPageViewModel = LoginPageViewModel(viewModel.model)
    val homepageViewModel = HomepageViewModel(viewModel.model)
    val basicInformationViewModel = BasicInformationViewModel(viewModel.model)
    val recordsViewModel = RecordsViewModel(viewModel.model)
    val analysisPageViewModel = AnalysisPageViewModel(viewModel.model)
    viewModel.updateModel()
    recordsViewModel.updateView()
    basicInformationViewModel.updateView()
    val settingsViewModel = SettingsViewModel(viewModel.model)

    // Maintain the current screen using rememberSaveable
    var currentScreen by rememberSaveable { userViewModel.currentScreen }
    var focusedButton by rememberSaveable { mutableStateOf("") }
    var recordsOverlay = false
    var recordsType = ""
    var settingsOverlay = false
    var settingsType = ""


    @Composable
    fun SidebarImageButton(imageRes: String, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(25.dp, 15.dp, 15.dp, 0.dp)
                .clickable {
                    onClick()
                    focusedButton = imageRes
                }
                .background(
                    color = if (focusedButton == imageRes) MaterialTheme.colors.secondaryVariant else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "graph icon",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }



    MyApplicationTheme(isInDarkTheme = viewModel.isInDarkTheme.value) {
        Surface(color = MaterialTheme.colors.background) {
            // Content area
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Check if the current screen is not the LoginPage, then display the sidebar
                if (currentScreen != Screens.Login.screen) {
                    // Sidebar with buttons to navigate to different screens
                    LazyColumn(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        item {
                            SidebarImageButton("icons/Home.png") { currentScreen = Screens.Homepage.screen }
                            SidebarImageButton("icons/Analysis.png") { currentScreen = Screens.Analysis.screen }
                            SidebarImageButton("icons/Records.png") { currentScreen = Screens.Records.screen }
                            SidebarImageButton("icons/Profile.png") { currentScreen = Screens.BasicInfo.screen }
                            SidebarImageButton("icons/Settings.png") { currentScreen = Screens.Settings.screen }
                        }
                    }
                }

                // Content area
                when (currentScreen) {
                    Screens.Login.screen -> LoginPageView(
                        loginPageViewModel,
                        {
                            viewModel.updateModel()
                            recordsViewModel.updateView()
                            basicInformationViewModel.updateView()
                            currentScreen = Screens.BasicInfo.screen
                        },
                        {
                            viewModel.updateModel()
                            recordsViewModel.updateView()
                            basicInformationViewModel.updateView()
                            currentScreen = Screens.Homepage.screen
                        })

                    Screens.Homepage.screen -> {
                        HomepageView(
                            homepageViewModel,
                            {
                                currentScreen = Screens.Records.screen
                                recordsOverlay = true
                                recordsType = "food"
                            },
                            {
                                currentScreen = Screens.Records.screen
                                recordsOverlay = true
                                recordsType = "drink"
                            },
                            {
                                currentScreen = Screens.Records.screen
                                recordsOverlay = true
                                recordsType = "exercise"
                            }
                        )
                        focusedButton = "icons/Home.png"
                    }

                    Screens.BasicInfo.screen -> {
                        BasicInformationPage(
                            basicInformationViewModel,
                            { currentScreen = Screens.Recommendation.screen })
                        focusedButton = "icons/Profile.png"
                    }

                    Screens.Recommendation.screen -> {
                        RecommendationPage(
                            basicInformationViewModel
                        ) {
                            viewModel.updateModel()
                            recordsViewModel.updateView()
                            basicInformationViewModel.updateView()
                            currentScreen = Screens.Homepage.screen
                        }
                        focusedButton = "icons/Profile.png"
                    }

                    Screens.Records.screen -> {
                        RecordsView(
                            recordsViewModel,
                            recordsOverlay,
                            recordsType
                        )
                        focusedButton = "icons/Records.png"
                    }

                    Screens.AddFood.screen -> AddFoodView()
                    Screens.AddDrink.screen -> AddDrinkView()
                    Screens.AddExercise.screen -> AddExerciseView()

                    Screens.Analysis.screen -> {
                        AnalysisPageView(analysisPageViewModel)
                        focusedButton = "icons/Analysis.png"
                    }

                    Screens.Settings.screen -> {
                        SettingsView(
                            settingsViewModel, { currentScreen = Screens.Login.screen },
                            settingsOverlay, settingsType
                        )
                        focusedButton = "icons/Settings.png"
                    }
                }
                recordsOverlay = false
                recordsType = ""
            }
        }
    }
}