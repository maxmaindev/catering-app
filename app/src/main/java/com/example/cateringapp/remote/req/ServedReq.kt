package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class ServedReq(
    var isServed: Boolean
)
