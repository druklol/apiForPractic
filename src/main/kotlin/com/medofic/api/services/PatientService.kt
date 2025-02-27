package com.medofic.api.services

import com.medofic.api.data.classes.ProtocolFile
import com.medofic.api.data.classes.ProtocolInfo
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class PatientService {
    fun getResolutionFileBySnils(snils:String): File? {
        val patientDirectory = findDirectoryBySnils(snils,"")

        return findFileByName(patientDirectory, "resolution.pdf")
    }

    fun getProtocolFile(snils: String, fileName: String): File? {
        val patientDirectory = findDirectoryBySnils(snils,"protocols")

        return findFileByName(patientDirectory, fileName)
    }

    fun getAllProtocolsBySnils(snils:String): MutableList<ProtocolFile> {
        val files = findDirectoryBySnils(snils,"protocols").listFiles()
        val protocols:MutableList<ProtocolFile> = mutableListOf()

        files.forEach { file->
            if(file.name.startsWith("."))
                return@forEach

            val name=file.name.split("_")
            val protocolInfo = ProtocolInfo(name[0],name[1], Date(),name[3])
            val protocolFile = ProtocolFile(file.name, protocolInfo)

            protocols.add(protocolFile)
        }
        return protocols
    }

    private fun findDirectoryBySnils(snils: String,subDirectory:String): File {
        return  File("./src/main/resources/static/${snils}/${subDirectory}")
    }

    private fun findFileByName(directory: File, fileName: String): File? {
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