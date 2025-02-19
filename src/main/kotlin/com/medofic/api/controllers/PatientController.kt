package com.medofic.api.controllers

import com.medofic.api.services.PatientService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/patient")
class PatientController(private val patientService: PatientService) {

    @GetMapping("/resolution")
    fun getResolution(@RequestBody request: Map<String, String>): ResponseEntity<FileSystemResource> {
        val snils = request["snils"] ?: return ResponseEntity.badRequest().build()
        val resolutionFile = patientService.getResolutionFileBySnils(snils)

        return if(resolutionFile?.exists() == true){
            val resource = FileSystemResource(resolutionFile)

            val headers = HttpHeaders().apply {
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${resolutionFile.name}")
                add(HttpHeaders.CONTENT_TYPE, "application/pdf")
            }

            ResponseEntity
                .ok()
                .headers(headers)
                .body(resource)
        }
        else {
            ResponseEntity.notFound().build()
        }
    }
}