package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hisuperaman.cartit.R
import com.hisuperaman.cartit.ui.theme.CartItTheme

@Composable
fun CartItem(
    id: Int,
    productId: Int,
    name: String,
    price: Long,
    quantity: Int,
    imgResId: Int,
    modifier: Modifier = Modifier,
    onQuantityIncrement: (Int, Int) -> Unit,
    onQuantityDecrement: (Int, Int) -> Unit,
    onCartItemDelete: (Int, Int) -> Unit,
    idx: Int,
    onCartItemClick: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small))
            .requiredHeight(100.dp),

        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            onCartItemClick(idx)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imgResId),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Price: ₹ "+price,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Total Price: ₹"+(price*quantity),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
            ) {
                CounterField(
                    count = quantity,
                    onIncrement = { onQuantityIncrement(id, productId) },
                    onDecrement = { onQuantityDecrement(id, productId) }
                )

                IconButton(onClick = {
                    onCartItemDelete(id, productId)
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CartItemPreview() {
    CartItTheme {
        CartItem(
            id = 0,
            name = "name",
            price = 29,
            quantity = 1,
            imgResId = R.drawable.oppo,
            onQuantityDecrement = {_, _ ->},
            onQuantityIncrement = {_, _ ->},
            onCartItemDelete = {_, _ ->},
            productId = 0,
            idx = 0,
            onCartItemClick = {}
        )
    }
}