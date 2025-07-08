import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.screens.Footer
import java.util.*

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val formattedHour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val selectedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
            onTimeSelected(selectedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    ).show()
}

@Composable
fun AppointmentScreen(
    navController: NavController,
    dbHelper: DatabaseHelper,
    email: String
) {
    val context = LocalContext.current

    val hospitals = listOf(
        "Apollo Hospital", "Fortis Hospital", "AIIMS Delhi", "Manipal Hospital", "Medanta Hospital",
        "Max Super Speciality Hospital", "Narayana Health", "BLK-Max Hospital", "Kokilaben Dhirubhai Ambani Hospital",
        "Tata Memorial Hospital", "Lilavati Hospital", "Care Hospital", "Continental Hospital", "Columbia Asia Hospital",
        "Hinduja Hospital", "Sankara Nethralaya", "Sri Ramachandra Medical Centre", "AMRI Hospitals", "Yashoda Hospitals",
        "Cloudnine Hospital", "Artemis Hospital", "Jaslok Hospital", "Ruby Hall Clinic", "Bhagwan Mahavir Hospital",
        "Dr. L H Hiranandani Hospital", "Rajiv Gandhi Cancer Institute", "Sir Ganga Ram Hospital", "St. John's Medical College Hospital",
        "Christian Medical College (CMC)", "Government General Hospital Chennai"
    )

    val doctorsMap = mapOf(
        "Apollo Hospital" to listOf("Dr. Rajeev Menon", "Dr. Priya Sharma", "Dr. Anil Kumar"),
        "Fortis Hospital" to listOf("Dr. Neha Gupta", "Dr. Suresh Reddy", "Dr. Vikram Singh"),
        "AIIMS Delhi" to listOf("Dr. Ramesh Verma", "Dr. Kavita Joshi", "Dr. Deepak Mehta"),
        "Manipal Hospital" to listOf("Dr. Alok Jain", "Dr. Swati Deshmukh", "Dr. Rohan Patel"),
        "Medanta Hospital" to listOf("Dr. Sudhir Kapoor", "Dr. Anjali Saxena", "Dr. Arvind Yadav"),
        "Max Super Speciality Hospital" to listOf("Dr. Manoj Nair", "Dr. Pooja Iyer", "Dr. Karan Chopra"),
        "Narayana Health" to listOf("Dr. Sanjay Kumar", "Dr. Rachna Bhatia", "Dr. Amit Gupta"),
        "BLK-Max Hospital" to listOf("Dr. Sneha Malhotra", "Dr. Ravi Prakash", "Dr. Vishal Chatterjee"),
        "Kokilaben Dhirubhai Ambani Hospital" to listOf("Dr. Sunil Mehra", "Dr. Aditi Verma", "Dr. Mohan Rao"),
        "Tata Memorial Hospital" to listOf("Dr. Sameer Kulkarni", "Dr. Deepa Nair", "Dr. Harish Kumar"),
        "Lilavati Hospital" to listOf("Dr. Nisha Agarwal", "Dr. Uday Shetty", "Dr. Parth Joshi"),
        "Care Hospital" to listOf("Dr. Rajat Saxena", "Dr. Rekha Menon", "Dr. Sandeep Rawat"),
        "Continental Hospital" to listOf("Dr. Vinod Kumar", "Dr. Saira Khan", "Dr. Prakash Mehta"),
        "Columbia Asia Hospital" to listOf("Dr. Kishore Sharma", "Dr. Priyanka Mishra", "Dr. Sunita Yadav"),
        "Hinduja Hospital" to listOf("Dr. Rahul Iyer", "Dr. Sanjana Gupta", "Dr. Anupama Reddy"),
        "Sankara Nethralaya" to listOf("Dr. Pradeep Agarwal", "Dr. Savita Nair", "Dr. Tanmay Rao"),
        "Sri Ramachandra Medical Centre" to listOf("Dr. Deepak Sharma", "Dr. Uma Maheshwari", "Dr. Suresh Tiwari"),
        "AMRI Hospitals" to listOf("Dr. Atul Khanna", "Dr. Meenal Chopra", "Dr. Raghav Deshpande"),
        "Yashoda Hospitals" to listOf("Dr. Krishnan Iyer", "Dr. Ayesha Kaur", "Dr. Mahendra Verma"),
        "Cloudnine Hospital" to listOf("Dr. Nandini Saxena", "Dr. Manish Kapoor", "Dr. Priti Sinha"),
        "Artemis Hospital" to listOf("Dr. Tarun Gupta", "Dr. Varsha Mehta", "Dr. Suraj Kumar"),
        "Jaslok Hospital" to listOf("Dr. Rajesh Khanna", "Dr. Sonali Sharma", "Dr. Devendra Singh"),
        "Ruby Hall Clinic" to listOf("Dr. Rohan Desai", "Dr. Shruti Rao", "Dr. Ashok Patel"),
        "Bhagwan Mahavir Hospital" to listOf("Dr. Siddharth Malhotra", "Dr. Neetu Mishra", "Dr. Anurag Gupta"),
        "Dr. L H Hiranandani Hospital" to listOf("Dr. Amitabh Sharma", "Dr. Sunil Khurana", "Dr. Radha Pillai"),
        "Rajiv Gandhi Cancer Institute" to listOf("Dr. Pranav Chopra", "Dr. Sarika Joshi", "Dr. Devina Sharma"),
        "Sir Ganga Ram Hospital" to listOf("Dr. Vipul Tandon", "Dr. Ishita Verma", "Dr. Ganesh Nair"),
        "St. John's Medical College Hospital" to listOf("Dr. Rakesh Nambiar", "Dr. Tanya Sen", "Dr. Mukesh Iyer"),
        "Christian Medical College (CMC)" to listOf("Dr. Joseph Mathew", "Dr. Rina Ghosh", "Dr. Peter Fernandes"),
        "Government General Hospital Chennai" to listOf("Dr. Aarthi Ramesh", "Dr. Dinesh Prabhu", "Dr. Meenakshi Babu")
    )

    var selectedHospital by remember { mutableStateOf(hospitals[0]) }
    var selectedDoctor by remember { mutableStateOf(doctorsMap[selectedHospital]?.firstOrNull() ?: "") }
    var selectedDate by remember { mutableStateOf("Select Date") }
    var selectedTime by remember { mutableStateOf("Select Time") }

    LaunchedEffect(selectedHospital) {
        selectedDoctor = doctorsMap[selectedHospital]?.firstOrNull() ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Book an Appointment",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hospital Dropdown
                DropdownSelector("Select Hospital", hospitals, selectedHospital) { selectedHospital = it }

                Spacer(modifier = Modifier.height(16.dp))

                // Doctor Dropdown
                DropdownSelector("Select Doctor", doctorsMap[selectedHospital] ?: emptyList(), selectedDoctor) { selectedDoctor = it }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker
                PickerBox("Select Date", selectedDate) { showDatePicker(context) { selectedDate = it } }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Picker
                PickerBox("Select Time", selectedTime) { showTimePicker(context) { selectedTime = it } }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Appointment Button
                Button(
                    onClick = {
                        try {
                            if (selectedDate == "Select Date" || selectedTime == "Select Time") {
                                Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (selectedDoctor.isNullOrEmpty()) {
                                Toast.makeText(context, "Please select a doctor", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val isAvailable = dbHelper.isDoctorAvailable(selectedDoctor, selectedDate, selectedTime)

                            if (isAvailable) {
                                dbHelper.bookAppointment(email, selectedHospital, selectedDoctor, selectedDate, selectedTime)
                                Toast.makeText(context, "Appointment Booked Successfully!", Toast.LENGTH_LONG).show()
                            } else {
                                val nextAvailableTime = dbHelper.getNextAvailableTime(selectedDoctor, selectedDate)
                                if (nextAvailableTime != "No Available Slots") {
                                    dbHelper.bookAppointment(email, selectedHospital, selectedDoctor, selectedDate, nextAvailableTime)
                                    Toast.makeText(context, "Doctor not available. Appointment booked at $nextAvailableTime", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "No available slots for this doctor today", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A99D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Appointment", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
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

@Composable
fun DropdownSelector(label: String, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(16.dp)
                .animateContentSize()
        ) {
            Text(text = selectedItem, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(text = { Text(item) }, onClick = {
                        onItemSelected(item)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun PickerBox(label: String, selectedText: String, onClick: () -> Unit) {
    Column {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(16.dp)
                .animateContentSize()
        ) {
            Text(text = selectedText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
