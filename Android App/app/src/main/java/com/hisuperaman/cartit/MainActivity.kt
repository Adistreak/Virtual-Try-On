package com.hisuperaman.cartit

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.hisuperaman.cartit.data.DataSource
import com.hisuperaman.cartit.data.database.AppDatabase
import com.hisuperaman.cartit.data.database.AppDatabase.Companion.getInstance
import com.hisuperaman.cartit.data.database.ProductRepository
import com.hisuperaman.cartit.data.database.UserRepository
import com.hisuperaman.cartit.data.viewmodel.AuthViewModel
import com.hisuperaman.cartit.data.viewmodel.AuthViewModelFactory
import com.hisuperaman.cartit.data.viewmodel.MainAppViewModelFactory
import com.hisuperaman.cartit.data.viewmodel.MainViewModel
import com.hisuperaman.cartit.ui.theme.CartItTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import com.hisuperaman.cartit.data.database.CartItemRepository
import com.hisuperaman.cartit.data.database.CartRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestCameraPermission()

        val database = AppDatabase.getInstance(applicationContext)

        setContent {
            val navController = rememberNavController()

            val authViewModel: AuthViewModel by viewModels {
                AuthViewModelFactory(application, UserRepository(database.userDao()), CartRepository(database.cartDao()))
            }

            val mainViewModel: MainViewModel by viewModels {
                MainAppViewModelFactory(application, ProductRepository(database.productDao()), CartRepository(database.cartDao()), CartItemRepository(database.cartItemDao()), authViewModel)
            }

            CartItTheme {
                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    mainViewModel = mainViewModel
                )
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher =
        this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Camera permission is required to use Virtual Try On feature", Toast.LENGTH_SHORT).show()
            }
        }
}