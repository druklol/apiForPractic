package com.medofic.api.controllers

import com.medofic.api.data.classes.DTO.Requests.ProtocolRequest
import com.medofic.api.data.classes.DTO.Requests.ProtocolsRequest
import com.medofic.api.data.classes.ProtocolFile
import com.medofic.api.services.PatientService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
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

    /**
     * Getting information about all available protocols in user directory
     *
     * @property request body from post request. Must contain snils(format: ddd ddd ddd dd)
     * @return list with all available protocols
     */
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

    /**
     * Get a specific protocol in user directory
     *
     * @property request body from post request. Must contain snils(format: ddd ddd ddd dd) and fileName
     * @return file - protocol
     */
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
}