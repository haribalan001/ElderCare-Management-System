package com.example.demoapp

import AppointmentScreen
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.demoapp.blockchain.Blockchain
import com.example.demoapp.crypto.CryptoUtils
import com.example.demoapp.screens.*
import com.example.demoapp.util.Web3Helper
import com.example.demoapp.utils.ContractDeployer

import com.example.demoapp.utils.Web3Utils
import kotlinx.coroutines.*
import org.web3j.crypto.Credentials


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val loggedInEmail = sharedPreferences.getString("loggedInEmail", "") ?: ""


        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("Blockchain", "🚀 Checking Web3 Connection...")

                if (Web3Helper.isConnected()) {
                    Log.d("Blockchain", " Web3 is connected Successfully!")

                    val contractDeployer = ContractDeployer(Web3Helper.web3j, Web3Helper.credentials)

                    Log.d("Blockchain", "Deploying Contract... Please wait!")
                    val contractAddress = contractDeployer.deployContract()

                    if (contractAddress.isNotEmpty()) {
                        Log.d("Blockchain", " Deployed Contract Address: $contractAddress")
                    } else {
                        Log.e("Blockchain", " Contract deployment failed!")
                    }
                } else {
                    Log.e("Blockchain", " Web3 connection failed.")
                }
            } catch (e: Exception) {
                Log.e("Blockchain", "Error deploying contract: ${e.message}", e)
            }
        }




        // Initialize BlockchainHelper and load ABI
        val blockchainHelper = BlockchainHelper(this)
        val abi = blockchainHelper.getAbi()
        Log.d("Blockchain", "Loaded ABI: $abi")

        Web3Utils.checkWeb3Connection()

        // Set the content for the app
        setContent {
            DemoApp(loggedInEmail)
        }
    }
}

@Composable
fun DemoApp(loggedInEmail: String) {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context = context)
    val cartItems = remember { mutableStateMapOf<Medicine, Int>() }
    val blockchain = remember { Blockchain() }

    NavHost(
        navController = navController,
        startDestination = "homeScreen"
    ) {
        composable("loginScreen") { LoginScreen(navController) }
        composable("signUpScreen") { SignUpScreen(navController) }
        composable("walletScreen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            WalletScreen(navController, dbHelper, userId)
        }
        composable("homeScreen") {
            HomeScreen(
                navController = navController,
                dbHelper = dbHelper,
                loggedInEmail, // Pass email as needed after login
                context = context
            )
        }
        composable("elderCareScreen") {
            ElderCareScreen(
                navController = navController,
                dbHelper = dbHelper,
                loggedInEmail,
                context = context
            )
        }
        composable("ambulanceScreen") {
            AmbulanceScreen(
                navController = navController,
                dbHelper = dbHelper,
                email = "",
                context = context
            )
        }
        composable("pharmacyScreen") {
            PharmacyScreen(navController, context, cartItems)
        }
        composable("appointmentScreen") {
            AppointmentScreen(
                navController = navController,
                dbHelper = DatabaseHelper(context),
                email = ""
            )
        }
        composable("cartScreen") {
            CartScreen(navController, cartItems)
        }

        composable("gamesScreen") {
            GamesScreen(
                navController = navController,
                dbHelper = dbHelper,
                loggedInEmail,
                context = context
            )
        }
        composable("settingsScreen") {
            SettingsScreen(
                navController = navController,
                dbHelper = dbHelper,
                email = loggedInEmail
            )
        }
        composable("patientdetails") {
            PatientDetailsScreen(
                navController = navController
            )
        }
        composable("2pGamesScreen") {
            TwoPlayerGameScreen(navController)
        }
        composable("1pGamesScreen") {
            OnePlayerGameScreen(navController)
        }
        composable(
            route = "accountScreen/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            AccountScreen(
                navController = navController,
                email = email,
                dbHelper = dbHelper
            )
        }
        composable("chatbot") {
            SecureChatbotScreen(
                navController = navController,
                blockchain = remember { Blockchain() }, // Initialize if needed
                userKeyPair = remember { CryptoUtils.generateKeyPair() } // Generate new keypair
            )
        }
        composable("payment/{amount}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 0
            PaymentScreen(navController, amount)
        }
    }
}



