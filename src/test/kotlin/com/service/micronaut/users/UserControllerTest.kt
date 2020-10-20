package com.service.micronaut.users

import com.github.javafaker.Faker
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest.DELETE
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.persistence.EntityManager

private val FAKER: Faker = Faker()

@MicronautTest
internal class UserControllerTest {

    @Inject
    @field:Client("/")
    private lateinit var client: RxHttpClient

    @Inject
    private lateinit var entityManager: EntityManager

    @Test
    fun shouldPersistUserInformation() {
        val userData = UserData(null,
                FAKER.name().firstName(), FAKER.name().lastName(), FAKER.phoneNumber().phoneNumber())

        val userResponse = client.toBlocking()
                .retrieve(POST("/users", userData), Argument.of(UserData::class.java))

        assertThat(userResponse).isNotNull
        assertThat(userResponse.id).isNotNull.isNotNegative
        assertThat(userResponse.firstName).isEqualTo(userData.firstName)
        assertThat(userResponse.lastName).isEqualTo(userData.lastName)
        assertThat(userResponse.phone).isEqualTo(userData.phone)
    }

    @Test
    fun shouldGetEmptyListOfUsers() {
        entityManager.createQuery("select u from UserData u", UserData::class.java)
                .resultList
                .forEach { entityManager.remove(it) }
        entityManager.transaction.commit()
        val userListResponse = client.toBlocking()
                .retrieve(GET<String>("/users"), Argument.listOf(UserData::class.java))

        assertThat(userListResponse).isEmpty()
    }

    @Test
    fun shouldReturnTheUserPersisted() {
        val userData1 = UserData(null,
                FAKER.name().firstName(), FAKER.name().lastName(), FAKER.phoneNumber().phoneNumber())
        val userData2 = UserData(null,
                FAKER.name().firstName(), FAKER.name().lastName(), FAKER.phoneNumber().phoneNumber())
        entityManager.persist(userData1)
        entityManager.persist(userData2)
        entityManager.transaction.commit()

        val usersResponse = client.toBlocking()
                .retrieve(GET<String>("/users"), Argument.listOf(UserData::class.java))

        assertThat(usersResponse).hasSize(2)
                .containsExactlyInAnyOrder(userData1, userData2)
    }

    @Test
    fun shouldReturnTheUserPersistedWhenSearchingById() {
        val userData = UserData(null,
                FAKER.name().firstName(), FAKER.name().lastName(), FAKER.phoneNumber().phoneNumber())
        entityManager.persist(userData)
        entityManager.transaction.commit()
        val user = client.toBlocking()
                .retrieve(GET<String>("/users/" + userData.id), Argument.of(UserData::class.java))
        assertThat(user).isNotNull
        assertThat(user).isEqualTo(userData)
    }

    @Test
    fun shouldDeleteUserPersisted() {
        val userData = UserData(null,
                FAKER.name().firstName(), FAKER.name().lastName(), FAKER.phoneNumber().phoneNumber())
        entityManager.persist(userData)
        entityManager.transaction.commit()

        val exchangeDeleteResponse = client.toBlocking()
                .exchange<String, Void>(DELETE("/users/" + userData.id))
        assertThat(exchangeDeleteResponse.code()).isEqualTo(200)
        
        assertThatThrownBy { client.toBlocking().retrieve(GET<String>("/users/" + userData.id)) }
                .isInstanceOf(HttpClientResponseException::class.java)
    }
}