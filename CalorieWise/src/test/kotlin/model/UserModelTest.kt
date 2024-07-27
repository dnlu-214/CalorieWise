package model

import org.junit.jupiter.api.Assertions.*
import userinterface.ISubscriber
import kotlin.test.Test

internal class UserModelTest {
    @Test
    fun email() {
        val model = UserModel()
        model.email = "test@test.com"
        assert(model.email == "test@test.com")
    }

    @Test
    fun password() {
        val model = UserModel()
        model.password = "22222"
        assert(model.password == "22222")
    }

    @Test
    fun LastnameNotifiesSubscribers() {
        val model = UserModel()
        var notified = false
        model.subscribe(object : ISubscriber {
            override fun update() {
                notified = true
            }
        })

        model.lastname = "Dongni"
        assertEquals("Dongni", model.lastname)
        assertTrue(notified)
    }

    @Test
    fun testUserLogin() {
        val model = UserModel()
        model.loggedIn = true
        assertTrue(model.loggedIn)

        model.loggedIn = false
        assertFalse(model.loggedIn)
    }

    @Test
    fun testCalorieTracking() {
        val model = UserModel()
        model.calorieTaken = 500
        assertEquals(500, model.calorieTaken)

        model.calorieBurned = 100
        assertEquals(100, model.calorieBurned)
    }

}