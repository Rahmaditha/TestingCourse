package com.plcoding.testingcourse.shopping.domain

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class ShoppingCartTest{

    private lateinit var cart: ShoppingCart

    @BeforeEach
    fun setUp(){
        cart = ShoppingCart()
    }

    @Test
    fun addMultipleProducts_totalPriceSumCorrect(){
        val product = Product(
            id = 0,
            name = "Ice Cream",
            price = 5.0
        )

        cart.addProduct(product, 4)

        //ACTION
        val priceSum = cart.getTotalCost()

        //ASSERTION
        assertThat(priceSum).isEqualTo(20.0)
    }

    @ParameterizedTest
    @ValueSource(
        ints = [1, 2, 3, 4, 5]
    )
    fun `addMultipleProducts, totalPriceSumCorrect using valuesource`(quantity: Int){
        val product = Product(
            id = 0,
            name = "Ice Cream",
            price = 5.0
        )

        cart.addProduct(product, quantity)

        //ACTION
        val priceSum = cart.getTotalCost()

        //ASSERTION
        assertThat(priceSum).isEqualTo(quantity * product.price)
    }

    @ParameterizedTest
    @CsvSource(
        "2, 10.0",
        "15, 75.0",
        "5, 25.0",
        "8, 40.0"
    )
    fun `addMultipleProducts, totalPriceSumCorrect using csvsource`(
        quantity: Int,
        expectedPriceSum: Double
    ){
        val product = Product(
            id = 0,
            name = "Ice Cream",
            price = 5.0
        )

        cart.addProduct(product, quantity)

        //ACTION
        val priceSum = cart.getTotalCost()

        //ASSERTION
        assertThat(priceSum).isEqualTo(quantity * 5.0)
    }

    @Test
    fun `add product with negative quantity, throws Exception`(){
        val product = Product(
            id = 0,
            name = "Ice Cream",
            price = 5.0
        )

        //ASSERTION
        assertFailure { cart.addProduct(product, -1) }
    }
}