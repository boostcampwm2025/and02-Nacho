package com.andlife.InvitationServer.controller

import com.andlife.InvitationServer.entity.Sample
import com.andlife.InvitationServer.service.SampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val sampleService: SampleService
) {

    @GetMapping("/")
    fun getSamples(): List<Sample> {
        return sampleService.getAllSamples()
    }

}