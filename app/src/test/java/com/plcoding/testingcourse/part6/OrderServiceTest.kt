package com.plcoding.testingcourse.part6

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.firebase.auth.FirebaseAuth
import com.plcoding.testingcourse.core.domain.Product
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderServiceTest{

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var orderService: OrderService
    private lateinit var emailClient: EmailClient
    private lateinit var mockWebServer: MockWebServer
    private lateinit var order: Order

    @BeforeEach
    fun setUp(){
        firebaseAuth = mockk(relaxed = true)
        mockWebServer = MockWebServer()
        emailClient = mockk(relaxed = true)
        order = mockk()

        orderService = OrderService(
            firebaseAuth,
            emailClient
        )
    }

    @Test
    fun `send email, if user is not anonymous - MockWebServer`(){
        mockkConstructor(FirebaseAuth::class)
        every { anyConstructed<FirebaseAuth>().currentUser } returns firebaseAuth.currentUser
        every { anyConstructed<FirebaseAuth>().currentUser?.isAnonymous } returns false

//        mockkConstructor(Customer::class)
//        every { anyConstructed<Customer>().email } returns "ditharahma12@gmail.com"
//
//        mockkConstructor(Order::class)
//        every { anyConstructed<Order>().productName } returns "Macbook"

//        val result = mockk<OrderService>()
        orderService.placeOrder("ditharahma12@gmail.com", "Macbook")

//        coEvery { result.placeOrder(any(), any()) } returns Unit

        verify {
            emailClient.send(Email(
                subject = "Order Confirmation",
                content ="Thank you for your order of Macbook.",
                recipient = "ditharahma12@gmail.com"
            ))
        }

//        verify { orderService.placeOrder(any(), any()) }
    }
}