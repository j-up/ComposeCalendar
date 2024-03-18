package com.jm.calendar.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jm.calendar.model.DayInfoPresent
import com.jm.calendar.navigation.NavigationConst
import com.jm.calendar.screens.day.DayScreen
import com.jm.calendar.screens.day.DayViewModel
import com.jm.calendar.screens.dayinfo.DayInfoScreen
import com.jm.calendar.screens.home.HomeScreen
import com.jm.calendar.screens.home.HomeViewModel
import java.time.LocalDate

@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    MainScreenNavigation(navController)
}

@Composable
private fun MainScreenNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationConst.Home.route
    ) {
        composable(NavigationConst.Home.route) {
            InitHomeScreen(navController)
        }

        composable(
            "${NavigationConst.Day.route}/{year}/{month}/{day}",
            arguments = listOf(
                navArgument("year") {
                    type = NavType.StringType
                },
                navArgument("month") {
                    type = NavType.StringType
                },
                navArgument("day") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val today = LocalDate.now()

            val year = backStackEntry.arguments?.getString("year")?.toInt() ?: today.year
            val month = backStackEntry.arguments?.getString("month")?.toInt() ?: today.monthValue
            val day = backStackEntry.arguments?.getString("day")?.toInt() ?: today.dayOfMonth

            InitDayScreen(navController, LocalDate.of(year, month, day))
        }

        composable(
            "${NavigationConst.DayInfo.route}/{date}/{startTime}/{endTime}",
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                },
                navArgument("startTime") {
                    type = NavType.StringType
                },
                navArgument("endTime") {
                    type = NavType.StringType
                }
            )

        ) { backStackEntry ->
            val today = LocalDate.now()
            
            val defaultDate = "${today.monthValue}월 ${today.dayOfMonth}일 (${today.dayOfWeek})"
            
            val date = backStackEntry.arguments?.getString("date")?: defaultDate
            val startTime = backStackEntry.arguments?.getString("startTime") ?: "00:00"
            val endTime = backStackEntry.arguments?.getString("endTime")?: "01:00"

            InitDayInfoScreen(navController = navController,dayInfoPresent = DayInfoPresent(date, startTime, endTime))
        }
    }
}


@Composable
private fun InitHomeScreen(navController: NavHostController) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        homeState = homeViewModel.homeStateFlow.collectAsStateWithLifecycle(),
        onDetailClickListener = { present ->
            navController.navigate("${NavigationConst.Day.route}/${present.year}/${present.month}/${present.dayOfMonth}")
        },
        bottomMonthClickListener = {
            homeViewModel.monthUpdate(it)
        }
    )
}

@Composable
private fun InitDayScreen(navController: NavHostController, defaultDate: LocalDate) {
    val dayViewModel: DayViewModel = hiltViewModel()

    DayScreen(
        dayState = dayViewModel.dayStateFlow.collectAsStateWithLifecycle(),
        onDetailClickListener = { present ->
            navController.navigate("${NavigationConst.DayInfo.route}/${present.date}/${present.startTime}/${present.endTime}")
        },
        onBackClickListener = {
            navController.popBackStack()
        },
        bottomMonthClickListener = {
            dayViewModel.monthUpdate(it)
        },
        onInitListener = {
            dayViewModel.initDate(defaultDate)
        }
    )
}

@Composable
private fun InitDayInfoScreen(navController: NavHostController, dayInfoPresent: DayInfoPresent) {
    DayInfoScreen(
        dayInfoPresent = dayInfoPresent,
        onBackClickListener = {
            navController.popBackStack()
        }
    )
}