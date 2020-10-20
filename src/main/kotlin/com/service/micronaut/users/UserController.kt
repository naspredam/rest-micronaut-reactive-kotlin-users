package com.service.micronaut.users

import io.micronaut.http.annotation.*

@Controller("/users")
class UserController(private val userRepository: UserRepository) {

    @Get
    fun retrieveAllUsers() = userRepository.findAll()

    @Get("/{user_id}")
    fun retrieveUserById(@PathVariable("user_id") userId: Long): UserData {
        print(userId)
        return userRepository.findById(userId)
    }

    @Post
    fun saveUser(@Body userData: UserData) = userRepository.save(userData)

    @Delete("/{user_id}")
    fun deleteUserById(@PathVariable("user_id") userId: Long) = userRepository.deleteById(userId)
}