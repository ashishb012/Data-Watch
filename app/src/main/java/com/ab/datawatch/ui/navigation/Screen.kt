package com.ab.datawatch.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object About : Screen("about")
    data object AppDetail : Screen("app_detail/{uid}") {
        fun createRoute(uid: Int) = "app_detail/$uid"
    }
}
