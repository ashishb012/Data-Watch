package com.ab.datawatch.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ab.datawatch.ui.navigation.Screen
import kotlin.math.roundToInt

@Composable
fun DataWatchScaffold(
    title: String,
    navController: NavHostController,
    showBackButton: Boolean = false,
    showOverflowMenu: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // One UI standard: 40% of screen height for the top bar
    val headerMaxHeightDp = configuration.screenHeightDp.dp * 0.4f
    val headerMinHeightDp = 64.dp
    
    val headerMaxHeightPx = with(density) { headerMaxHeightDp.toPx() }
    val headerMinHeightPx = with(density) { headerMinHeightDp.toPx() }
    val maxScrollPx = headerMaxHeightPx - headerMinHeightPx

    var scrollOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                
                // If scrolling up (delta < 0), collapse header
                if (delta < 0 && scrollOffsetPx < maxScrollPx) {
                    val newOffset = (scrollOffsetPx - delta).coerceIn(0f, maxScrollPx)
                    val consumed = scrollOffsetPx - newOffset
                    scrollOffsetPx = newOffset
                    return Offset(0f, consumed)
                }
                
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                // If scrolling down (delta > 0) and content is already at top, expand header
                if (delta > 0 && scrollOffsetPx > 0f) {
                    val newOffset = (scrollOffsetPx - delta).coerceIn(0f, maxScrollPx)
                    val headerConsumed = scrollOffsetPx - newOffset
                    scrollOffsetPx = newOffset
                    return Offset(0f, headerConsumed)
                }
                return Offset.Zero
            }
        }
    }

    // Smooth animation for snap back if needed (optional implementation for overscroll snap)
    val animatedScrollOffsetPx by animateFloatAsState(
        targetValue = scrollOffsetPx,
        animationSpec = tween(durationMillis = 100),
        label = "scrollOffset"
    )

    val currentHeaderHeightPx = headerMaxHeightPx - animatedScrollOffsetPx
    val currentHeaderHeightDp = with(density) { currentHeaderHeightPx.toDp() }
    
    // Calculate progress: 0f is fully expanded, 1f is fully collapsed
    val progress = animatedScrollOffsetPx / maxScrollPx

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        // CONTENT
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content(PaddingValues(top = currentHeaderHeightDp))
        }

        // HEADER
        var showMenu by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeaderHeightDp)
                .background(
                    if (progress > 0.9f) MaterialTheme.colorScheme.surfaceContainer 
                    else MaterialTheme.colorScheme.background
                )
        ) {
            // Navigation Icon (Back Button)
            if (showBackButton) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 8.dp, start = 8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Actions (Overflow Menu)
            if (showOverflowMenu) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                ) {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                navController.navigate(Screen.Settings.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu = false
                                navController.navigate(Screen.About.route)
                            }
                        )
                    }
                }
            }

            // Title
            val expandedScale = 1f
            val collapsedScale = 0.65f
            val scale = expandedScale - ((expandedScale - collapsedScale) * progress)
            
            // X offset logic: Centered when expanded, moved to start when collapsed
            // Standard start padding is 16dp. If back button exists, offset more (e.g. 56dp)
            val startPaddingPx = with(density) { if (showBackButton) 56.dp.toPx() else 16.dp.toPx() }
            // Center is screenWidth / 2 - textWidth / 2. We use alignment instead of raw X offset.
            
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.BottomCenter) // Anchor to bottom when expanded
                        // As it collapses, we move it up and to the left
                        .offset {
                            // Calculate movement from bottom center to center left of the collapsed bar
                            val yOffset = -(currentHeaderHeightPx / 2 - with(density) { 32.dp.toPx() }) * progress
                            // Approximation: shift left by 30% of screen width based on progress
                            val xOffset = -(configuration.screenWidthDp * density.density * 0.25f) * progress + (startPaddingPx * progress)
                            IntOffset(xOffset.roundToInt(), yOffset.roundToInt())
                        }
                        .scale(scale)
                        .padding(bottom = if (progress < 0.1f) 32.dp else 0.dp) // Extra padding when fully expanded
                )
            }
        }
    }
}
