package com.example.demoapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.R
import com.example.demoapp.blockchain.Blockchain
import com.example.demoapp.crypto.CryptoUtils
import com.example.demoapp.util.Web3Helper.web3j
import kotlinx.coroutines.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

val primaryColor = Color(0xFF1E88E5) // Blue color for the app
val secondaryColor = Color(0xFF64B5F6) // Lighter blue for accents

@Composable
fun ServiceOption(title: String, iconResId: Int, onClick: () -> Unit) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isHovered) 1.1f else 1f)

    Column(
        modifier = Modifier
            .padding(12.dp)
            .width(90.dp)
            .clickable(onClick = onClick)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = primaryColor.copy(alpha = if (isHovered) 0.8f else 0.5f),
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, _, _ ->
                        isHovered = true
                    }
                }
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center),
                tint = Color.White
            )
        }
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center,
            color = primaryColor
        )
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    dbHelper: DatabaseHelper,
    email: String,
    context: Context
) {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<DatabaseHelper.User?>(null) }
    val web3j = remember {
        Web3j.build(HttpService("https://mainnet.infura.io/v3/d9c2983e96d6440390d659f12896c839"))
    }

    // Text animation states
    val helpMessages = listOf(
        "Welcome back!",
        "How can I help you today?",
        "Need any assistance?",
        "I'm here to help!"
    )
    var currentText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf(helpMessages[0]) }
    var isAnimatingOut by remember { mutableStateOf(false) }
    var currentHelpIndex by remember { mutableStateOf(0) }

    // Animation values
    val animatedAlpha by animateFloatAsState(
        targetValue = if (currentText.isNotEmpty()) 1f else 0f,
        animationSpec = tween(300)
    )
    val animatedWidth by animateDpAsState(
        targetValue = if (currentText.isNotEmpty()) 180.dp else 0.dp,
        animationSpec = tween(500)
    )

    // Fetch user profile
    LaunchedEffect(email) {
        scope.launch {
            user = dbHelper.getUserProfile(email)
        }
    }

    // Text animation handler
    LaunchedEffect(Unit) {
        while (true) {
            // Set new target text
            targetText = helpMessages[currentHelpIndex]

            // Type in animation
            targetText.forEachIndexed { index, _ ->
                currentText = targetText.take(index + 1)
                delay(50)
            }

            delay(3000) // Show full message

            // Shrink out animation (remove letters one by one)
            isAnimatingOut = true
            while (currentText.isNotEmpty()) {
                currentText = currentText.dropLast(1)
                delay(30)
            }
            isAnimatingOut = false

            // Prepare next message
            currentHelpIndex = (currentHelpIndex + 1) % helpMessages.size
            delay(1000) // Pause before next message
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 25.dp, bottom = 80.dp)
            ) {
                // Animated textbox
                AnimatedVisibility(
                    visible = currentText.isNotEmpty(),
                    enter = expandHorizontally() + fadeIn(),
                    exit = shrinkHorizontally() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 180.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = currentText,
                            color = primaryColor,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chatbot FAB
                // In HomeScreen's FloatingActionButton:
                FloatingActionButton(
                    onClick = {
                        val keyPair = CryptoUtils.generateKeyPair()
                        navController.navigate("chatbot") {
                            // Optional: Clear back stack if needed
                            // popUpTo("homeScreen") { inclusive = false }
                        }
                    },
                    backgroundColor = primaryColor,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.chatbot),
                        contentDescription = "Chatbot",
                        tint = Color.White,
                        modifier = Modifier.size(29.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AdvertisementCarousel()
                    Spacer(modifier = Modifier.height(30.dp))

                    // Quick Access Section
                    Text(
                        text = "Quick Access",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ServiceOption("Blockchain Info", R.drawable.blockchain) {
                            fetchBlockchainInfo(context, web3j)
                        }
                        ServiceOption("Elder Care", R.drawable.eldercare) {
                            navController.navigate("elderCareScreen")
                        }
                        ServiceOption("Ambulance", R.drawable.ambulance) {
                            navController.navigate("ambulanceScreen")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ServiceOption("Pharmacy", R.drawable.pharmacy) {
                            navController.navigate("pharmacyScreen")
                        }
                        ServiceOption("Appointments", R.drawable.calendar) {
                            navController.navigate("appointmentScreen")
                        }
                        ServiceOption("Fun Games", R.drawable.console) {
                            navController.navigate("gamesScreen")
                        }
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Footer(navController, email, dbHelper, context)
                }
            }
        }
    )
}

@Composable
fun Footer(
    navController: NavController,
    email: String,
    dbHelper: DatabaseHelper,
    context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f))
            .shadow(elevation = 8.dp)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate("homeScreen") }) {
            Icon(Icons.Filled.Home, contentDescription = "Home", modifier = Modifier.size(40.dp), tint = primaryColor)
        }
        IconButton(onClick = { navController.navigate("walletScreen") }) {
            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet", modifier = Modifier.size(40.dp), tint = primaryColor)
        }
        IconButton(onClick = {
            navController.navigate("accountScreen/$email")
        }) {
            Icon(Icons.Filled.Person, contentDescription = "Profile",
                modifier = Modifier.size(40.dp), tint = primaryColor)
        }
        IconButton(onClick = { navController.navigate("settingsScreen") }) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(40.dp),
                tint = primaryColor
            )
        }
    }
}

@Composable
fun AdvertisementCarousel() {
    val ads = listOf(
        R.drawable.advertisement1,
        R.drawable.advertisement2,
        R.drawable.advertisement3,
        R.drawable.advertisement4
    )
    val pagerState = rememberPagerState(pageCount = { ads.size })
    var currentAdIndex by remember { mutableStateOf(0) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentAdIndex = page
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % ads.size)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = ads[page]),
                    contentDescription = "Advertisement",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(ads.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (index == currentAdIndex) primaryColor else Color.Gray,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                )
            }
        }
    }
}

fun fetchBlockchainInfo(context: Context, web3j: Web3j) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val clientVersion = web3j.web3ClientVersion().send()
            val versionInfo = "Blockchain Client: ${clientVersion.web3ClientVersion}"
            withContext(Dispatchers.Main) {
                Toast.makeText(context, versionInfo, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error fetching blockchain info", Toast.LENGTH_SHORT).show()
            }
        }
    }
}