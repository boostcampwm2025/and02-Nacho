package com.andlife.InvitationServer.repository

import com.andlife.InvitationServer.entity.Sample
import org.springframework.data.jpa.repository.JpaRepository

interface SampleRepository : JpaRepository<Sample, Long>