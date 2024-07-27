package viewmodel.basicInfo

import model.UserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.*
import viewmodel.BasicInformationViewModel
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.test.Test

class BasicInformationViewModelTest {
    private lateinit var model: UserModel
    private lateinit var viewModel: BasicInformationViewModel
    private lateinit var mockConnection: Connection
    private lateinit var mockPreparedStatement: PreparedStatement
    private lateinit var mockResultSet: ResultSet

    @BeforeEach
    fun setUp() {
        model = UserModel()
        viewModel = BasicInformationViewModel(model)

        // Mock the database connection
        mockConnection = mock(Connection::class.java)
        mockPreparedStatement = mock(PreparedStatement::class.java)
        mockResultSet = mock(ResultSet::class.java)

        `when`(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement)
        `when`(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet)
        `when`(mockResultSet.next()).thenReturn(true)

    }

    @Test
    fun testUpdateBasicInformation() {
        val gender = "F"
        val age = 25
        val height = 165
        val weight = 60
        val goalWeight = 50

        viewModel.gender.value = gender
        viewModel.age.value = age.toString()
        viewModel.height.value = height.toString()
        viewModel.weight.value = weight.toString()
        viewModel.goalWeight.value = goalWeight.toString()
        viewModel.updateBasicInformation()

        assertEquals(height, viewModel.height.value.toInt())
        assertEquals(weight, viewModel.weight.value.toInt())
    }

    @Test
    fun testCalorieCalculation() {
        viewModel.gender.value = "F"
        viewModel.age.value = "25"
        viewModel.height.value = "170"
        viewModel.weight.value = "65"
        viewModel.goalWeight.value = "60"

        viewModel.calculateRecommendation()

        val expectedCalories = viewModel.calorie.value

        assertEquals(expectedCalories, 1824)
    }


}