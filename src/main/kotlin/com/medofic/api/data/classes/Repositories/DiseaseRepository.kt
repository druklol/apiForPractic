package com.medofic.api.data.classes.Repositories

import com.medofic.api.data.classes.Disease
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DiseaseRepository : JpaRepository<Disease, Long> {
    @Query("SELECT d FROM Disease d WHERE d.mkbCodes LIKE %:mkbCode%")
    fun findByMkbCode(@Param("mkbCode") mkbCode: String): List<Disease>
}