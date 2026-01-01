package com.andlife.InvitationServer.response

import org.springframework.http.HttpStatus

interface ResponseCode {
    val httpStatus: HttpStatus
    val message: String
    val name: String
}