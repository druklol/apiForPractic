package com.medofic.api.services

import com.medofic.api.data.classes.*
import com.medofic.api.data.classes.Repositories.NotificationRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PatientService(private val notificationRepository: NotificationRepository) {

    private fun findPatientDirectory(snils: String, subDirectory: String): File {
        return File("./src/main/resources/static/${snils}/${subDirectory}")
    }

    private fun findFile(directory: File, fileName: String): File? {
        if (!directory.isDirectory) {
            return null
        }

        directory.walk().forEach { file ->
            if (file.name == fileName) {
                return file
            }
        }
        return null
    }

    private fun createFile(directory: File, fileName: String, text: String): File {
        val file = File(directory, fileName)
        file.createNewFile()
        file.writeText(text)
        return file
    }

    fun getProtocolFile(snils: String, fileName: String): File? {
        val patientDirectory = findPatientDirectory(snils, "protocols")

        return findFile(patientDirectory, fileName)
    }

    fun getAllProtocols(snils: String): MutableList<ProtocolFile> {
        val files = findPatientDirectory(snils, "protocols").listFiles()
        val protocols: MutableList<ProtocolFile> = mutableListOf()

        if (files.isNullOrEmpty())
            return protocols

        files.forEach { file ->
            if (file.name.startsWith(".")) return@forEach

            val splittedFileName = file.name.split("_")

            val lpu = splittedFileName[0]
            val investigationName = splittedFileName[1]
            val doctorName = splittedFileName[2]

            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val date = LocalDate.parse(splittedFileName[3], dateFormatter)

            val time = splittedFileName[4]

            val protocolInfo = ProtocolInfo(lpu, investigationName, doctorName, date, time)
            val protocolFile = ProtocolFile(file.name, protocolInfo)

            protocols.add(protocolFile)
        }
        return protocols
    }

    fun getAppointments(snils: String): List<Appointment> {
        val directory = findPatientDirectory(snils, "")
        val file = findFile(directory, "appointments.json") ?: run {
            createFile(directory, "appointments.json", "[]")
        }

        val appointments = file.readText().let {
            Json.decodeFromString<List<Appointment>>(it)
        }

        return appointments
    }

    fun addAppointment(snils: String, appointment: Appointment) {
        val directory = findPatientDirectory(snils, "")
        val file = findFile(directory, "appointments.json") ?: run {
            createFile(directory, "appointments.json", "[]")
        }

        val appointments = file.readText().let {
            Json.decodeFromString<MutableList<Appointment>>(it)
        }

        appointments.add(appointment)
        file.writeText(Json.encodeToString(appointments))
    }

    fun getNotifications(snils:String) = notificationRepository.findBySnils(snils)

    fun addNotification(notification: Notification) = notificationRepository.save(notification)

    fun getDispensaryObservationsList(snils: String): MutableList<DispensaryObservation> {
        val directory = findPatientDirectory(snils, "")
        val file = findFile(directory, "dispensary_observations.json") ?: run {
            createFile(directory, "dispensary_observations.json", "[]")
        }

        val observations = file.readText().let {
            Json.decodeFromString<MutableList<DispensaryObservation>>(it)
        }

        return observations
    }

    fun addOrChangeDispensaryObservations(snils: String, newObservation: DispensaryObservation) {
        val directory = findPatientDirectory(snils, "")
        val file = findFile(directory, "dispensary_observations.json") ?: run {
            createFile(directory, "dispensary_observations.json", "[]")
        }

        val observations = file.readText().let {
            Json.decodeFromString<MutableList<DispensaryObservation>>(it)
        }

        observations.removeAll { obs ->
            obs.LPU == newObservation.LPU && obs.disease == newObservation.disease
        }

        observations.add(newObservation)

        file.writeText(Json.encodeToString(observations))
    }
}