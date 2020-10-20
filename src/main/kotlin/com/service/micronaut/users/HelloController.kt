package com.service.micronaut.users

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/hello")
class HelloController {

    @Get
    fun ping() = "hello..."

}