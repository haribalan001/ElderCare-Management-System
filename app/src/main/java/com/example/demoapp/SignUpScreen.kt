package com.example.demoapp.screens

import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.R
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var mobileNumberError by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo), // Replace with your logo resource
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp)
            )

            // Sign Up Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.h4,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Email input field
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it; emailError = false },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        icon = Icons.Default.Email,
                        isError = emailError
                    )

                    if (emailError) {
                        Text(
                            text = "Please enter a valid email",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mobile Number input field
                    CustomTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it; mobileNumberError = false },
                        label = "Mobile Number",
                        keyboardType = KeyboardType.Phone,
                        icon = Icons.Default.Phone,
                        isError = mobileNumberError
                    )

                    if (mobileNumberError) {
                        Text(
                            text = "Please enter a valid mobile number",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password input field
                    CustomPasswordField(
                        value = password,
                        onValueChange = { password = it; passwordError = false },
                        label = "Password",
                        isPasswordVisible = passwordVisibility,
                        onPasswordVisibilityChange = { passwordVisibility = it },
                        leadingIcon = Icons.Filled.Lock,
                        isError = passwordError
                    )

                    if (passwordError) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password input field
                    CustomPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = false },
                        label = "Confirm Password",
                        isPasswordVisible = confirmPasswordVisibility,
                        onPasswordVisibilityChange = { confirmPasswordVisibility = it },
                        leadingIcon = Icons.Filled.Lock,
                        isError = confirmPasswordError
                    )

                    if (confirmPasswordError) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Image Picker
                    ProfileImagePicker(profileImageUri) { uri ->
                        profileImageUri = uri
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Terms and Conditions Checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF6200EE),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "I accept the terms and conditions",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign Up button
                    Button(
                        onClick = {
                            // Validate inputs
                            emailError = email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            mobileNumberError = mobileNumber.isEmpty() || mobileNumber.length != 10
                            passwordError = password.length < 6
                            confirmPasswordError = password != confirmPassword

                            if (!emailError && !mobileNumberError && !passwordError && !confirmPasswordError && termsAccepted) {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val success = dbHelper.insertUserDetails(
                                            email, password, mobileNumber, profileImageUri?.toString() ?: ""
                                        )
                                        if (success) {
                                            Toast.makeText(context, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                                            navController.navigate("LoginScreen")
                                        } else {
                                            Toast.makeText(context, "Failed to Register User", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else if (!termsAccepted) {
                                Toast.makeText(context, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Sign Up",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigate to Login Screen
                    TextButton(onClick = { navController.navigate("LoginScreen") }) {
                        Text(
                            text = "Already have an account? Login",
                            color = Color(0xFF6200EE),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileImagePicker(profileImageUri: Uri?, onImageSelected: (Uri?) -> Unit) {
    val context = LocalContext.current
    val openGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onImageSelected(uri)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { openGalleryLauncher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF6200EE),
                contentColor = Color.White
            )
        ) {
            Text(text = "Select Profile Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        profileImageUri?.let { uri ->
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { if (icon != null) Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) MaterialTheme.colors.error else Color(0xFF6200EE),
            unfocusedBorderColor = if (isError) MaterialTheme.colors.error else Color(0xFF6200EE)
        ),
        isError = isError,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) MaterialTheme.colors.error else Color(0xFF6200EE),
            unfocusedBorderColor = if (isError) MaterialTheme.colors.error else Color(0xFF6200EE)
        ),
        isError = isError,
        shape = RoundedCornerShape(12.dp)
    )
}