package com.medofic.api.data.classes.DTO.Requests

import com.medofic.api.data.classes.Appointment

data class AppointmentForDoctorRequest(val snils:String, val appointment: Appointment)