package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisuperaman.cartit.ui.theme.CartItTheme

@Composable
fun BottomCartBar(
    totalItems: Int,
    grandTotal: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = "Grand Total",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "₹ "+grandTotal,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("$totalItems")
                    }
                    append(" Items")
                },
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun BottomCartBarPreview() {
    CartItTheme {
        BottomCartBar(
            totalItems = 0,
            grandTotal = 0
        )
    }
}