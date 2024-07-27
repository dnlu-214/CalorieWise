package userinterface.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import userinterface.composables.Appname
import userinterface.composables.MessagePrompt
import viewmodel.LoginPageViewModel

enum class LoginPageViewEvent {
    EmailEvent, PasswordEvent, SignInEvent
}

@Composable
fun LoginPageView(
    loginPageViewModel: LoginPageViewModel, onSignInSuccess: () -> Unit, onReturningUser: () -> Unit
) {
    val viewmodel by remember { mutableStateOf(loginPageViewModel) }
    var passwordVisible by remember { mutableStateOf(false) }
    val emailFocusRequester = remember { FocusRequester() }
    val pwFocusRequester = remember { FocusRequester() }

    val onEnterPressed: () -> Unit = {
        viewmodel.invoke(LoginPageViewEvent.SignInEvent, "signin")
        if (viewmodel.loggedin) {
            if (viewmodel.returning) {
                onReturningUser()
            } else {
                onSignInSuccess()
            }
        }
    }

    val handleEnterEvent: (KeyEvent) -> Boolean = { keyEvent ->
        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
            onEnterPressed()
            true
        } else false
    }



    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().onKeyEvent(handleEnterEvent)) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Appname()

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                viewmodel.email.value,
                label = { Text("E-mail: ") },
                modifier = Modifier.focusRequester(emailFocusRequester)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyUp) {
                            pwFocusRequester.requestFocus()
                            true
                        } else false
                    },
                onValueChange = { viewmodel.invoke(LoginPageViewEvent.EmailEvent, it.trim()) },
                leadingIcon = {
                    Icon(
                        painter = painterResource("icons/EmailIcon.png"),
                        contentDescription = "Email",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                viewmodel.password.value,
                label = { Text("Password: ") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                onValueChange = { viewmodel.invoke(LoginPageViewEvent.PasswordEvent, it.trim()) },
                modifier = Modifier
                    .focusRequester(pwFocusRequester)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyUp) {
                            emailFocusRequester.requestFocus()
                            true
                        } else false
                    },
                leadingIcon = {
                    Icon(
                        painter = painterResource("icons/PasswordIcon.png"),
                        contentDescription = "Password",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                        pwFocusRequester.requestFocus()
                    }) {
                        Icon(
                            painter = painterResource("icons/ViewIcon.png"),
                            contentDescription = "View",
                            tint = Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(45.dp))

            Button(
                onClick = {
                    viewmodel.invoke(LoginPageViewEvent.SignInEvent, "signin")
                    if (viewmodel.loggedin) {
                        if (viewmodel.returning) {
                            onReturningUser()
                        } else {
                            onSignInSuccess()
                        }
                    }
                }) {
                Text("Log In")
            }

            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    viewmodel.invoke(LoginPageViewEvent.SignInEvent, "signup")
                    if (viewmodel.loggedin) {
                        if (viewmodel.returning) {
                            onReturningUser()
                        } else {
                            onSignInSuccess()
                        }
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.primary)
            ) {
                Text("Create Account Here")
            }


            if (viewmodel.showMessagePrompt.value) {
                MessagePrompt(viewmodel.loginMessage.value, { viewmodel.showMessagePrompt.value = false }, "error")
            }
        }
    }
}
