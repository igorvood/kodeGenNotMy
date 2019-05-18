package com.valapay.test.messages.api

import com.valapay.test.annotations.frontend.IsEmail
import com.valapay.test.annotations.frontend.IsString

data class SearchRequest(
    @field:IsString
    val author: String?,

    @field:IsEmail
    val someEmail: String,

    @field:IsString
    val title: String?
)
