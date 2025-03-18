package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CameraToggleButton(isUsingFrontCameraUI: Boolean, onToggle: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp) // Padding around the button
                .background(color = MaterialTheme.colorScheme.surface) // Background color
        ) {
            IconButton(
                onClick = {
                    onToggle() // Toggle camera state
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                if (isUsingFrontCameraUI) {
                    Icon(
                        imageVector = Icons.Default.CameraRear,
                        contentDescription = "Use Back Camera"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraFront,
                        contentDescription = "Use Front Camera"
                    )
                }
            }
        }
    }
}