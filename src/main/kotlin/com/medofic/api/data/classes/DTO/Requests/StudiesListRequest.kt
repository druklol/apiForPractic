package com.medofic.api.data.classes.DTO.Requests

import com.medofic.api.data.classes.Enums.Gender

data class StudiesListRequest(val age:Int, val gender:Gender)
