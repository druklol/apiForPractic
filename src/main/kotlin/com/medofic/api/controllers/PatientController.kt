package com.medofic.api.controllers

import com.medofic.api.data.classes.Appointment
import com.medofic.api.data.classes.DTO.Requests.AppointmentForDoctorRequest
import com.medofic.api.data.classes.DTO.Requests.AppointmentRequest
import com.medofic.api.data.classes.DTO.Requests.ProtocolRequest
import com.medofic.api.data.classes.DTO.Requests.ProtocolsRequest
import com.medofic.api.data.classes.Enums.AppointmentStatus
import com.medofic.api.data.classes.ProtocolFile
import com.medofic.api.services.PatientService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("api/v1/patient")
class PatientController(private val patientService: PatientService) {

    private fun createPdfHeaders(resolutionFile: File) = HttpHeaders().apply {
        add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${resolutionFile.name}")
        add(HttpHeaders.CONTENT_TYPE, "application/pdf")
    }

    @Operation(summary = "Not used")
    @PostMapping("/resolution")
    fun getResolution(@RequestBody request: Map<String, String>): ResponseEntity<FileSystemResource> {
        val snils = request["snils"] ?: return ResponseEntity.badRequest().build()
        val resolutionFile = patientService.getResolutionFileBySnils(snils)

        if (resolutionFile?.exists() != true) {
            return ResponseEntity.notFound().build()
        }
        val resource = FileSystemResource(resolutionFile)

        val headers = createPdfHeaders(resolutionFile)

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(resource)
    }

    @Operation(summary = "Gets info about all protocols")
    @PostMapping("/listProtocols")
    fun getProtocolsInfo(@RequestBody request: ProtocolsRequest): ResponseEntity<MutableList<ProtocolFile>> {
        val protocols = patientService.getAllProtocolsBySnils(request.snils)

        val start = (request.page * request.size).coerceAtMost(protocols.size)
        val end = ((request.page + 1) * request.size).coerceAtMost(protocols.size)
        val paginatedProtocols = protocols.subList(start, end)

        return ResponseEntity
            .ok(paginatedProtocols)
    }

    @Operation(summary = "Get protocol by snils and fileName")
    @PostMapping("/protocol")
    fun getProtocolByName(@RequestBody request: ProtocolRequest): ResponseEntity<FileSystemResource> {
        val protocol = patientService.getProtocolFile(request.snils, request.fileName)

        return when {
            protocol == null -> ResponseEntity.badRequest().build()
            !protocol.exists() -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok()
                .headers(createPdfHeaders(protocol))
                .body(FileSystemResource(protocol))
        }
    }

    @Operation(summary = "Get appointments by snils")
    @PostMapping("/appointments")
    fun getAppointments(@RequestBody request: AppointmentRequest): List<Appointment> {
        val appointments = patientService.getAppointmentsBySnils(request.snils)

        return if (request.status == AppointmentStatus.ANY)
            appointments
        else
            appointments.filter { it.status == request.status }
    }

    @Operation(summary = "Set appointment by snils")
    @PostMapping("/setAppointment")
    fun setAppointment(@RequestBody request:AppointmentForDoctorRequest): ResponseEntity<String> {
        return try {
            patientService.setAppointmentBySnils(request.snils, request.appointment)
            ResponseEntity.ok("Appointment set successfully")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error setting appointment: ${e.message}")
        }
    }
}