package com.ab.datawatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ab.datawatch.data.repository.UserPreferencesRepository
import com.ab.datawatch.ui.about.AboutScreen
import com.ab.datawatch.ui.appdetail.AppDetailScreen
import com.ab.datawatch.ui.home.HomeScreen
import com.ab.datawatch.ui.navigation.Screen
import com.ab.datawatch.ui.permissions.PermissionsScreen
import com.ab.datawatch.ui.settings.SettingsScreen
import com.ab.datawatch.ui.theme.DataWatchTheme
import com.ab.datawatch.data.model.ThemeColor
import com.ab.datawatch.util.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val themeColor by userPreferencesRepository.themeColor.collectAsState(initial = ThemeColor.BLUE)
            val isDarkMode by userPreferencesRepository.isDarkMode.collectAsState(initial = true)
            
            DataWatchTheme(
                themeColor = themeColor,
                darkTheme = isDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    var hasPermissions by androidx.compose.runtime.remember { 
                        androidx.compose.runtime.mutableStateOf(PermissionHelper.hasUsageStatsPermission(context)) 
                    }

                    if (!hasPermissions) {
                        PermissionsScreen(onPermissionsGranted = { hasPermissions = true })
                    } else {
                        val navController = rememberNavController()
                        
                        NavHost(navController = navController, startDestination = Screen.Home.route) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    navController = navController,
                                    onThemeToggle = {
                                        val nextTheme = when (themeColor) {
                                            ThemeColor.BLUE -> ThemeColor.ORANGE
                                            ThemeColor.ORANGE -> ThemeColor.GREEN
                                            ThemeColor.GREEN -> ThemeColor.PURPLE
                                            ThemeColor.PURPLE -> ThemeColor.BLUE
                                        }
                                        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                            userPreferencesRepository.setThemeColor(nextTheme)
                                        }
                                    }
                                )
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(navController = navController)
                            }
                            composable(Screen.About.route) {
                                AboutScreen(navController = navController)
                            }
                            composable(
                                route = Screen.AppDetail.route,
                                arguments = listOf(navArgument("uid") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val uid = backStackEntry.arguments?.getInt("uid") ?: return@composable
                                AppDetailScreen(uid = uid, navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
