package com.app.ecomapp.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.ui.theme.Montserrat


@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    PrimeSubscribe({},{})
}


@Composable
fun PrimeSubscribe( onContinue: () -> Unit,
                    onNoThanks: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F7FF))
            .padding(16.dp),

        ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0073E6), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\u2728 Limited Period Offer \u2728",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, fontFamily = Montserrat
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Prime Shopping Edition",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black, fontFamily = Montserrat
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Super savings, rocket speed deliveries & more with",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray, fontFamily = Montserrat
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(
                            1.dp,
                            Color.LightGray,
                            RoundedCornerShape(12.dp)
                        ) // Border with rounded corners
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) // White background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OfferItem(
                            "FREE Same day & 1 day delivery",
                            "\u2728 Unlimited \u2728 No minimum order value"
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        ) // Divider between items
                        OfferItem("UNLIMITED 5% Cashback", "\u2728 with Amazon Pay ICICI card")
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        ) // Divider between items
                        OfferItem("UP TO 10% Off", "\u2728 with Prime offers")
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        ) // Divider between items
                        OfferItem("EARLY access", "\u2728 to shopping events")
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        ) // Divider between items
                        OfferItem("5% Cashback", "\u2728 on all Uber rides")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    /*Icon(
                        painter = painterResource(id = R.drawable.ic_home), // Use proper icon
                        contentDescription = "Discount Icon",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )*/
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "at ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0073E6),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.wrapContentWidth(), fontFamily = Montserrat
                    )
                    Text(
                        "₹999",
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "₹499/year", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF0073E6), fontFamily = Montserrat
                    )
                }

                //Spacer(modifier = Modifier.height(8.dp))
                // Prime Video & Music Not Included
               /* Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Prime Video not included",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontFamily = Montserrat
                        )
                        Text(
                            "Prime Music not included",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontFamily = Montserrat
                        )
                    }
                }*/

                Spacer(modifier = Modifier.height(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { onContinue()},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Continue with Prime Shopping Edition",
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontFamily = Montserrat
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { onNoThanks()},
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text("No Thanks", fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}

@Composable
fun OfferItem(title: String, subtitle: String) {

    Row(
        modifier = Modifier.padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(Icons.Default.Star, contentDescription = "Feature Icon", tint = Color.Blue)
//        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Montserrat
            )
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray, fontFamily = Montserrat)
            Spacer(Modifier.height(1.dp))
        }
    }
}

