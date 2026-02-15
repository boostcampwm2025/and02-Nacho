package com.andlife.nachoserver

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class NachoServerApplication {
	@PostConstruct
	fun started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
	}
}

fun main(args: Array<String>) {
	runApplication<NachoServerApplication>(*args)
}
