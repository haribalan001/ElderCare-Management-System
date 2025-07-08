package com.example.demoapp.screens
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp)
        )


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 8.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = false },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = MaterialTheme.shapes.medium,
                    isError = emailError
                )

                if (emailError) {
                    Text(
                        text = "Please enter a valid email",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = false },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(
                                imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    isError = passwordError
                )

                if (passwordError) {
                    Text(
                        text = "Password cannot be empty",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = {
                        if (email.isEmpty()) {
                            emailError = true
                        }
                        if (password.isEmpty()) {
                            passwordError = true
                        }
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            coroutineScope.launch {
                                val isValid = dbHelper.validateUserCredentials(email, password)
                                isLoading = false
                                if (isValid) {
                                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()

                                    val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("loggedInEmail", email) // ✅ Store logged-in email
                                    editor.putBoolean("isLoggedIn", true)
                                    editor.apply()

                                    navController.navigate("homeScreen")
                                } else {
                                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Login")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { navController.navigate("forgotPasswordScreen") }) {
                    Text("Forgot Password?")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { navController.navigate("signUpScreen") }) {
                    Text("Don't have an account? Sign Up")
                }
            }
        }
    }
}