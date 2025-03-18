package com.hisuperaman.cartit

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hisuperaman.cartit.data.model.User
import com.hisuperaman.cartit.data.viewmodel.AuthViewModel
import com.hisuperaman.cartit.ui.theme.CartItTheme


@Composable
fun SignUpScreen(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            //  image
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "logo",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(94.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = name,
                    label = { Text(text = "Name")},
                    onValueChange = { name = it },
                )
                OutlinedTextField(
                    value = email,
                    label = { Text(text = "Email")},
                    onValueChange = { email = it },
                )
                OutlinedTextField(
                    value = password,
                    label = { Text(text = "Password")},
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it }
                )
                OutlinedTextField(
                    value = confirmPassword,
                    label = { Text(text = "Confirm Password")},
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { confirmPassword = it }
                )
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_medium)))

            Button(
                onClick = {
                    if(name.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "All fields are required"
                    } else if(password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                    } else {
                        authViewModel.signUp(name, email, password,
                            onSuccess = {
                                Toast.makeText(context, "Sign Up successful!", Toast.LENGTH_SHORT).show()
                                onSignUpClick()
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Text(text = "Sign Up")
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_medium)))


            Row {
                Text(
                    text = "Already have an account? ",
                    modifier = Modifier
                )
                Text(
                    text="Sign In",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onSignInClick()
                    }
                )
            }

        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SignUpScreenPreview() {
    CartItTheme {
        SignUpScreen(
            onSignInClick = {},
            onSignUpClick = {},
            authViewModel = viewModel()
        )
    }
}