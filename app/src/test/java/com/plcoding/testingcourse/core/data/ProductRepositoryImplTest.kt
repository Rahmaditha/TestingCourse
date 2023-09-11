package com.plcoding.testingcourse.core.data

import assertk.assertThat
import assertk.assertions.isTrue
import com.plcoding.testingcourse.core.domain.AnalyticsLogger
import com.plcoding.testingcourse.core.domain.LogParam
import com.plcoding.testingcourse.core.domain.Product
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ProductRepositoryImplTest{
    private lateinit var repository: ProductRepositoryImpl
    private lateinit var productApi: ProductApi
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun setUp(){
        mockWebServer = MockWebServer()
        productApi = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create()
        analyticsLogger = mockk(relaxed = true)
        repository = ProductRepositoryImpl(productApi, analyticsLogger)
    }

    @Test
    fun `Response error, exception logged - MockWebServer`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )
        val result = repository.purchaseProducts(listOf())

        assertThat(result.isFailure).isTrue()
        verify{
            analyticsLogger.logEvent(
                "http_error",
                LogParam("code", 404),
                LogParam("message", "Client Error")
            )
        }
    }

    @Test
    fun `Response error, exception is Logged`() = runBlocking {
        coEvery { productApi.purchaseProducts(any()) } throws mockk<HttpException>{
            every { code() } returns 404
            every { message() } returns "Test message"
        }

        mockkConstructor(Product::class)
        every { anyConstructed<Product>().name } returns "Mocked Ice Cream"

        val result = repository.purchaseProducts(listOf())

        assertThat(result.isFailure).isTrue()

        verify(exactly = 1) {
            analyticsLogger.logEvent(
                "http_error",
                LogParam("code", 404),
                LogParam("message", "Test message")
            )
        }
    }

    @Test
    fun `Response error, exception is Logged with specified product class`() = runBlocking {
        coEvery { productApi.purchaseProducts(
            match {
                it.products.any { it.price > 5.0 }
            })
        } throws mockk<HttpException>{
            every { code() } returns 404
            every { message() } returns "Test message"
        }

        val result = repository.purchaseProducts(listOf(
            Product(id = 1, name = "Ice Cream", price = 6.0)
        ))

        assertThat(result.isFailure).isTrue()

        verify(exactly = 1) {
            analyticsLogger.logEvent(
                "http_error",
                LogParam("code", 404),
                LogParam("message", "Test message")
            )
        }
    }
}