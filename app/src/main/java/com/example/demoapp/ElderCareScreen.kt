package com.example.demoapp.screens

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.util.Web3Helper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ElderCareScreen(
    navController: NavController,
    dbHelper: DatabaseHelper,
    email: String,
    context: Context
) {
    var patientName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var shiftStartTime by remember { mutableStateOf("") }
    var shiftEndTime by remember { mutableStateOf("") }
    var assignedCaregiver: DatabaseHelper.Caregiver? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(false) }
    var snackbarVisible by remember { mutableStateOf(false) }
    val snackbarMessage = "Caregiver assigned: ${assignedCaregiver?.name ?: "N/A"}"
    val coroutineScope = rememberCoroutineScope()

    var showCaregiverDialog by remember { mutableStateOf(false) }

    val isBlockchainConnected = remember { Web3Helper.isConnected() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (isBlockchainConnected) "Blockchain Connected " else "Blockchain Not Connected ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isBlockchainConnected) Color.Green else Color.Red
        )
    }

    CaregiverPromptDialog(
        showDialog = showCaregiverDialog,
        onDismiss = { showCaregiverDialog = false },
        onAssignRandom = {
            assignedCaregiver = dbHelper.getAvailableCaregiver(gender, assignAny = true)
            showCaregiverDialog = false
            if (assignedCaregiver != null) {
                Toast.makeText(context, "Random Caregiver Assigned: ${assignedCaregiver!!.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No Caregivers Available!", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val handleSubmit = {
        if (patientName.isNotEmpty() && gender.isNotEmpty()) {
            isLoading = true
            coroutineScope.launch {
                delay(2000)
                val result = dbHelper.insertElderCareDetails(patientName, gender, shiftStartTime, shiftEndTime)
                if (result) {
                    assignedCaregiver = dbHelper.getAvailableCaregiver(gender, assignAny = false)

                    if (assignedCaregiver == null) {
                        showCaregiverDialog = true
                    } else {
                        val blockchainResult = Web3Helper.assignCaregiverOnBlockchain(
                            assignedCaregiver!!.name,
                            patientName,
                            shiftStartTime,
                            shiftEndTime
                        )
                        if (blockchainResult) {
                            snackbarVisible = true
                            Toast.makeText(context, "Caregiver Assigned: ${assignedCaregiver?.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Blockchain Assignment Failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to Save Data!", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }
        } else {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Elder Care Services", fontSize = 28.sp, fontWeight = FontWeight.Bold, color=primaryColor)

                CustomTextField("Patient Name", patientName) { patientName = it }

                Text("Gender", fontSize = 18.sp, fontWeight = FontWeight.Bold, color=primaryColor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GenderButton("Male", gender, { gender = "Male" })
                    GenderButton("Female", gender, { gender = "Female" })
                }

                Text("Shift Timing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                TimePickerField("Start Time", shiftStartTime) { shiftStartTime = it }
                TimePickerField("End Time", shiftEndTime) { shiftEndTime = it }

                Button(
                    onClick = {handleSubmit()},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Submit", fontSize = 18.sp, color = Color.White)
                    }
                }
                assignedCaregiver?.let { CaregiverCard(it)}
            }
        }
        if (snackbarVisible) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = { TextButton(onClick = { snackbarVisible = false }) { Text("Dismiss", color = Color.White) } }
            ) { Text(snackbarMessage) }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .align(Alignment.BottomCenter)
        ) {
            Footer(navController, email, dbHelper, context)
        }
    }
}
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    var isHovered by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
            detectTapGestures(onPress = {
                isHovered = true
                tryAwaitRelease()
                isHovered = false
            })
        }.background(if (isHovered) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent),
        shape = RoundedCornerShape(12.dp)
    )
}
@Composable
fun TimePickerField(label: String, timeValue: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val showTimePicker = remember {
        {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val amPm = if (hour < 12) "AM" else "PM"
                    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
                    val selectedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
                    onTimeSelected(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().clickable { showTimePicker() }
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = timeValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { showTimePicker() },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Select Time",
                    modifier = Modifier.clickable { showTimePicker() }
                )
            }
        )
    }
}



@Composable
fun GenderButton(gender: String, selectedGender: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = if (gender == selectedGender) primaryColor else Color.Gray),
        shape = RoundedCornerShape(50),
        modifier = Modifier.width(140.dp)
    ) { Text(gender, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White) }
}

@Composable
fun CaregiverCard(caregiver: DatabaseHelper.Caregiver) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Assigned Caregiver", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Name: ${caregiver.name}", fontSize = 16.sp)
            Text(text = "Gender: ${caregiver.gender}", fontSize = 16.sp)
        }
    }
}

@Composable
fun CaregiverPromptDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAssignRandom: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Alert", tint = primaryColor, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Caregivers Available!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Would you like to assign a random caregiver?", fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                            Text("Cancel", color = Color.White)
                        }
                        Button(onClick = onAssignRandom, colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor)) {
                            Text("Assign Random", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}