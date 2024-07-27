package viewmodel

import DatabaseManager
import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber
import userinterface.login.LoginPageViewEvent
import java.sql.Connection


class LoginPageViewModel(val model: UserModel) : ISubscriber {
    // we can cast `Any` later since each event has an associated type
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    var loggedin = false
    var loginFailed = false
    var loginMessage = mutableStateOf("")
    var returning = false
    var showMessagePrompt = mutableStateOf(false)


    init {
        model.subscribe(this)
    }

    private fun signInOrSignUp(buttonPressed: String) {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
//        println("Checking if your username exist in our database. ")
        val exist = connection.checkUserExist(model.email)
//        println("Your exist status is ${exist}. ")
        if (exist == 0) {
            if (buttonPressed == "signin") {
                showMessagePrompt.value = true
                loginMessage.value = "Please create an account first."
                return
            }
//            println("Welcome! You are a new user. Signing you up.")
            val signUpSuccessCode = connection.signUp(model.email, model.password)
            if (signUpSuccessCode == 1) {
//                println("Great, you are signed up!")
                model.loggedIn = true
                return
            }
        } else if (exist == 1) {
            if (buttonPressed == "signup") {
                showMessagePrompt.value = true
                loginMessage.value = "The account already exists."
                return
            }
//            println("Welcome! You are a returning user. Logging you in.")
            returning = true
            val logInSuccessCode = connection.logInOrInterrupt(model.email, model.password)
            if (logInSuccessCode == 1) {
                model.loggedIn = true
//                println("Welcome! You are logged in!")
                return
            } else if (logInSuccessCode == 0) {
                showMessagePrompt.value = true
                loginMessage.value = "Oops! Incorrect email or password. Please try again."
            } else {
//                println("??????? Something wrong happened during login ???????")
            }
        } else {
//            println("??????? Something wrong happened during exist check ???????")
        }
        loginFailed = true
    }

    private fun Connection.signUp(username: String, password: String): Int {
        try {
            val stmt = createStatement()
            stmt.executeUpdate("INSERT INTO Users (username, password) VALUES ('${username}', '${password}');")
            stmt.close()
        } catch (exception: Exception) {
            println(exception)
        }
        var exist = -1
        try {
            val query = prepareStatement("SELECT COUNT(*) AS user_count FROM Users WHERE username = '${username}';")
            val result = query.executeQuery()
            result.next()
            exist = result.getInt("user_count")
            result.close()
            query.close()

        } catch (exception: Exception) {
            println(exception)
        }
        return exist
    }

    private fun Connection.logInOrInterrupt(username: String, password: String): Int {
        var realPassword = "?"
        try {
            val query = prepareStatement("SELECT password FROM Users WHERE username = '${username}';")
            val result = query.executeQuery()
            result.next()
            realPassword = result.getString("password")
            result.close()
            query.close()

        } catch (exception: Exception) {
            println(exception)
        }
        if (realPassword == "?") {
            return -1
        } else if (realPassword == password) {
            return 1
        } else {
            return 0
        }
    }

    private fun Connection.checkUserExist(username: String): Int {
        var exist = -1
        try {
            val query = prepareStatement("SELECT COUNT(*) AS user_count FROM Users WHERE username = '${username}';")
            val result = query.executeQuery()
            result.next()
            exist = result.getInt("user_count")
            result.close()
            query.close()

        } catch (exception: Exception) {
            println(exception)
        }
        return exist
    }


    fun invoke(event: LoginPageViewEvent, value: Any?) {
        when (event) {
            LoginPageViewEvent.EmailEvent -> model.email = value as String
            LoginPageViewEvent.PasswordEvent -> model.password = value as String
            LoginPageViewEvent.SignInEvent -> signInOrSignUp(value as String)
            else -> assert(false)
        }
    }

    override fun update() {
        email.value = model.email
        password.value = model.password
        loggedin = model.loggedIn
    }
}