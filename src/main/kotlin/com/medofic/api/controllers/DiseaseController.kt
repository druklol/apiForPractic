package com.medofic.api.controllers

import com.medofic.api.data.classes.DTO.Requests.StringRequest
import com.medofic.api.data.classes.Disease
import com.medofic.api.services.DiseaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/diseases")
class DiseaseController(@Autowired private val diseaseService: DiseaseService) {
    @PostMapping("/get_or")
    fun searchDiseases(@RequestBody request:StringRequest): Disease? {
        val mkbCode = request.string
        val foundedDiseases = diseaseService.findDiseaseByMkbCodeSubstring(mkbCode)
        return foundedDiseases.firstOrNull()
    }
}