package com.andlife.InvitationServer.controller

import com.andlife.InvitationServer.entity.Sample
import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.service.SampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val sampleService: SampleService
) {

    @GetMapping("/")
    fun getSamples(): BaseResponse<List<Sample>> {
        val samples = sampleService.getAllSamples()
        return BaseResponse.success(samples)
    }

}