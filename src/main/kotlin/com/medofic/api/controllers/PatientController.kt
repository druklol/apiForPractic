package com.medofic.api.controllers

import com.medofic.api.data.classes.Appointment
import com.medofic.api.data.classes.DTO.Requests.*
import com.medofic.api.data.classes.Enums.AppointmentRequestStatus
import com.medofic.api.data.classes.Notification
import com.medofic.api.data.classes.ProtocolFile
import com.medofic.api.services.PatientService
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger(PatientController::class.java)

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
    fun getProtocolsInfo(@RequestBody request: ProtocolsRequest): MutableList<ProtocolFile> {
        val protocols = patientService.getAllProtocolsBySnils(request.snils)

        val start = (request.page * request.size).coerceAtMost(protocols.size)
        val end = ((request.page + 1) * request.size).coerceAtMost(protocols.size)
        val paginatedProtocols = protocols.subList(start, end)

        logger.info("${request.snils} запросил список протоколов. Результат: $paginatedProtocols")
        return paginatedProtocols
    }

    @Operation(summary = "Get protocol by snils and fileName")
    @PostMapping("/protocol")
    fun getProtocolByName(@RequestBody request: ProtocolRequest): ResponseEntity<FileSystemResource> {
        val protocol = patientService.getProtocolFile(request.snils, request.fileName)

        return when {
            protocol == null -> {
                logger.error("${request.snils} запросил протокол ${request.fileName} который не был найден.")
                ResponseEntity.badRequest().build()
            }

            else -> {
                logger.info("${request.snils} запросил протокол ${request.fileName}.")
                ResponseEntity.ok()
                    .headers(createPdfHeaders(protocol))
                    .body(FileSystemResource(protocol))
            }
        }
    }

    @Operation(summary = "Get appointments by snils")
    @PostMapping("/appointments")
    fun getAppointments(@RequestBody request: AppointmentRequest): List<Appointment> {
        val appointments = patientService.getAppointmentsBySnils(request.snils)

        return if (request.status == AppointmentRequestStatus.ANY) {
            logger.info("${request.snils} запросил общий список приёмов.")

            appointments
        } else {
            logger.info("${request.snils} запросил список приёмов c фильтром ${request.status.name}.")

            appointments.filter { it.status.name == request.status.name }
        }
    }

    @Operation(summary = "Add appointment by snils")
    @PostMapping("/addAppointment")
    fun addAppointment(@RequestBody request: AppointmentForDoctorRequest): ResponseEntity<String> {
        return try {
            patientService.addAppointmentBySnils(request.snils, request.appointment)
            logger.info("Пользователю ${request.snils} добавлен приём:${request.appointment}")

            ResponseEntity.ok("Appointment added successfully")
        } catch (e: Exception) {
            logger.error("Ошибка при добавлении приёма. Ошибка:${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding appointment: ${e.message}")
        }
    }

    @Operation(summary = "Get notifications by snils")
    @PostMapping("/notifications")
    fun getNotifications(@RequestBody request: SnilsRequest): MutableList<Notification> {
        val notificaions = patientService.getNotificationsBySnils(request.snils)
        logger.info("${request.snils} запросил список уведомлений.")

        return notificaions
    }

    @Operation(summary = "Add notification by snils")
    @PostMapping("/addNotification")
    fun addNotification(@RequestBody request: NotificationRequest): ResponseEntity<String> {
        return try {
            patientService.addNotificationBySnils(request.snils, request.notification)
            logger.info("Пользователю ${request.snils} добавлено уведомление:${request.notification}")

            ResponseEntity.ok("Notification added successfully")
        }
        catch (e:Exception){
            logger.error("Ошибка при добавлении уведомления. Ошибка:${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding appointment: ${e.message}")
        }
    }
}