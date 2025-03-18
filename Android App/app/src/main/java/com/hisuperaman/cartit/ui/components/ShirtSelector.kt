package com.hisuperaman.cartit.ui.components

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import androidx.compose.material3.Button

@Composable
fun ShirtSelector(shirtImages: List<Int>, currentShirtIndex: Int, onShirtSelected: (Int) -> Unit) {
    val pagerState = rememberPagerState(initialPage = currentShirtIndex, pageCount = { shirtImages.size }) // Start at the first shirt

    LaunchedEffect(currentShirtIndex) {
        pagerState.animateScrollToPage(currentShirtIndex)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Horizontal Pager for swiping through shirts
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Adjust height as needed
                .padding(vertical = 8.dp),
            state = pagerState
        ) { page ->
            val shirtImage = shirtImages[page]
            Image(
                painter = painterResource(id = shirtImage),
                contentDescription = "Shirt Image",
                modifier = Modifier.fillMaxSize()
            )
        }

        LaunchedEffect(pagerState.currentPage) {
            onShirtSelected(pagerState.currentPage) // Callback with selected shirt
        }
    }
}
