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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import com.hisuperaman.cartit.data.database.CartItemWithProduct
import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.CartItem
import com.hisuperaman.cartit.data.viewmodel.MainViewModel
import com.hisuperaman.cartit.ui.components.ActionButton
import com.hisuperaman.cartit.ui.components.AppCheckbox
import com.hisuperaman.cartit.ui.components.BottomCartBar
import com.hisuperaman.cartit.ui.components.BottomNavBar
import com.hisuperaman.cartit.ui.components.CartItem
import com.hisuperaman.cartit.ui.components.SelectField
import com.hisuperaman.cartit.ui.components.TopBar
import com.hisuperaman.cartit.ui.theme.CartItTheme


@Composable
fun CartScreen(
    onQuantityIncrement: (Int, Int) -> Unit,
    onQuantityDecrement: (Int, Int) -> Unit,
    onCartItemDelete: (Int, Int) -> Unit,
    cartItems: List<CartItemWithProduct>,
    onCartItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
        Surface(
            modifier = modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .fillMaxSize()
            ) {
                CartItemsSection(
                    cartItems = cartItems,
                    onQuantityIncrement = onQuantityIncrement,
                    onQuantityDecrement = onQuantityDecrement,
                    onCartItemDelete = onCartItemDelete,
                    onCartItemClick = onCartItemClick
                )
            }

        }

}

@Composable
fun CartItemsSection(
    onQuantityIncrement: (Int, Int) -> Unit,
    onQuantityDecrement: (Int, Int) -> Unit,
    onCartItemDelete: (Int, Int) -> Unit,
    cartItems: List<CartItemWithProduct>,
    onCartItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cartItems.isEmpty()) {
        Text(
            text = "Your cart is empty. Start adding items!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
    }
    else {
        LazyColumn {
            itemsIndexed(cartItems) { index, cartItem ->
                CartItem(
                    id = cartItem.cartItemId,
                    name = cartItem.product.name,
                    price = cartItem.product.price,
                    quantity = cartItem.quantity,
                    imgResId = cartItem.product.image_res_id,
                    productId = cartItem.product.id,
                    onQuantityIncrement = onQuantityIncrement,
                    onQuantityDecrement = onQuantityDecrement,
                    onCartItemDelete = onCartItemDelete,
                    idx = index,
                    onCartItemClick = onCartItemClick
                )
            }
    }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CartScreenPreview() {
    CartItTheme {
        CartScreen(
            cartItems = listOf(),
            onQuantityIncrement = {_, _ ->},
            onQuantityDecrement = {_, _, ->},
            onCartItemDelete = {_, _ ->},
            onCartItemClick = {}
        )
    }
}