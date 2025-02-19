package com.medofic.api.services

import org.springframework.stereotype.Service
import java.io.File

@Service
class PatientService {
    fun getResolutionFileBySnils(snils:String): File? {
        val patientDirectory = findDirectoryBySnils(snils)
        return findFileByName(patientDirectory, "resolution.pdf")
    }

    private fun findDirectoryBySnils(snils: String): File {
        return  File("/home/druk/IdeaProjects/api/src/main/resources/static/${snils}")
    }

    fun findFileByName(directory: File, fileName: String): File? {
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

}