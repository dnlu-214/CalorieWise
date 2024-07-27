package userinterface.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import userinterface.composables.*
import viewmodel.SettingsViewModel

enum class SettingsViewEvent {
    SignOutEvent, ChangePasswordEvent, ChangeThemeEvent, UnitsConversionEvent, FavoritesEditingEvent
}

@Composable
fun SettingsView(
    settingsViewModel: SettingsViewModel, onSignOutSuccess: () -> Unit,
    overlayVisible: Boolean, settingsType: String
) {
    val viewmodel by remember { mutableStateOf(settingsViewModel) }
    var overlayVisible by remember { mutableStateOf(overlayVisible) }
    var settingsType by remember { mutableStateOf(settingsType) }
    var showMessagePrompt by remember { mutableStateOf(false) }
    var editFavType by remember { mutableStateOf("food") }

    @Composable
    fun changePasswordPrompt() {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                viewmodel.email.value,
                label = { Text("E-mail: ") },
                enabled = false,
                onValueChange = {},
                leadingIcon = {
                    Icon(
                        painter = painterResource("icons/EmailIcon.png"),
                        contentDescription = "Email",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            TextField(
                viewmodel.password.value,
                label = { Text("New Password: ") },
                onValueChange = {
                    viewmodel.passwordEdited = true
                    viewmodel.password.value = it
                    viewmodel.settingsMessage.value = ""
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource("icons/PasswordIcon.png"),
                        contentDescription = "Password",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            Button(
                onClick = {
                    viewmodel.invoke(SettingsViewEvent.ChangePasswordEvent, viewmodel.password.value, 1)
                    showMessagePrompt = true
                    viewmodel.passwordEdited = false
                },
                enabled = viewmodel.passwordEdited,
                modifier = Modifier.width(100.dp)
            ) {
                Text("Save")
            }
        }
    }

    @Composable
    fun themeSwitch() {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewmodel.isInDarkTheme.value) {
                Text("Switch to Light Theme")
            } else {
                Text("Switch to Dark Theme")
            }
            Switch(
                checked = viewmodel.isInDarkTheme.value,
                onCheckedChange = {
                    viewmodel.isInDarkTheme.value = it
                    viewmodel.invoke(SettingsViewEvent.ChangeThemeEvent, 1, 1)
                }
            )
        }
    }

    @Composable
    fun unitsDropDown(type: String) {
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.width(350.dp)) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val description =
                    when (type) {
                        "Height" -> viewmodel.heightUnits.value
                        "Weight" -> viewmodel.weightUnits.value
                        "Food" -> viewmodel.foodUnits.value
                        "Drink" -> viewmodel.drinkUnits.value
                        "Exercise" -> viewmodel.exerciseUnits.value
                        else -> assert(false)
                    }
                Text("$description ($type)", modifier = Modifier.weight(1f))

                // Button to trigger the dropdown menu
                Button(onClick = { expanded = !expanded }) {
                    Text("Select Units")
                }

                // DropdownMenu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val items =
                        when (type) {
                            "Height" -> heightUnitsList
                            "Weight" -> weightUnitsList
                            "Food" -> foodUnitsList
                            "Drink" -> drinkUnitsList
                            "Exercise" -> exerciseUnitsList
                            else -> listOf<String>()
                        }

                    items.forEach { item ->
                        DropdownMenuItem(onClick = {
                            viewmodel.invoke(SettingsViewEvent.UnitsConversionEvent, type, item)
                            viewmodel.settingsMessage.value = "'$type' units has been changed to '$item'!"
                            showMessagePrompt = true
                            expanded = false
                        }) {
                            Text(item)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun changeUnitPrompt() {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            unitsDropDown("Height")
            unitsDropDown("Weight")
            unitsDropDown("Food")
            unitsDropDown("Drink")
            unitsDropDown("Exercise")
        }
    }

    @Composable
    fun FavoriteEntry(
        name: String,
        favIconPath: String,
        onFavClicked: () -> Unit,
    ) {
        Row(
            modifier = Modifier.border(1.dp, Color.Gray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .padding(5.dp)
                    .width(220.dp)
                    .align(Alignment.CenterVertically)
            )
            Icon(
                painter = painterResource(favIconPath),
                contentDescription = "FavIcon",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(5.dp)
                    .size(30.dp)
                    .clickable { onFavClicked() }
            )
        }
    }

    @Composable
    fun editFavoritesPrompt() {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        modifier = Modifier.width(110.dp), onClick = { editFavType = "food" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (editFavType == "food") MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
                        )
                    ) {
                        Text("Food")
                    }
                    Button(
                        modifier = Modifier.width(110.dp), onClick = { editFavType = "drink" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (editFavType == "drink") MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
                        )
                    ) {
                        Text("Drink")
                    }
                    Button(
                        modifier = Modifier.width(110.dp), onClick = { editFavType = "exercise" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (editFavType == "exercise") MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
                        )
                    ) {
                        Text("Exercise")
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(16.dp).height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    viewmodel.invoke(SettingsViewEvent.FavoritesEditingEvent, editFavType, 1)

                    if (viewmodel.FavouriteList.isEmpty()) {
                        item { Text("Nothing's been added to Favourite!") }
                    }
                    items(viewmodel.FavouriteList.size) { index ->
                        val recordItem = viewmodel.FavouriteList[index]
                        var favClicked by remember {
                            mutableStateOf(
                                viewmodel.getFavourite(
                                    recordItem,
                                    editFavType
                                )
                            )
                        }
                        FavoriteEntry(
                            recordItem,
                            if (favClicked) "icons/FavClicked.png" else "icons/FavUnclicked.png",
                            {
                                favClicked = !favClicked
                                viewmodel.updateFavourite(recordItem, editFavType, favClicked)
                            })
                    }
                }
            }
        }
    }


    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Settings", style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.height(40.dp))

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {

                // Change password
                item {
                    OutlinedButton(
                        onClick = {
                            overlayVisible = true
                            settingsType = "password"
                        },
                        modifier = Modifier.width(275.dp)
                    ) {
                        Text(
                            "Change Password",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                // Theme switch
                item {
                    OutlinedButton(
                        onClick = {
                            overlayVisible = true
                            settingsType = "theme"
                        },
                        modifier = Modifier.width(275.dp)
                    ) {
                        Text(
                            "Change Theme",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                // Unit conversion
                item {
                    OutlinedButton(
                        onClick = {
                            overlayVisible = true
                            settingsType = "unit"
                        },
                        modifier = Modifier.width(275.dp)
                    ) {
                        Text(
                            "Change Units",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                // Favorite editing
                item {
                    OutlinedButton(
                        onClick = {
                            overlayVisible = true
                            settingsType = "favorite"
                        },
                        modifier = Modifier.width(275.dp)
                    ) {
                        Text(
                            "Edit Favorites",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                // Logout
                item {
                    OutlinedButton(
                        onClick = {
                            viewmodel.invoke(SettingsViewEvent.SignOutEvent, 1, 1)
                            if (!viewmodel.loggedin) {
                                onSignOutSuccess()
                            } else {
                                viewmodel.settingsMessage.value = "Something went wrong, you are still logged in."
                                settingsType = "logoutFail"
                                showMessagePrompt = true
                            }
                        },
                        modifier = Modifier.width(275.dp)
                    ) {
                        Text(
                            "Log out",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }

        // Overlay area
        if (overlayVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background.copy(alpha = 0.8f), shape = RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp)
                        .sizeIn(maxWidth = 500.dp, maxHeight = 600.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        when (settingsType) {
                            "password" -> changePasswordPrompt()
                            "theme" -> themeSwitch()
                            "unit" -> changeUnitPrompt()
                            "favorite" -> editFavoritesPrompt()
                            else -> assert(false)
                        }

                        Button(
                            onClick = {
                                overlayVisible = false
                                settingsType = ""
                                viewmodel.settingsMessage.value = ""
                            },
                            modifier = Modifier.width(100.dp)
                        ) {
                            Text("Back")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (showMessagePrompt) {
                            when (settingsType) {
                                "logoutFail" -> MessagePrompt(
                                    viewmodel.settingsMessage.value,
                                    { showMessagePrompt = false },
                                    "error"
                                )

                                else -> MessagePrompt(
                                    viewmodel.settingsMessage.value,
                                    { showMessagePrompt = false },
                                    "message"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
