package com.service.micronaut

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.service.micronaut")
		.start()
}

