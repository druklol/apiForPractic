package com.medofic.api.controllers

import com.medofic.api.data.classes.DTO.Requests.MkbCodeRequest
import com.medofic.api.data.classes.DTO.Requests.StudiesListRequest
import com.medofic.api.data.classes.Disease
import com.medofic.api.services.DiseaseService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/diseases")
class DiseaseController(@Autowired private val diseaseService: DiseaseService) {
    val logger = LoggerFactory.getLogger(DiseaseController::class.java)

    @PostMapping("/get_or")
    fun searchDiseases(@RequestBody request: MkbCodeRequest): ResponseEntity<Disease> {
        val mkbCode = request.mkbCode
        logger.info("Поиск заболевания по коду $mkbCode")
        val foundedDiseases = diseaseService.findDiseaseByMkbCodeSubstring(mkbCode)
        val disease = foundedDiseases.firstOrNull() ?: return ResponseEntity.badRequest().body(null)
        logger.info("Найдено заболевание: ${disease.name}")
        return ResponseEntity.ok(disease)
    }

    @PostMapping("get_sr")
    fun searchStudiesList(@RequestBody request:StudiesListRequest): ResponseEntity<String> {
        logger.info("Поиск списка исследований")
        return ResponseEntity.ok("- Опрос (анкетирование)\n" +
                "- Расчет индекса массы тела\n" +
                "- Измерение артериального давления\n" +
                "- Определение уровня общего холестерина в крови\n" +
                "- Определение уровня глюкозы в крови\n" +
                "- Определение абсолютного сердечно-сосудистого риска\n" +
                "- Электрокардиография\n" +
                "- Измерение внутриглазного давления\n" +
                "- Прием (осмотр) по результатам профилактического медицинского осмотра\n" +
                "- ОАК\n" +
                "- Краткое индивидуальное профилактическое консультирование\n" +
                "- Прием (осмотр) врачом-терапевтом по результатам первого этапа диспансеризации")
    }

}