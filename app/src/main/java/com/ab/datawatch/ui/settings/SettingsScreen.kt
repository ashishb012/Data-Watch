package com.ab.datawatch.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ab.datawatch.data.model.SpeedUnit
import com.ab.datawatch.data.model.ThemeColor
import com.ab.datawatch.ui.components.DataWatchScaffold
import com.ab.datawatch.util.PermissionHelper

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val speedUnit by viewModel.speedUnit.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    val context = LocalContext.current

    DataWatchScaffold(
        title = "Settings",
        navController = navController,
        showBackButton = true
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "PERMISSIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PermissionRow(
                            name = "Usage Access",
                            isGranted = PermissionHelper.hasUsageStatsPermission(context),
                            onFixClick = {
                                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PermissionRow(
                            name = "Notifications",
                            isGranted = PermissionHelper.hasPostNotificationsPermission(context),
                            onFixClick = {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "APPEARANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Theme Color", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThemeColorSelector(ThemeColor.BLUE, Color(0xFF1B6EB5), themeColor == ThemeColor.BLUE) {
                        viewModel.setThemeColor(ThemeColor.BLUE)
                    }
                    ThemeColorSelector(ThemeColor.ORANGE, Color(0xFFAD5A00), themeColor == ThemeColor.ORANGE) {
                        viewModel.setThemeColor(ThemeColor.ORANGE)
                    }
                    ThemeColorSelector(ThemeColor.GREEN, Color(0xFF2D6B34), themeColor == ThemeColor.GREEN) {
                        viewModel.setThemeColor(ThemeColor.GREEN)
                    }
                    ThemeColorSelector(ThemeColor.PURPLE, Color(0xFF6B4FA7), themeColor == ThemeColor.PURPLE) {
                        viewModel.setThemeColor(ThemeColor.PURPLE)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "MONITORING",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Status Bar Notification", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Show real-time speed in status bar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { viewModel.toggleNotification(it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Speed Unit", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                
                SpeedUnit.entries.forEach { unit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setSpeedUnit(unit) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = speedUnit == unit,
                            onClick = { viewModel.setSpeedUnit(unit) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = unit.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PermissionRow(name: String, isGranted: Boolean, onFixClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isGranted) Color(0xFF4CAF50) else Color(0xFFFF9800),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
        }
        
        if (!isGranted) {
            TextButton(onClick = onFixClick) {
                Text("Fix")
            }
        } else {
            Text(
                "Granted",
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun ThemeColorSelector(color: ThemeColor, displayColor: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(displayColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = Color.White
            )
        }
    }
}
