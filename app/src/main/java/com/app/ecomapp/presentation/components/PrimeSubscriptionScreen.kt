package com.app.ecomapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.home.ProductData

import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.LoginRequiredScreen
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.home.HomeViewModel
import com.app.ecomapp.ui.theme.BlueDark
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.black
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore


@Composable
fun PrimeSubscriptionScreen(
    onPlanSelect: (Plan?) -> Unit, // Passing null to deselect
    onNoThanks: () -> Unit,
    selectedPlan: Plan? // Pass the selectedPlan to reflect current selection
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explore plans with Video, Music, & more\nalong with shopping benefits*",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Montserrat,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            SubscriptionCard(
                title = "Prime",
                price = "₹1499/year",
                benefitsList = listOf(
                    "All benefits of Prime Shopping Edition",
                    "4K/UHD Prime Video (Ads Free, on any 5 devices)",
                    "Prime Music, Gaming & more"
                ),
                isSelected = selectedPlan == Plan.Prime,
                onSelect = { onPlanSelect(Plan.Prime) },
                modifier = Modifier.weight(1f)
            )

            Spacer_8dp()

            SubscriptionCard(
                title = "Prime Lite",
                price = "₹799/Half-year",
                benefitsList = listOf(
                    "All benefits of Prime Shopping Edition",
                    "HD Prime Video \n(1 Device - Mobile/TV)"
                ),
                isSelected = selectedPlan == Plan.PrimeLite,
                onSelect = { onPlanSelect(Plan.PrimeLite) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer_12dp()

       /* Button(
            onClick = onNoThanks,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = "No Thanks")
        }*/
    }
}


@Composable
fun SubscriptionCard(
    title: String,
    price: String,
    benefitsList: List<String>,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
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
                .clickable { onSelect() }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer_24dp()
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = Montserrat
                )
                IncludeApp().CustomDivider()
                Spacer_8dp()
                benefitsList.forEachIndexed { index, benefit ->
                    Text(
                        text = benefit,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer_8dp()
                    if (index != benefitsList.lastIndex) {
                        IncludeApp().DashedDivider()
                    }
                }
            }
            Spacer_8dp()

        }

        // Add a checkmark icon below the card, outside the border, if selected
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle, // Checkmark icon
                contentDescription = "Selected",
                tint = Color(0xFF0086F9), // Blue checkmark
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Align the checkmark at the bottom center
                    .padding(top = 8.dp) // Padding above the checkmark to give space between card and checkmark
                    .size(32.dp) // Make the checkmark larger
            )
        }


        // Title on top of the card
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFF0086F9),
                fontSize = 14.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

sealed class Plan(val price: Int, val name :String) {
    object Prime : Plan(1499, "prime") // ₹1499/year
    object PrimeLite : Plan(799,"primelite") // ₹799/year

    fun priceInRupees() = "${price}"}