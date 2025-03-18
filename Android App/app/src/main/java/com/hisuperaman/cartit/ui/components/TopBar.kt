package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hisuperaman.cartit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.app_name),
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    showBackbutton: Boolean = false,
    showActions: Boolean = true
) {
    TopAppBar(
        title = {
            Text(text = label, color = MaterialTheme.colorScheme.secondary)
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if(showBackbutton) {
                StyledIconButton(onClick = { onBackClick() }, imageVector = Icons.Filled.ArrowBackIosNew)
            }
        },
        actions = {
            if(showActions) {
                StyledIconButton(
                    imageVector = Icons.Default.ShoppingCart,
                    onClick = { onCartClick() }
                )
            }
        }
    )
}