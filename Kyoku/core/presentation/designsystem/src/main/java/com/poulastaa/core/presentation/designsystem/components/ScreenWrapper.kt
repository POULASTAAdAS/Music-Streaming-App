package com.poulastaa.core.presentation.designsystem.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.poulastaa.core.presentation.designsystem.dimens

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ScreenWrapper(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    compactContext: @Composable ColumnScope.() -> Unit,
    mediumContext: @Composable ColumnScope.() -> Unit,
    expandedContext: @Composable ColumnScope.() -> Unit,
) {
    val context = LocalContext.current
    val screenWith = calculateWindowSizeClass(activity = context as Activity)


    Scaffold {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    )
                )
                .padding(it)
                .padding(horizontal = MaterialTheme.dimens.medium1)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            when (screenWith.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {

                }

                WindowWidthSizeClass.Medium -> {

                }

                WindowWidthSizeClass.Expanded -> {

                }
            }
        }
    }
}