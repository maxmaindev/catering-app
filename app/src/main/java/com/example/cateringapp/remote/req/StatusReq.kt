package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class StatusReq(
    var status: String
)
