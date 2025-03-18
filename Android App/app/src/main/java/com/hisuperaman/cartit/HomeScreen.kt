package com.hisuperaman.cartit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisuperaman.cartit.data.model.Product
import com.hisuperaman.cartit.ui.components.AppCheckbox
import com.hisuperaman.cartit.ui.components.AppHorizontalDivider
import com.hisuperaman.cartit.ui.components.BottomNavBar
import com.hisuperaman.cartit.ui.components.ProductCard
import com.hisuperaman.cartit.ui.components.TopBar
import com.hisuperaman.cartit.ui.theme.CartItTheme


@Composable
fun HomeScreen(
    products: List<Product>,
    onProductClick: (Int) -> Unit,
    onAddToCartClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding()
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            ProductSection(
                products = products,
                onProductClick = onProductClick,
                onAddToCartClick = onAddToCartClick
            )
        }

    }
}

@Composable
fun Header() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "",
            label = { Text(text = "Search for a product")},
            onValueChange = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Sort by", fontWeight = FontWeight.SemiBold)

            Row {
                AppCheckbox(
                    label = "Price",
                    onCheckedChange = {},
                )
                AppCheckbox(
                    label = "Name",
                    onCheckedChange = {},
                )
            }
        }
    }
}

@Composable
fun ProductSection(products: List<Product>, onAddToCartClick: (Int) -> Unit, onProductClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(
            span = { GridItemSpan(2) }
        ) {
            Text(
                text = "Recommended Products",
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item(
            span = { GridItemSpan(2) }
        ) {
            AppHorizontalDivider()
        }

        itemsIndexed(products) { index, product ->
            ProductCard(
                idx = index,
                id = product.id,
                name = product.name,
                price = product.price,
                imageResId = product.image_res_id,
                onProductClick = onProductClick,
                onAddToCartClick = onAddToCartClick
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    CartItTheme {
        HomeScreen(
            onProductClick = {},
            products = listOf(),
            onAddToCartClick = {}
        )
    }
}