package com.medofic.api.data.classes.Interfaces

import com.medofic.api.data.classes.*
import io.ktor.client.*
import java.io.File

interface OnkorInterface {
    val client:HttpClient

    fun getDiseases() : List<Disease>
    fun getUser(snils:String) : User
    fun getDispensaryObservations(snils: String):List<DispensaryObservation>
    fun getAppointments(snils:String):List<Appointment>
    fun getProtocols(snils:String):List<ProtocolFile>
    fun getProtocol(snils:String, protocolName:String):File
    fun getDisease(snils:String, diseaseName:String):Disease
}