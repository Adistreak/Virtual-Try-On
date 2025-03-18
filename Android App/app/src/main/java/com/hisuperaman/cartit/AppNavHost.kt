package com.hisuperaman.cartit

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hisuperaman.cartit.data.viewmodel.AuthViewModel
import com.hisuperaman.cartit.data.viewmodel.MainViewModel
import com.hisuperaman.cartit.ui.CameraScreen
import com.hisuperaman.cartit.ui.CartScreen
import com.hisuperaman.cartit.ui.ProductScreen
import com.hisuperaman.cartit.ui.components.BottomCartBar
import com.hisuperaman.cartit.ui.components.BottomNavBar
import com.hisuperaman.cartit.ui.components.TopBar


enum class NavRoutes(@StringRes val title: Int) {
    Home(title = R.string.home),
    User(title = R.string.user),
    SignIn(title = R.string.signin),
    SignUp(title = R.string.signup),
    ProductInfo(title = R.string.productinfo),
    Cart(title = R.string.cart),
    Camera(title = R.string.camera),
    SingleProductCamera(title = R.string.camera)
}

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val previousRoute = navController.previousBackStackEntry?.destination?.route

    val currentScreenTitle = stringResource(id = NavRoutes.valueOf(
        currentRoute?:NavRoutes.Home.name
    ).title)

    val screensWithNoBars = setOf(NavRoutes.SignIn.name, NavRoutes.SignUp.name, NavRoutes.SingleProductCamera.name)
    val screensWithNoBottomBar = setOf(NavRoutes.ProductInfo.name, NavRoutes.Cart.name)

    val userEmail by authViewModel.userEmail.collectAsState()

    val products by mainViewModel.products.collectAsState(initial = listOf())

    val cartItems by mainViewModel.cartItems.collectAsState(initial = listOf())

    LaunchedEffect(userEmail) {
        if (userEmail != null) {
            navController.navigate(NavRoutes.Home.name) {
                popUpTo(NavRoutes.SignIn.name) { inclusive = true }
            }
        } else {
            navController.navigate(NavRoutes.SignIn.name) {
                popUpTo(0)
            }
        }
    }


    var currentProductIdx by remember { mutableIntStateOf(0) }

    val context = LocalContext.current

    val cart = mainViewModel.cart.collectAsState()

    val email by authViewModel.userEmail.collectAsState()

    LaunchedEffect(email) {
        authViewModel.getUserInfo(email ?: "")
    }

    var singleProductCameraImage by remember { mutableStateOf(R.drawable.red_shirt) }

    Scaffold(
        topBar = {
            if(currentRoute !in screensWithNoBars) {
                if(currentRoute == NavRoutes.Camera.name) {

                }
                else if(currentRoute in screensWithNoBottomBar) {
                    TopBar(
                        label = currentScreenTitle,
                        showBackbutton = true,
                        showActions = false,
                        onCartClick = {
                            navController.navigate(NavRoutes.Cart.name) {
                                launchSingleTop = true
                            } },
                        onBackClick = {navController.navigateUp()}
                    )
                }
                else TopBar(
                    onCartClick = {
                        navController.navigate(NavRoutes.Cart.name) {
                            launchSingleTop = true
                        }},
                )
            }
        },
        bottomBar = {
            if(currentRoute !in screensWithNoBars) {
                if(currentRoute !in screensWithNoBottomBar) BottomNavBar(
                    onHomeClick = {
                        if (currentRoute != NavRoutes.Home.name) {
                            navController.navigate(NavRoutes.Home.name) {
                                launchSingleTop = true
                                popUpTo(NavRoutes.Home.name) { inclusive = true }
                            }
                        }
                    },

                    onUserClick = {
                        if (currentRoute != NavRoutes.User.name) {
                            navController.navigate(NavRoutes.User.name) {
                                launchSingleTop = true
                                popUpTo(NavRoutes.Home.name) { inclusive = false }
                            }
                        }
                    },

                    onCameraClick = {
                        if (currentRoute != NavRoutes.Camera.name) {
                            navController.navigate(NavRoutes.Camera.name) {
                                launchSingleTop = true
                                popUpTo(NavRoutes.Home.name) { inclusive = false }
                            }
                        }
                    },

                    currentRoute = currentRoute
                )
                else {
                    if (currentRoute == NavRoutes.Cart.name) {
                        BottomCartBar(
                            totalItems = cart.value?.totalItems?:0,
                            grandTotal = cart.value?.totalPrice?:0,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SignIn.name,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                if (
                // Home <-> User transition
                    (initialState.destination.route == NavRoutes.Home.name && targetState.destination.route == NavRoutes.User.name) ||
                    (initialState.destination.route == NavRoutes.User.name && targetState.destination.route == NavRoutes.Home.name) ||

                    // Home <-> Camera transition
                    (initialState.destination.route == NavRoutes.Home.name && targetState.destination.route == NavRoutes.Camera.name) ||
                    (initialState.destination.route == NavRoutes.Camera.name && targetState.destination.route == NavRoutes.Home.name) ||

                    // Camera <-> User transition
                    (initialState.destination.route == NavRoutes.Camera.name && targetState.destination.route == NavRoutes.User.name) ||
                    (initialState.destination.route == NavRoutes.User.name && targetState.destination.route == NavRoutes.Camera.name)
                ) {
                    fadeIn(animationSpec = tween(700))
                }

                else {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                }
            },
            exitTransition = {
                if(
                // Home <-> User transition
                    (initialState.destination.route == NavRoutes.Home.name && targetState.destination.route == NavRoutes.User.name) ||
                    (initialState.destination.route == NavRoutes.User.name && targetState.destination.route == NavRoutes.Home.name) ||

                    // Home <-> Camera transition
                    (initialState.destination.route == NavRoutes.Home.name && targetState.destination.route == NavRoutes.Camera.name) ||
                    (initialState.destination.route == NavRoutes.Camera.name && targetState.destination.route == NavRoutes.Home.name) ||

                    // Camera <-> User transition
                    (initialState.destination.route == NavRoutes.Camera.name && targetState.destination.route == NavRoutes.User.name) ||
                    (initialState.destination.route == NavRoutes.User.name && targetState.destination.route == NavRoutes.Camera.name)
                ) {
                    fadeOut(animationSpec = tween(400))
                }
                else {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(400)
                    )
                }
            }
        ) {
            composable(route = NavRoutes.Home.name) {
                HomeScreen(
                    products = products,
                    onProductClick = { idx ->
                        currentProductIdx = idx
                        navController.navigate(NavRoutes.ProductInfo.name) {
                            launchSingleTop = true
                        }
                    },
                    onAddToCartClick = { productId ->
                        mainViewModel.addToCart(productId, null) {
                            Toast.makeText(context, "Item added to cart!", Toast.LENGTH_SHORT).show()
                        }

                    }
                )
            }
            composable(route = NavRoutes.User.name) {
                UserScreen(
                    authViewModel = authViewModel,
                    onLogoutClick = {
                        navController.navigate(NavRoutes.SignIn.name) {
                            popUpTo(NavRoutes.User.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = NavRoutes.SignIn.name) {
                SignInScreen(
                    onSignInClick = {
                        navController.navigate(NavRoutes.Home.name) {
                            launchSingleTop = true
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate(NavRoutes.SignUp.name) {
                            launchSingleTop = true
                        }},
                    authViewModel = authViewModel
                )
            }
            composable(route = NavRoutes.SignUp.name) {
                SignUpScreen(
                    onSignInClick = {
                        navController.navigate(NavRoutes.SignIn.name) {
                            launchSingleTop = true
                        }},
                    onSignUpClick = {
                        navController.navigate(NavRoutes.SignIn.name) {
                            launchSingleTop = true
                        }},
                    authViewModel = authViewModel
                )
            }
            composable(route = NavRoutes.ProductInfo.name) {
                ProductScreen(
                    product = products[currentProductIdx],
                    onAddToCartClick = { productId, quantity ->
                        mainViewModel.addToCart(productId = productId, quantity = quantity) {
                            Toast.makeText(context, "Item added to cart!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onTryOnClick = { clickedImage ->
                        singleProductCameraImage = clickedImage
                        navController.navigate(NavRoutes.SingleProductCamera.name) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(route = NavRoutes.Cart.name) {
                CartScreen(
                    cartItems = cartItems,
                    onQuantityIncrement = { id, productId ->
                        mainViewModel.incrementCartItemQuantity(id, productId)
                    },
                    onQuantityDecrement = { id, productId ->
                        mainViewModel.decrementCartItemQuantity(id, productId)
                    },
                    onCartItemDelete = { id, productId ->
                        mainViewModel.deleteFromCart(id, productId) {
                            Toast.makeText(context, "Cart Item deleted!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCartItemClick = { idx ->
                        currentProductIdx = idx
                        navController.navigate(NavRoutes.ProductInfo.name) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(route = NavRoutes.Camera.name) {
                CameraScreen(
                    shirtImages = cartItems.map { it.product.image_res_id }
                )
            }

            composable(route = NavRoutes.SingleProductCamera.name) {
                Box(

                ) {
                    CameraScreen(
                        shirtImages = listOf(singleProductCameraImage),
                        showBackButton = true,
                        onBackClick = {navController.navigateUp()}
                    )
                }
            }

        }
    }
}