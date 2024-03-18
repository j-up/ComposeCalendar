package com.jm.calendar

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent

import android.os.Bundle
import androidx.compose.material.ExperimentalMaterialApi
import com.jm.calendar.screens.main.MainScreen
import com.jm.calendar.theme.ResourceObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class EntryPointActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResourceObject.CalendarTheme {
                MainScreen()
            }
        }
    }
}