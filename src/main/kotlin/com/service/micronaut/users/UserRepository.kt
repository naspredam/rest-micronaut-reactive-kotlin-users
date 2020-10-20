package com.service.micronaut.users

import io.micronaut.transaction.annotation.ReadOnly
import io.micronaut.transaction.annotation.TransactionalAdvice
import javax.inject.Singleton
import javax.persistence.EntityManager

@Singleton
open class UserRepository (private val entityManager: EntityManager) {

    @ReadOnly
    open fun findAll(): List<UserData> =
            entityManager
                    .createQuery("select u from UserData u", UserData::class.java)
                    .resultList

    @ReadOnly
    open fun findById(userId: Long): UserData = entityManager.find(UserData::class.java, userId)

    @TransactionalAdvice
    open fun save(userData: UserData): UserData {
        entityManager.persist(userData)
        return userData
    }

    @TransactionalAdvice
    open fun deleteById(userId: Long) {
        val userData = entityManager.find(UserData::class.java, userId)
        entityManager.remove(userData)
    }
}