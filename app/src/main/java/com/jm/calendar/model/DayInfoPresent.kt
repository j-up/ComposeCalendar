package com.jm.calendar.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayInfoPresent(
    val date: String,
    val startTime: String,
    val endTime: String
) : Parcelable