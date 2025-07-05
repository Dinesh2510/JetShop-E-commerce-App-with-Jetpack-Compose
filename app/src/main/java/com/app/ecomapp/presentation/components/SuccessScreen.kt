package com.app.ecomapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.compose.jetshop.R


@Composable
fun SuccessScreen(navController: NavController,paymentId: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier
            .fillMaxHeight(0.66f)
            .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier= Modifier.size(200.dp)) {
                LottieAnimationView(animationResId = R.raw.tick)
            }
            Spacer(modifier = Modifier.height(60.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                DarkBlue,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("Your Order has been Accepted")
                        }

                        withStyle(
                            style = SpanStyle(
                                Color.DarkGray,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("\t\nOrder Id: $paymentId")
                        }

                    },
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Your order has been placed and is on",  fontFamily = Montserrat,
                fontWeight = FontWeight.Normal)
            Text(text = "its way to being processed",  fontFamily = Montserrat,
                fontWeight = FontWeight.Normal)
        }
        Button(
            modifier = Modifier
                .height(57.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(0) // ✅ Clears the entire back stack
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF53B175),
                contentColor = Color(0xFFFFFFFF)
            ),
            shape = RoundedCornerShape(19),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp,
                pressedElevation = 8.dp)
        ) {
            Text(
                text = "Back To Shop",
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF)
            )
        }
    }
}