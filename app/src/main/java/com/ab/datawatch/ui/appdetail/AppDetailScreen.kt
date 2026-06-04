package com.ab.datawatch.ui.appdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ab.datawatch.ui.components.DataWatchScaffold
import com.ab.datawatch.ui.components.UsageCard
import com.ab.datawatch.util.FormatUtils

@Composable
fun AppDetailScreen(
    uid: Int,
    navController: NavHostController,
    viewModel: AppDetailViewModel = hiltViewModel()
) {
    val appData by viewModel.appData.collectAsState()

    DataWatchScaffold(
        title = appData?.appName ?: "Loading...",
        navController = navController,
        showBackButton = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (appData != null) {
                UsageCard(
                    title = "TODAY's USAGE",
                    mobileData = FormatUtils.formatDataSize(appData!!.rxBytes + appData!!.txBytes), // Simplified for app data
                    wifiData = "Included in total"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Package: ${appData!!.packageName}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "UID: ${appData!!.uid}"
                )
            } else {
                Text("App data not found")
            }
        }
    }
}
