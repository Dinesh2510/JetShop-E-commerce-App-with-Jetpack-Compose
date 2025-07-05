package com.app.ecomapp.presentation.screens.search


import androidx.navigation.NavHostController
import com.app.ecomapp.presentation.screens.home.HomeViewModel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.compose.jetshop.R
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.BluePrimary
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.GrayColor
import com.app.ecomapp.ui.theme.MainWhiteColor
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.black
import com.app.ecomapp.ui.theme.white
import com.app.ecomapp.utils.NetworkImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val searchResponse = viewModel.searchResponse.collectAsState().value

    var productName by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ToolbarWithBackButtonAndTitle("Search Products",
                onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = productName,
                    leadingIcon = {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = DarkBlue
                                )
                                Canvas(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .padding(start = 10.dp)
                                ) {
                                    drawLine(
                                        color = GrayColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = 2.0F
                                    )
                                }
                            }
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        //setting the text field background when it is focused
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f).padding(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchProducts(productName, rating)
                        }
                    ),
                    label = { Text(text = "Search...") },
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = {
                        productName = it
                    }
                )

                /* IconButton(onClick = { viewModel.searchProducts(productName, rating) }) {
                     Icon(Icons.Filled.Search, contentDescription = "Search")
                 }*/
            }

            Spacer_16dp()

            when (val response = searchResponse) {
                is Resource.Loading -> {
                    CenteredCircularProgressIndicator()
                }
                is Resource.Error -> {
                    Text(text = response.message, color = MaterialTheme.colorScheme.error)
                }
                is Resource.Success -> {
                    val products = response.data?.data
                    if (products.isNullOrEmpty()) {
                        Text("No products found")
                    } else {
                        LazyColumn(Modifier.background(Color.LightGray)) {
                            items(products) { product ->
                                ItemsSearchItem(product = product, onViewProduct = { product ->
                                    navController.navigate("${Screen.ProductDetails.route}/${product.productId}")
                                })

                            }
                        }
                    }
                }
                else -> {
                    // Default case if nothing is loaded yet
                }
            }
        }
    }
}

@Composable
fun ItemsSearchItem(product: ProductData, onViewProduct: (ProductData) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(MainWhiteColor)
            .clickable {
                onViewProduct(product) // Call onViewProduct when clicked
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .height(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                NetworkImage(
                    modifier = Modifier
                        .size(70.dp),
                    contentScale = ContentScale.Inside,
                    url = product.productThumbnailUrl
                )
            }

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(0.9f)
                    .wrapContentHeight()
            ) {
                Text(
                    text = product.productName,
                    fontSize = 16.sp,
                    color = black,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating Star",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = product.productRating,
                        fontSize = 16.sp,
                        color = DarkBlue,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rs.${product.productPrice}",
                        fontSize = 16.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    )
                    Text(
                        text = "Rs.${product.productDiscountPrice}",
                        fontSize = 16.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Normal,
                        textDecoration = TextDecoration.LineThrough,
                        color = DarkBlue,
                    )
                }
            }
        }
    }
}
