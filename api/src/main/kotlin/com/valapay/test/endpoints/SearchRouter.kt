package com.valapay.test.endpoints

import com.valapay.test.annotations.AsyncHandler
import com.valapay.test.annotations.Endpoint
import com.valapay.test.annotations.EndpointController
import com.valapay.test.annotations.HttpMethodName
import com.valapay.test.messages.api.SearchRequest

@EndpointController("/util")
interface SearchRouter {
    @Endpoint(HttpMethodName.GET, param = "id")
    fun search(id: String): String

    @Endpoint(method = HttpMethodName.POST)
    @AsyncHandler
    fun search(searchRequest: SearchRequest) // we have no check or response type
}