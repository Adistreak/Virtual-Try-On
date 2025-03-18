package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun StyledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    tint: Color = LocalContentColor.current
) {
    require(imageVector != null || painter != null) { "Either imageVector or painter must be provided" }

    Box(
        modifier = modifier
            .size(50.dp, 36.dp)
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageVector != null -> Icon(imageVector = imageVector, contentDescription = null, tint = tint)
            painter != null -> Icon(painter = painter, contentDescription = null, tint = tint)
        }
    }
}
