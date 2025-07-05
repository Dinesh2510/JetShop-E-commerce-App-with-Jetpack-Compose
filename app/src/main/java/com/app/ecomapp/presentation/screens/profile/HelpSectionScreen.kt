package com.app.ecomapp.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.navigation.NavHostController
import com.app.ecomapp.presentation.components.LabelMedium
import com.app.ecomapp.presentation.components.SubtitleLarge
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSectionScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    val faqs = listOf(
        "How to place an order?" to "Go to the product page, add the item to your cart, and proceed to checkout.",
        "What is the return policy?" to "You can return products within 30 days of delivery, provided they meet our return conditions.",
        "How to track my order?" to "Go to 'My Orders' and click on 'Track Order' to view the delivery status.",
        "How to reset my password?" to "Click on 'Forgot Password' on the login screen and follow the instructions sent to your email.",
        "How to contact customer support?" to "You can email us at support@ecomapp.com or call 123-456-7890.",
        "Can I cancel my order?" to "Yes, you can cancel your order before it has been shipped by going to 'My Orders'.",
        "What payment methods are accepted?" to "We accept credit cards, debit cards, UPI, net banking, and various digital wallets.",
        "What should I do if my payment fails?" to "Ensure your payment details are correct. If the issue persists, contact your bank or try a different payment method.",
        "Can I change my delivery address after placing an order?" to "Yes, but only before the order is shipped. You can update your address from the 'My Orders' section.",
        "Why was my order canceled automatically?" to "Orders may be canceled due to payment issues, stock unavailability, or failed verification.",
        "Is there any refund policy?" to "No refunds will be issued once the order is successfully delivered, except in cases of defective or damaged products.",
        "Do you offer international shipping?" to "Currently, we only ship within the country. International shipping is not available at the moment.",
        "How do I know if my order is confirmed?" to "Once your order is confirmed, you will receive an email and SMS with the order details.",
        "What should I do if I received a damaged product?" to "Please contact our support within 48 hours of delivery with images of the damaged product.",
        "How long does delivery take?" to "Standard delivery typically takes 3-7 business days, depending on your location."
    )


    Scaffold(topBar = {
        ToolbarWithBackButtonAndTitle("Help & Support",
            onBackClick = { navController.popBackStack() })
    }) {padding ->
       Column(
           modifier = Modifier
               .fillMaxSize()
               .padding(padding)
               .padding(16.dp)
       ) {

           // Search Bar
           OutlinedTextField(
               value = searchQuery,
               onValueChange = { searchQuery = it },
               label = { LabelMedium(text = "Search FAQs...") },
               modifier = Modifier.fillMaxWidth()
           )

           Spacer(modifier = Modifier.height(16.dp))

           // FAQs List
           LazyColumn {
               items(faqs.filter { it.first.contains(searchQuery.text, ignoreCase = true) }) { faq ->
                   ExpandableFaqItem(question = faq.first, answer = faq.second)
               }
           }

           Spacer(modifier = Modifier.height(24.dp))

           // Contact Support Button
           Button(
               onClick = {
                   Toast.makeText(context, "Contacting Support...", Toast.LENGTH_SHORT).show()
               },
               modifier = Modifier
                   .fillMaxWidth()
                   .height(50.dp),
               colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
           ) {
               Text("Contact Support", color = Color.White, fontSize = 16.sp)
           }
       }
   }


}

@Composable
fun ExpandableFaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SubtitleLarge(text = question)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                LabelMedium(text = answer, modifier = Modifier)
            }
        }
    }

}