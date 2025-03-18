package com.hisuperaman.cartit

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hisuperaman.cartit.data.viewmodel.AuthViewModel
import com.hisuperaman.cartit.ui.components.ActionButton
import com.hisuperaman.cartit.ui.components.AppCheckbox
import com.hisuperaman.cartit.ui.components.BottomNavBar
import com.hisuperaman.cartit.ui.components.ProductCard
import com.hisuperaman.cartit.ui.components.StyledIconButton
import com.hisuperaman.cartit.ui.components.TopBar
import com.hisuperaman.cartit.ui.theme.CartItTheme
import kotlinx.coroutines.flow.stateIn


@Composable
fun UserScreen(authViewModel: AuthViewModel, onLogoutClick: () -> Unit, modifier: Modifier = Modifier) {
        val context = LocalContext.current

        val user by authViewModel.user.collectAsState()

        var name by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }

        LaunchedEffect(user) {
//            Log.d("userScreen", user?.email?:"")
            name = user?.name ?: ""
            address = user?.address ?: ""
        }

        var errorMessage: String? by remember { mutableStateOf(null) }

        Surface(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                StyledIconButton(
                    onClick = {
                        authViewModel.logout {
                            Toast.makeText(context, "Logout successful!", Toast.LENGTH_SHORT).show()
                            onLogoutClick()
                        }
                    },
                    painter = painterResource(id = R.drawable.ic_logout),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .fillMaxSize()
            ) {
                Text(
                    text = "User Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(vertical = 24.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = user?.email?:"",
                        label = { Text(text = "Email") },
                        onValueChange = {},
                        readOnly = true
                    )
                    OutlinedTextField(
                        value = name,
                        label = { Text(text = "Name") },
                        onValueChange = {name = it},
                    )
                    OutlinedTextField(
                        value = address,
                        label = { Text(text = "Address") },
                        onValueChange = {address = it},
                        maxLines = 5
                    )
                }

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (name.isBlank() || address.isBlank()) {
                                errorMessage = "Any field must not be empty"
                            }
                            else {
                                authViewModel.updateUserInfo(name = name, address = address, email = user?.email?:"", password = null) {
                                    Toast.makeText(context, "User info update successful!", Toast.LENGTH_SHORT).show()
                                    errorMessage = null
                                }
                            }
                        },
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Save",
                        )
                    }
                }

            }

        }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun UserScreenPreview() {
    CartItTheme {
        UserScreen(
            onLogoutClick = {},
            authViewModel = viewModel()
        )
    }
}