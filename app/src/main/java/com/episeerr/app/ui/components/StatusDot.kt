package com.episeerr.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.episeerr.app.ui.theme.StatusConnected
import com.episeerr.app.ui.theme.StatusDisabled
import com.episeerr.app.ui.theme.StatusUnconfigured

/** Small colored dot used to give service/connection state a glanceable color, not just text. */
@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(8.dp).background(color, CircleShape))
}

fun serviceStatusColor(connected: Boolean, enabled: Boolean): Color = when {
    !connected -> StatusUnconfigured
    !enabled -> StatusDisabled
    else -> StatusConnected
}
