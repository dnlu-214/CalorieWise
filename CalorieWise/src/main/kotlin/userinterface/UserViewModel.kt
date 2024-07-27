package userinterface

import androidx.compose.runtime.mutableStateOf
import model.UserModel

class UserViewModel(val model: UserModel) : ISubscriber {
    var isInDarkTheme = mutableStateOf(false)

    init {
        model.subscribe(this)
    }

    override fun update() {
        isInDarkTheme.value = model.isInDarkTheme
    }
}