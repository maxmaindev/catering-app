package com.example.cateringapp.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


object DateSerializer : KSerializer<LocalDateTime> {
    override val descriptor= PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val epochMilli = value.toInstant(ZoneOffset.UTC).toEpochMilli()
        encoder.encodeLong(epochMilli)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val epochMilli = decoder.decodeLong()
        val instant = Instant.ofEpochMilli(epochMilli)
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
//        val epochMilli = decoder.decodeLong()
//        val localTime = LocalDateTime.from(Instant.ofEpochMilli(epochMilli))
//        return localTime

    }
}