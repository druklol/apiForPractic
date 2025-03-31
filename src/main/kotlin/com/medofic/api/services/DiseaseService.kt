package com.medofic.api.services

import com.medofic.api.data.classes.Disease
import com.medofic.api.data.classes.Repositories.DiseaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DiseaseService(@Autowired private val diseaseRepository: DiseaseRepository) {
    fun findDiseaseByMkbCodeSubstring(mkbCode: String): List<Disease> = diseaseRepository.findByMkbCode(mkbCode)
}