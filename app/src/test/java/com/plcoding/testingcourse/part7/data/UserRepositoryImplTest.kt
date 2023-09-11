package com.plcoding.testingcourse.part7.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRepositoryImplTest{

    private lateinit var useriApi: UserApiFake
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @BeforeEach
    fun setUp(){

        useriApi = UserApiFake()
        userRepositoryImpl = UserRepositoryImpl(useriApi)
    }

    @Test
    fun `getting profile data`() = runBlocking{
        val profileResult = userRepositoryImpl.getProfile("1")

        assertThat(profileResult.isSuccess).isTrue()
        assertThat(profileResult.getOrThrow().user.id).isEqualTo("1")

        val expected = useriApi.posts.filter { it.userId == "1" }
        assertThat(profileResult.getOrThrow().posts).isEqualTo(expected)
    }
}