package com.ab.datawatch.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ab.datawatch.ui.components.AppUsageItem
import com.ab.datawatch.ui.components.DataWatchScaffold
import com.ab.datawatch.ui.components.UsageCard
import com.ab.datawatch.ui.components.UsageTrendsChart
import com.ab.datawatch.ui.navigation.Screen
import com.ab.datawatch.util.FormatUtils

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val todaySummary by viewModel.todaySummary.collectAsState()
    val appUsageList by viewModel.appUsageList.collectAsState()
    val speedUnit by viewModel.speedUnit.collectAsState()
    val currentSpeed by viewModel.currentSpeed.collectAsState()

    DataWatchScaffold(
        title = "DataWatch",
        navController = navController,
        showOverflowMenu = true
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            item {
                UsageCard(
                    title = "TODAY's USAGE",
                    mobileData = FormatUtils.formatDataSize(todaySummary.totalMobile),
                    wifiData = FormatUtils.formatDataSize(todaySummary.totalWifi)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            item {
                UsageTrendsChart()
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                Text(
                    text = "APP DATA USAGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }
            
            items(appUsageList.take(20)) { app ->
                AppUsageItem(
                    appName = app.appName,
                    packageName = app.packageName,
                    usageText = FormatUtils.formatDataSize(app.totalBytes),
                    isSystemApp = app.isSystemApp,
                    onClick = { navController.navigate(Screen.AppDetail.createRoute(app.uid)) }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
