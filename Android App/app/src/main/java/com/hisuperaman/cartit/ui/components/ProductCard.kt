package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hisuperaman.cartit.HomeScreen
import com.hisuperaman.cartit.R
import com.hisuperaman.cartit.ui.theme.CartItTheme

@Composable
fun ProductCard(
    idx: Int,
    id: Int,
    name: String,
    price: Long,
    imageResId: Int,
    onProductClick: (Int) -> Unit,
    onAddToCartClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
            .width(160.dp)
            .height(360.dp),

        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            onProductClick(idx)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.CenterHorizontally),
                contentDescription = null
            )

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "₹ "+price,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            ActionButton(
                label = "Add to Cart",
                onClick = {
                    onAddToCartClick(id)
                }
            )

        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProductCardPreview() {
    CartItTheme {
        ProductCard(
            onProductClick = {},
            imageResId = R.drawable.oppo,
            name = "name",
            price = 29,
            idx = 0,
            onAddToCartClick = {},
            id = 0
        )
    }
}