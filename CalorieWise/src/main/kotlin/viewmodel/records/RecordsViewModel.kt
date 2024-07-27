package viewmodel.records

import model.UserModel
import userinterface.ISubscriber
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class RecordsViewModel(val model: UserModel) : ISubscriber {
    private var recordItem: String = ""
    private var recordAmount: String = ""
    private var recordType: String = ""
    val foodRecords: MutableList<Pair<String, Int>> = mutableListOf()
    val drinkRecords: MutableList<Pair<String, Int>> = mutableListOf()
    val exerciseRecords: MutableList<Pair<String, Int>> = mutableListOf()

    init {
        model.subscribe(this)
    }

    override fun update() {
        recordItem = model.recordItem
        recordAmount = model.recordAmount.toString()
        recordType = model.recordType
    }

    fun addRecord(item: String, amount: String, type: String) {
        recordItem = item
        recordAmount = amount
        recordType = type

        model.recordItem = recordItem
        model.recordAmount = recordAmount.toInt()
        model.recordType = recordType
        insertRecord()
        updateView()
        model.notifySubscribers()
    }

    private fun connect(): Connection? {
        var connection: Connection? = null
        try {
            val appDataDir = System.getProperty("user.home") + File.separator + ".CalorieWise"
            val dbPath = "$appDataDir${File.separator}data.db"
            val url = "jdbc:sqlite:$dbPath"

//            val url = "jdbc:sqlite:src/main/kotlin/data/data.db"
            connection = DriverManager.getConnection(url)
            println("Connection is valid.")
        } catch (e: SQLException) {
            println(e.message)
        }
        return connection
    }

    private fun Connection.insertRecord(
        username: String,
        item: String,
        amount: Int,
        type: String,
//      TODO: add date: Int
    ): Int {
        try {
            val stmt = createStatement()
            stmt.executeUpdate(
                "INSERT INTO Records (username, recordItem, recordType, recordAmount) " +
                        "VALUES ('${username}', '${item}', '${type}', ${amount});"
            )
            stmt.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun Connection.updateView(): Int {
        try {
            val query = prepareStatement(
                "SELECT recordItem, recordAmount, recordType FROM Records WHERE username = '${model.email}';"
            )
            val result = query.executeQuery()
            while (result.next()) {
                val resultType = result.getString("recordType")
                val resultItem = result.getString("recordItem")
                val resultAmount = result.getInt("recordAmount")
                when(resultType) {
                    "food" -> foodRecords.add(Pair(resultItem, resultAmount))
                    "drink" -> drinkRecords.add(Pair(resultItem, resultAmount))
                    "exercise" -> exerciseRecords.add(Pair(resultItem, resultAmount))
                    else -> assert(false)
                }
            }
            result.close()
            query.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun insertRecord() {
        val connection = connect()
        val insertSuccessCode =
            connection?.insertRecord(model.email, model.recordItem, model.recordAmount, model.recordType)
        assert(insertSuccessCode == 1)
    }

    fun updateView() {
        foodRecords.clear()
        drinkRecords.clear()
        exerciseRecords.clear()
        val connection = connect()
        val updateViewSuccessCode = connection?.updateView()
        assert(updateViewSuccessCode == 1)
    }
}