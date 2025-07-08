package com.example.demoapp.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.ui.theme.Typography

@SuppressLint("Range")
@Composable
fun PatientDetailsScreen(navController: NavController, patientId: Int? = null) {
    // State variables for form inputs
    var guardianName by remember { mutableStateOf("") }
    var guardianContact by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var patientEmail by remember { mutableStateOf("") }
    var patientPhone by remember { mutableStateOf("") }
    var patientPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var prescriptionUri by remember { mutableStateOf<Uri?>(null) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessages by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)

    // Scrollable form
    val scrollState = rememberScrollState()

    // File picker launcher
    val filePickerLauncher: ActivityResultLauncher<String> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> prescriptionUri = uri }
    )

    // If patientId is provided, load patient details
    patientId?.let {
        val patientDetails = dbHelper.getPatientDetailsById(it)
        patientDetails?.let {
            guardianName = it.guardianName
            guardianContact = it.guardianContact
            val addressParts = it.patientAddress.split(",")
            addressLine1 = addressParts[0]
            addressLine2 = addressParts.getOrElse(1) { "" }
            city = addressParts.getOrElse(2) { "" }
            state = addressParts.getOrElse(3) { "" }
            pincode = addressParts.getOrElse(4) { "" }
            patientEmail = it.patientEmail
            patientPhone = it.patientPhone
            patientPassword = it.patientPassword
            prescriptionUri = it.prescriptionUri?.let { Uri.parse(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Form fields for patient details
        Text("Patient Details", style = Typography.h5, modifier = Modifier.padding(bottom = 16.dp))

        FormField(
            value = guardianName,
            onValueChange = { guardianName = it },
            label = "Guardian Name",
            error = errorMessages["guardianName"]
        )

        FormField(
            value = guardianContact,
            onValueChange = { guardianContact = it },
            label = "Guardian Contact",
            keyboardType = KeyboardType.Phone,
            error = errorMessages["guardianContact"]
        )

        FormField(
            value = addressLine1,
            onValueChange = { addressLine1 = it },
            label = "Address Line 1",
            error = errorMessages["addressLine1"]
        )

        FormField(
            value = addressLine2,
            onValueChange = { addressLine2 = it },
            label = "Address Line 2",
            error = errorMessages["addressLine2"]
        )

        FormField(
            value = city,
            onValueChange = { city = it },
            label = "City",
            error = errorMessages["city"]
        )

        FormField(
            value = state,
            onValueChange = { state = it },
            label = "State",
            error = errorMessages["state"]
        )

        FormField(
            value = pincode,
            onValueChange = { pincode = it },
            label = "Pincode",
            keyboardType = KeyboardType.Number,
            error = errorMessages["pincode"]
        )

        FormField(
            value = patientEmail,
            onValueChange = { patientEmail = it },
            label = "Patient Email",
            keyboardType = KeyboardType.Email,
            error = errorMessages["patientEmail"]
        )

        FormField(
            value = patientPhone,
            onValueChange = { patientPhone = it },
            label = "Patient Phone",
            keyboardType = KeyboardType.Phone,
            error = errorMessages["patientPhone"]
        )

        PasswordField(
            value = patientPassword,
            onValueChange = { patientPassword = it },
            label = "Patient Password",
            passwordVisibility = passwordVisibility,
            onPasswordVisibilityChange = { passwordVisibility = it },
            error = errorMessages["patientPassword"]
        )

        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            passwordVisibility = confirmPasswordVisibility,
            onPasswordVisibilityChange = { confirmPasswordVisibility = it },
            error = errorMessages["confirmPassword"]
        )

        // Prescription URI selection
        Button(
            onClick = { filePickerLauncher.launch("application/pdf") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Prescription File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = {
                if (validateForm()) {
                    isLoading = true
                    // Perform save operation
                    if (patientId != null) {
                        // Update existing patient logic
                    } else {
                        // Insert new patient logic
                    }
                    Toast.makeText(context, "Patient details saved successfully", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colors.primary)
            } else {
                Text("Save Patient Details")
            }
        }
    }
}

// Helper composable for form fields
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.error),
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

// Helper composable for password fields
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisibility: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisibility) }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.error),
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

// Form validation
fun validateForm(): Boolean {
    // Basic form validation (to be improved)
    return true
}
