package com.denishovart.freqs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FreqsApplication

fun main(args: Array<String>) {
	runApplication<FreqsApplication>(*args)
}
