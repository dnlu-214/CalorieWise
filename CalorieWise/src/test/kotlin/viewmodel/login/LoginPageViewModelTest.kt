package viewmodel.login

import model.UserModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import userinterface.login.LoginPageViewEvent
import viewmodel.LoginPageViewModel
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.test.Test

class LoginPageViewModelTest {
    private lateinit var model: UserModel
    private lateinit var loginViewModel: LoginPageViewModel
    private lateinit var mockConnection: Connection
    private lateinit var mockPreparedStatement: PreparedStatement
    private lateinit var mockResultSet: ResultSet
    private lateinit var spyLoginViewModel: LoginPageViewModel

    @BeforeEach
    fun setUp() {
        model = UserModel()
        loginViewModel = LoginPageViewModel(model)

        mockConnection = mock(Connection::class.java)
        mockPreparedStatement = mock(PreparedStatement::class.java)
        mockResultSet = mock(ResultSet::class.java)
        spyLoginViewModel = spy(loginViewModel)

        //`when`(userViewModel.connect()).thenReturn(mockConnection)
        //doReturn(mockConnection).`when`(spyLoginViewModel).connect()
        `when`(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement)
        `when`(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet)
        `when`(mockResultSet.next()).thenReturn(true)
    }

    @Test
    fun testSignInSuccess() {
        // Set user email and password
        model.email = "test@example.com"
        model.password = "11111"

        `when`(mockResultSet.next()).thenReturn(true)
        `when`(mockResultSet.getInt("user_count")).thenReturn(1)
        `when`(mockResultSet.getString("password")).thenReturn("11111")

        loginViewModel.invoke(LoginPageViewEvent.SignInEvent, null)

        // Assert that the user is logged in
        assertTrue(loginViewModel.loggedin)
    }


}