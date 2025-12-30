package com.andlife.InvitationServer.service

import com.andlife.InvitationServer.entity.Sample
import com.andlife.InvitationServer.repository.SampleRepository

class SampleService(
    private val sampleRepository: SampleRepository
) {
    fun getAllSamples(): List<Sample> = sampleRepository.findAll()
}