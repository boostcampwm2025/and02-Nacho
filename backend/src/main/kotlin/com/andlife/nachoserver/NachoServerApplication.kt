package com.andlife.nachoserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NachoServerApplication

fun main(args: Array<String>) {
	runApplication<NachoServerApplication>(*args)
}