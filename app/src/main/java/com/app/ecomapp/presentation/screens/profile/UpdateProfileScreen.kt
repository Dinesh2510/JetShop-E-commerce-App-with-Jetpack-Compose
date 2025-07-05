package com.app.ecomapp.presentation.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.UserResponse
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_24dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.AppTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.BASE_URL
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val user by userDataStore.getUserData.collectAsState(initial = null)
    val profileUpdateState by viewModel.profileUpdateResponse.collectAsState()
    val dataStoreHelper = remember { UserDataStore(context) }
    var apiData by remember { mutableStateOf<UserResponse?>(null) } // ✅ Store success data
    val scrollState = rememberScrollState()

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    var deniedPermissionName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    // State variables for input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var user_profile by remember { mutableStateOf("") }
    val userDataState by viewModel.userDataResponse.collectAsState()

    // Load user data safely
    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName ?: ""
            lastName = it.lastName ?: ""
            fullName = it.name ?: ""
            phoneNumber = it.phone ?: ""
            email = it.email ?: ""
            user_profile = it.profileImage ?: ""
            viewModel.getUserData(it.userId)
        }
    }

    /*......*/


    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = saveBitmapToCache(context, it)
                Log.d("ImagePicker", "Camera image captured: $uri")
                selectedImageUri = uri
                //onImagePicked(uri)
            }
        }

    val launcherGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                // ✅ Handle bitmap from gallery
                Log.d("ImagePicker", "Gallery image selected: $uri")
                selectedImageUri = it
                //  onImagePicked(it)
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Automatically handled from button logic
        } else {
            permissionDeniedPermanently = !ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                deniedPermissionName
            )
            showPermissionDialog = true
        }
    }

    fun requestPermission(permission: String, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            deniedPermissionName = permission
            permissionLauncher.launch(permission)
        }
    }

    fun handleGalleryRequest() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestPermission(permission) {
            launcherGallery.launch("image/*")
        }
    }

    fun handleCameraRequest() {
        requestPermission(Manifest.permission.CAMERA) {
            launcherCamera.launch()
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = {
                Text(
                    if (permissionDeniedPermanently)
                        "Permission was permanently denied. Please enable it from Settings."
                    else
                        "Permission is required to continue."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    if (permissionDeniedPermanently) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }) {
                    Text("Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    /*......*/
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Choose Option") },
            text = {
                Column {
                    Text("Camera", modifier = Modifier
                        .clickable {
                            handleCameraRequest()
                            showImagePickerDialog = false
                        }
                        .padding(8.dp))
                    Text("Gallery", modifier = Modifier
                        .clickable {
                            handleGalleryRequest()
                            showImagePickerDialog = false
                        }
                        .padding(8.dp))
                }
            },
            confirmButton = {}
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        ToolbarWithBackButtonAndTitle("Update Profile",
            onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer_8dp()


            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                when {
                    selectedImageUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    !user?.profileImage.isNullOrEmpty() -> {
                        Image(
                            painter = rememberAsyncImagePainter(BASE_URL +user?.profileImage),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.user),
                            contentDescription = "User Image",
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.Gray, RoundedCornerShape(50))
                        )
                    }
                }
            }

            Spacer_8dp()

            Button(onClick = { showImagePickerDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Select profile", style = AppMainTypography.subHeader)
            }


            // First Name Field
            CustomOutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )

            // Last Name Field
            CustomOutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )

            /* // Full Name Field (combined)
             CustomOutlinedTextField(
                 value = fullName,
                 onValueChange = { fullName = it },
                 label = "Full Name",
                 leadingIcon = Icons.Default.Person,
                 modifier = Modifier.fillMaxWidth()
             )
 */
            // Email (Read-Only)
            CustomOutlinedTextField(
                value = email,
                onValueChange = {},
                label = "Email",
                leadingIcon = Icons.Default.Email,
                modifier = Modifier.fillMaxWidth(),
                isEnabled = false // Make email read-only
            )

            // Phone Number Field
            CustomOutlinedTextField(
                value = phoneNumber,
                onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it },
                label = "Phone Number",
                leadingIcon = Icons.Default.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer_16dp()
            // Submit Button
            LoadingButton(
                text = "Submit",
                isLoading = profileUpdateState is Resource.Loading,
                onClick = {
                    when {
                        firstName.isBlank() -> {
                            Toast.makeText(context, "First Name is required", Toast.LENGTH_SHORT)
                                .show()
                        }

                        lastName.isBlank() -> {
                            Toast.makeText(context, "Last Name is required", Toast.LENGTH_SHORT)
                                .show()
                        }

                        phoneNumber.isBlank() -> {
                            Toast.makeText(context, "Phone Number is required", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            user?.let {
                                coroutineScope.launch {
                                    // If user selected a new image, handle image upload (optional)
                                    val imagePart = selectedImageUri?.let { uri ->
                                        // Optional: Convert to Base64 if you still need it
                                        val base64 = uriToBase64(context, uri)
                                        Log.d("TAG", "Base64: $base64") // Optional

                                        uriToMultipart(context, uri)
                                    }

                                    viewModel.updateUser(
                                        userId = it.userId.toString(),
                                        firstName = firstName,
                                        lastName = lastName,
                                        fullName = fullName,
                                        phoneNumber = phoneNumber,
                                        email = email,
                                        profile_image = imagePart // This will be null if user didn't select a new image
                                    )

                                    // Clear selected image if any
                                    selectedImageUri = null
                                }
                            }
                        }

                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        HandleApiState(
            apiState = viewModel.userDataResponse,
            showLoader = false,
            navController = navController,
            onSuccess = { data ->
                apiData = data
            }
        ) {
            apiData?.let { data ->
                LaunchedEffect(data) {
                    dataStoreHelper.saveUser(
                        userId = data.userData.userId.toString(),
                        name = "${data.userData.firstName} ${data.userData.lastName}",
                        first_name = data.userData.firstName.orEmpty(),
                        last_name = data.userData.lastName.orEmpty(),
                        email = data.userData.email.orEmpty(),
                        phone = data.userData.phone.orEmpty(),
                        user_profile = data.userData.profileImage.orEmpty(),
                        isPrimeActive = data.userData.isPrimeActive.toString(),
                        referralCode = data.userData.referralCode.orEmpty(),
                        referredBy = data.userData.referredBy.orEmpty(),
                        walletBalance = data.userData.walletBalance
                    )
                }
                // navController.popBackStack()
                val primeMembership = data?.userData?.primeMembership
                if (primeMembership != null) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "👑 Your Plan \uD83D\uDC51",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontFamily = Montserrat
                        )

                        Spacer_16dp()
                        showPlanDetails(data)
                    }
                }

            }
        }
    }

    // Handling profile update state
    when (profileUpdateState) {
        is Resource.Loading -> {
            // ✅ Show progress indicator
            //  CenteredCircularProgressIndicator()
        }

        is Resource.Success -> {
            val response = (profileUpdateState as Resource.Success).data

            // Fetch the updated user data
            user?.let {
                viewModel.getUserData(it.userId)
            }
            Toast.makeText(
                LocalContext.current,
                response?.message ?: "Profile updated!",
                Toast.LENGTH_SHORT
            ).show()
        }

        is Resource.Error -> {
            val errorMessage = (profileUpdateState as Resource.Error).message
            Toast.makeText(
                LocalContext.current,
                errorMessage ?: "Failed to update profile",
                Toast.LENGTH_SHORT
            ).show()
        }

        else -> {}
    }

    /* LaunchedEffect(userDataState) {
         if (userDataState is Resource.Success) {
             val updatedUser = (userDataState as Resource.Success).data

             updatedUser?.let { newUser ->
                 dataStoreHelper.saveUser(
                     userId = newUser.userData.userId.toString(),
                     name = "${newUser.userData.firstName} ${newUser.userData.lastName}",
                     first_name = newUser.userData.firstName.orEmpty(),
                     last_name = newUser.userData.lastName.orEmpty(),
                     email = newUser.userData.email.orEmpty(),
                     phone = newUser.userData.phone.orEmpty(),
                     user_profile = newUser.userData.profile_image.orEmpty(),
                     isPrimeActive = newUser.userData.isPrimeActive.toString()
                 )
             }

             Toast.makeText(
                 context,
                 "Profile updated!",
                 Toast.LENGTH_SHORT
             ).show()
             navController.popBackStack()
         }
     }*/


}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showPlanDetails(
    userData: UserResponse?,
    modifier: Modifier = Modifier,
    isSelected: Boolean = true,
) {
    var primeMembership = userData!!.userData.primeMembership
    Box(
        modifier = modifier
    ) {
        // Card with conditional background color based on selection
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (isSelected) Color(0xFF0086F9) else Color.Gray),
            colors = CardDefaults.cardColors(if (isSelected) Color(0xFFE1F5FE) else Color.White), // Light blue if selected
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable { }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer_24dp()
                Text(
                    text = "Amount: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontFamily = Montserrat
                )
                Spacer_8dp()
                Text(
                    text = Ruppes + primeMembership!!.planPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = Montserrat
                )
                Spacer_8dp()
                IncludeApp().CustomDivider()
                Spacer_16dp()
                Text(
                    text = "Expired on: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontFamily = Montserrat
                )
                Spacer_8dp()
                Text(
                    text = formatDateTime(primeMembership!!.expiryDate),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
                Spacer_8dp()
                IncludeApp().CustomDivider()
                Spacer_16dp()
            }
            Spacer_8dp()

        }

        // Add a checkmark icon below the card, outside the border, if selected


        // Title on top of the card
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = primeMembership!!.planName.uppercase(Locale.getDefault()),
                color = Color(0xFF0086F9),
                fontSize = 16.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun uriToMultipart(
    context: Context,
    uri: Uri,
    paramName: String = "profile_image"
): MultipartBody.Part? {
    return try {
        val file = FileUtils.from(context, uri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

object FileUtils {
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}

