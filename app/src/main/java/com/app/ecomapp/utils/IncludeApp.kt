package com.app.ecomapp.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.compose.jetshop.R
import com.app.ecomapp.utils.CommonFunction.openLink


class IncludeApp {
    @Composable
    fun CustomDivider(modifier: Modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material3.HorizontalDivider(
            modifier = modifier,
            thickness = 1.dp,
            color = Color(0xFFB3B3B3)
        )
    }


    @Composable
    fun DashedDivider() {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        Canvas(Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(
                color = Color(0xFF0086F9),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = pathEffect
            )
        }
    }

    @ExperimentalMaterial3Api
    @ExperimentalLayoutApi
    @Composable
    fun ShowAbout(onDismiss: () -> Unit) {
        val mContext = LocalContext.current
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Image(
                    painter = painterResource(id =  R.drawable.logo),
                    modifier = Modifier.size(50.dp),
                    contentDescription = null
                )
            },
            title = { Text(text = stringResource(id = R.string.app_name)) },
            text = {
                Text(stringResource(R.string.about_text))
            },
            confirmButton = {
                Button(modifier = Modifier.fillMaxWidth(), onClick
                = {
                    openLink(mContext, "https://github.com/Dinesh2510/")
                }
                ) {
                    Text("Get Code")
                }

            }
        )
    }

    @Composable
    fun emptyState() {
        Column(
            modifier = Modifier.width(200.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer_12dp()
                Column(horizontalAlignment = Alignment.Start) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer_8dp()
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
            Spacer_12dp()
            Row(horizontalArrangement = Arrangement.Start) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer_12dp()
                Column(horizontalAlignment = Alignment.Start) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer_8dp()
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}