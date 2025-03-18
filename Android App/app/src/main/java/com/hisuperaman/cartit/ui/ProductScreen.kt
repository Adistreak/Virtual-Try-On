package com.hisuperaman.cartit.ui

import androidx.compose.foundation.Image
import com.hisuperaman.cartit.R

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.hisuperaman.cartit.ui.components.ActionButton
import com.hisuperaman.cartit.ui.components.AppCheckbox
import com.hisuperaman.cartit.ui.components.BottomNavBar
import com.hisuperaman.cartit.ui.components.CounterField
import com.hisuperaman.cartit.ui.components.ProductCard
import com.hisuperaman.cartit.ui.components.SelectField
import com.hisuperaman.cartit.ui.components.StyledIconButton
import com.hisuperaman.cartit.ui.components.TopBar
import com.hisuperaman.cartit.ui.theme.CartItTheme


@Composable
fun ProductScreen(
    product: Product,
    onAddToCartClick: (Int, Int) -> Unit,
    onTryOnClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
        var quantity by remember { mutableStateOf(1) }

        Surface(
            modifier = modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(id = product.image_res_id),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f),
                        contentDescription = null
                    )
                    StyledIconButton(
                        onClick = { onTryOnClick(product.image_res_id) },
                        imageVector = Icons.Default.CameraAlt,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "₹ "+product.price,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quantity ",
                        )
                        CounterField(
                            count = quantity,
                            onIncrement = { quantity++ },
                            onDecrement = { quantity-- }
                        )
                    }

                    ActionButton(
                        label = "Add to Cart",
                        onClick = {
                            onAddToCartClick(product.id, quantity)
                        }
                    )

                    Text(
                        text = product.description
                    )
                }
            }

        }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProductScreenPreview() {
    CartItTheme {
        ProductScreen(
            product = Product(0, "", "", 99, R.drawable.oppo),
            onAddToCartClick = {_, _ ->},
            onTryOnClick = {}
        )
    }
}