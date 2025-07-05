package com.app.ecomapp.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.app.ecomapp.data.models.blogs.BlogData
import com.app.ecomapp.presentation.map.GoogleMapScreen
import com.app.ecomapp.presentation.screens.MainScreen
import com.app.ecomapp.presentation.screens.profile.HelpSectionScreen
import com.app.ecomapp.presentation.screens.profile.notification.NotificationScreen
import com.app.ecomapp.presentation.screens.profile.CouponCodeScreen
import com.app.ecomapp.presentation.screens.profile.SavedAddressScreen
import com.app.ecomapp.presentation.screens.profile.UpdateProfileScreen
import com.app.ecomapp.presentation.screens.home.ProductDetailScreen
import com.app.ecomapp.presentation.screens.auth.forgotPass.ForgotPasswordScreen
import com.app.ecomapp.presentation.screens.auth.login.LoginScreen
import com.app.ecomapp.presentation.screens.auth.onBoard.OnBoardingScreen
import com.app.ecomapp.presentation.screens.auth.otp.OTPScreen
import com.app.ecomapp.presentation.screens.auth.resetPassword.ResetPasswordScreen
import com.app.ecomapp.presentation.screens.auth.register.RegisterScreen
import com.app.ecomapp.presentation.screens.cart.CheckoutScreen
import com.app.ecomapp.presentation.components.SuccessScreen
import com.app.ecomapp.presentation.screens.home.CategoryBrandDetails
import com.app.ecomapp.presentation.screens.home.CategoryBrandSplitScreen
import com.app.ecomapp.presentation.screens.home.CategoryGridScreen
import com.app.ecomapp.presentation.screens.home.ProductListScreen
import com.app.ecomapp.presentation.screens.order.OrderScreen
import com.app.ecomapp.presentation.screens.profile.UserOrderScreen
import com.app.ecomapp.presentation.screens.profile.BlogDetailsScreen
import com.app.ecomapp.presentation.screens.profile.BlogListScreen
import com.app.ecomapp.presentation.screens.profile.ManagedSetting
import com.app.ecomapp.presentation.screens.profile.WebViewScreen
import com.app.ecomapp.presentation.screens.profile.referral.InviteFriendsScreen
import com.app.ecomapp.presentation.screens.search.SearchScreen
import com.app.ecomapp.presentation.screens.splash.SplashScreen
import com.google.gson.Gson

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.OnBoard.route) { OnBoardingScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("code") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://pixeldev.in/referinvite/{code}"
                }
            )
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: ""
            RegisterScreen(navController = navController, referCode = code)
        }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
        composable(Screen.UpdateProfile.route) { UpdateProfileScreen(navController) }
        composable("otp_screen/{email}/{name}") { backStackEntry ->
            OTPScreen(navController, backStackEntry)
        }
        composable(Screen.PasswordReset.route + "/{email}/{name}") { backStackEntry ->
            ResetPasswordScreen(navController, backStackEntry)
        }
        // ✅ Main App Screen with Bottom Navigation (After Login)
        composable(Screen.Main.route) { MainScreen(navController) }
        composable(Screen.CategoryGridScreen.route + "/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "category"
            val isCategoryScreen = type == "category"
            CategoryGridScreen(navController = navController, isCategoryScreen = isCategoryScreen)
        }
        composable(
            route = Screen.ProductDetails.route + "/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://pixeldev.in/product/{productId}"
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                navController = navController,
                backStackEntry = backStackEntry,
                productId = productId
            )
        }
        composable(Screen.HelpScreen.route) { HelpSectionScreen(navController) }
        composable(Screen.Notification.route) { NotificationScreen(navController) }
        composable(Screen.PromoCode.route) { CouponCodeScreen(navController) }
        composable(Screen.Address.route) { SavedAddressScreen(navController) }
        composable(Screen.GoogleLocation.route) { GoogleMapScreen(navController) }
        composable(Screen.SearchScreen.route) { SearchScreen(navController) }
        composable(Screen.CheckoutScreen.route) { CheckoutScreen(navController) }
        composable(Screen.UserOrderScreen.route) { UserOrderScreen(navController) }
        composable(Screen.SettingScreen.route) { ManagedSetting(navController)}
        composable(Screen.SuccessScreen.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("order_id") ?: ""
            SuccessScreen(navController, orderId) // Pass orderId to the screen
        }
        composable(Screen.OrderScreen.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("order_id") ?: ""
            OrderScreen(navController, orderId) // Pass orderId to the screen
        }
        composable(Screen.CategoryBrandDetails.route + "/{type}/{id}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CategoryBrandDetails(navController,type, id)
        }
        composable(Screen.AllProductList.route) {
            ProductListScreen(navController)
        }
        /*composable(Screen.BlogsDetailsScreen.route) {
            BlogDetailsScreen(navController)
        }*/
        composable(Screen.BlogsScreen.route) {
            BlogListScreen(navController)
        }
        composable(
            route = Screen.BlogsDetailsScreen.route+"/{blogJson}",
            arguments = listOf(navArgument("blogJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val blogJson = backStackEntry.arguments?.getString("blogJson")
            val blog = Gson().fromJson(blogJson, BlogData::class.java)
            BlogDetailsScreen(blog = blog, navController = navController)
        }

        composable(
            route = Screen.CategoryBrandSplit.route,
            arguments = listOf(navArgument("type") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "category"
            CategoryBrandSplitScreen(
                navController = navController,
                isCategoryScreen = type == "category"
            )
        }

        composable(
            route = Screen.WebViewScreen.route+"/{title}/{encodedUrl}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("encodedUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val url = Uri.decode(backStackEntry.arguments?.getString("encodedUrl") ?: "")
            WebViewScreen(title = title, url = url) {
                navController.popBackStack()
            }
        }
        composable(Screen.ReferralScreen.route) {InviteFriendsScreen(navController) }

    }

}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object OnBoard : Screen("onboard_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen/{code}")
    object OtpScreen : Screen("otp_screen/{email}/{name}") // Define route with placeholders
    object ForgotPassword : Screen("forgot_password_screen")
    object PasswordReset : Screen("reset_password_screen/{email}/{name}")
    object Main : Screen("home_screen")
    object HelpScreen : Screen("help_screen")
    object Notification : Screen("noti")
    object PromoCode : Screen("promocode")
    object Address : Screen("address")
    object UpdateProfile : Screen("updateProfile")
    object GoogleLocation : Screen("googleMap")
    object SettingScreen : Screen("setting_screen")
    object SearchScreen : Screen("search")
    object ProductDetails : Screen("product_details_screen/{product_id}}")
    object CheckoutScreen : Screen("checkout")
    object OrderScreen : Screen("orderScreen/{order_id}")
    object SuccessScreen : Screen("success_screen/{order_id}")
    object UserOrderScreen : Screen("userOrderScreen")
    object CategoryGridScreen : Screen("category_grid")
    object CategoryBrandDetails : Screen("category_brand_details")
    object AllProductList : Screen("all_product_list")
    object BlogsScreen : Screen("blog")
    object WebViewScreen : Screen("webviewScreen")
    object BlogsDetailsScreen : Screen("blog_details")
    object ReferralScreen : Screen("referral")
    object CategoryBrandSplit : Screen("category_brand_split/{type}") {
        fun createRoute(type: String) = "category_brand_split/$type"
    }

}
