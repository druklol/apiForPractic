package com.medofic.api.controllers

import com.medofic.api.data.classes.ProtocolFile
import com.medofic.api.services.PatientService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/patient")
class PatientController(private val patientService: PatientService) {

    @PostMapping("/resolution")
    fun getResolution(@RequestBody request: Map<String, String>): ResponseEntity<FileSystemResource> {
        val snils = request["snils"] ?: return ResponseEntity.badRequest().build()
        val resolutionFile = patientService.getResolutionFileBySnils(snils)

        if(resolutionFile?.exists() != true){
            return ResponseEntity.notFound().build()
        }
        val resource = FileSystemResource(resolutionFile)

        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${resolutionFile.name}")
            add(HttpHeaders.CONTENT_TYPE, "application/pdf")
        }

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
    @PostMapping("/listProtocols")
    fun getAllProtocolsInfo(@RequestBody request: Map<String,String>): ResponseEntity<MutableList<ProtocolFile>> {
        val snils = request["snils"] ?: return ResponseEntity.badRequest().build()
        val protocols = patientService.getAllProtocolsBySnils(snils)

        return ResponseEntity
                .ok()
                .body(protocols)

    }

    /**
     * Get a specific protocol in user directory
     *
     * @property request body from post request. Must contain snils(format: ddd ddd ddd dd) and fileName
     * @return file - protocol
     */
    @PostMapping("/protocol")
    fun getProtocolByName(@RequestBody request: Map<String, String>): ResponseEntity<FileSystemResource> {
        val snils = request["snils"] ?: return ResponseEntity.badRequest().build()
        val fileName = request["fileName"] ?: return ResponseEntity.badRequest().build()

        val protocol = patientService.getProtocolFile(snils, fileName)

        if(protocol?.exists() != true){
            return ResponseEntity.notFound().build()
        }
        val headers = HttpHeaders().apply {
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${protocol.name}")
            add(HttpHeaders.CONTENT_TYPE, "application/pdf")
        }

        val protocolFile = FileSystemResource(protocol)

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(protocolFile)
    }
}