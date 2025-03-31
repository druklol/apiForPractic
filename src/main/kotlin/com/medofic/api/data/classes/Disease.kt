package com.medofic.api.data.classes

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Table(name = "Diseases")
@Entity
data class Disease(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    val id: Long? = null,
    val mkbCodes:String,
    @Lob
    val name:String,
    val frequencyVisits:String,
    @Lob
    val controlledIndicators:String,
    val durationObservation:String,
    @Lob
    val note:String
)
