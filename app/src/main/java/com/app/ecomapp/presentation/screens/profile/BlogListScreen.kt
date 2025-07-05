package com.app.ecomapp.presentation.screens.profile

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.data.Resource


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.blogs.BlogData
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.HandleApiState
import com.compose.jetshop.R
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogListScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    var blogsResponse by remember { mutableStateOf<BlogResponse?>(null) } // Store success data

    LaunchedEffect(Unit) {
        viewModel.fetchBlogs()
    }
    // HandleApiState for Add address submission
    HandleApiState(apiState = viewModel.blogList, // Pass the API state
        showLoader = true, // Enable/disable loader
        navController = navController, onSuccess = { data ->
            blogsResponse = data
        }) {

        Scaffold(
            topBar = {
                ToolbarWithBackButtonAndTitle("Blogs",
                    onBackClick = { navController.popBackStack() })
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    blogsResponse?.data?.let {
                        items(it.size) { index ->
                            PostCardTop(it[index]){
                                viewModel.setSelectedBlog(it[index])
                                val blogJson = Uri.encode(Gson().toJson(it[index]))
                                navController.navigate(Screen.BlogsDetailsScreen.route+"/$blogJson")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PostCardTop(blog: BlogData, onClick: () -> Unit) {    // TUTORIAL CONTENT STARTS HERE
    val typography = MaterialTheme.typography
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }, // Open Details Page
    ) {
        val imageModifier = Modifier
            .heightIn(min = 180.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.large)
        Image(
              painter = rememberAsyncImagePainter(blog.imageUrl),
           // painter = rememberAsyncImagePainter("https://raw.githubusercontent.com/android/nowinandroid/main/docs/images/nia-splash.jpg"),
            contentDescription = null, // decorative
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = blog.title,
            style = typography.titleLarge,
            fontFamily = Montserrat,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = blog.description,
            style = typography.labelLarge,
            fontFamily = Montserrat,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = formatDateTime(blog.publishedDate),
            fontFamily = Montserrat,
            style = typography.bodySmall
        )
    }
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BlogDetailsScreen(blog: BlogData, navController: NavController) {


    Scaffold(
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = blog!!.title,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(blog!!.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = blog!!.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = blog!!.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Published: ${blog!!.publishedDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(input: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm a", Locale.ENGLISH)

    val dateTime = LocalDateTime.parse(input, inputFormatter)
    return dateTime.format(outputFormatter)
}

