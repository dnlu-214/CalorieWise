package model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import userinterface.ISubscriber
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class IPublisherTest{
    @Mock
    private lateinit var publisher: IPublisher

    @Mock
    private lateinit var subscriber1: ISubscriber

    @BeforeEach
    fun setUp() {
        publisher = object : IPublisher() {
        }
    }

    @Test
    fun testSubscriberUpdate() {
        // Arrange
        publisher.subscribe(subscriber1)

        // Act
        publisher.notifySubscribers()

        // Assert
        verify(subscriber1, times(1)).update()
    }
    @Test
    fun testNothingNotified() {
        publisher.notifySubscribers()

        verify(subscriber1, never()).update()
    }
}
