package com.example.demoapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import kotlinx.coroutines.launch
@Composable
fun AmbulanceScreen(
    navController: NavController,
    dbHelper: DatabaseHelper,
    email: String,
    context: Context
) {
    var patientName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var streetName by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var emergency by remember { mutableStateOf(false) }
    var selectedHospital by remember { mutableStateOf<String?>(null) }
    var showHospitalDialog by remember { mutableStateOf(false) }
    var snackbarVisible by remember { mutableStateOf(false) }
    val snackbarMessage = "Ambulance booked successfully!"
    val coroutineScope = rememberCoroutineScope()

    // Predefined list of hospitals
    val hospitals = remember {
        mapOf(
            "Chennai" to mapOf(
                "Anna Nagar" to listOf(
                    "Apollo Hospitals, Anna Nagar", "Fortis Malar Hospital", "Billroth Hospitals",
                    "Frontier Lifeline Hospital", "Sundaram Medical Foundation", "KHM Hospital",
                    "Shree Balaji Hospital", "Soundarapandian Bone & Joint Hospital"
                ),
                "T. Nagar" to listOf(
                    "MIOT International", "Global Hospitals", "Cloudnine Hospital",
                    "Kauvery Hospital", "Be Well Hospital", "The Capstone Clinic",
                    "BSS Hospital", "Dr. Mehta’s Hospital"
                ),
                "Adyar" to listOf(
                    "Sri Ramachandra Medical Centre", "Kauvery Hospital, Adyar", "Apollo Spectra Hospital",
                    "Iswarya Fertility Centre", "Fortis Malar Hospital", "Astra Ortho & Spine Hospital",
                    "Billroth Hospitals, Adyar"
                ),
                "Velachery" to listOf(
                    "VS Hospitals", "SIMS Hospital", "Apollo Clinic", "PIMS Hospital",
                    "Dr. Kamakshi Memorial Hospital", "Prashanth Super Speciality Hospital",
                    "Annai Arul Hospital", "Be Well Hospital, Velachery"
                ),
                "Mylapore" to listOf(
                    "Cloudnine Hospital", "Billroth Hospital, Mylapore", "CSI Kalyani Multi-Speciality Hospital",
                    "Sundaram Medical Foundation", "St. Isabel’s Hospital", "Chellaram Hospitals",
                    "M. V. Hospital for Diabetes", "Apollo Children’s Hospital"
                ),
                "Porur" to listOf(
                    "Sri Ramachandra Medical Centre", "MIOT International", "Arokya Hospital",
                    "Kedar Hospital", "Venkateswara Hospital", "Sooriya Hospital",
                    "Apollo Specialty Hospital, Vanagaram"
                ),
                "Tambaram" to listOf(
                    "Hindu Mission Hospital", "Cosmo ENT Hospital", "Annai Arul Hospital",
                    "Be Well Hospital, Tambaram", "Nirmals Eye Hospital", "Sudar Hospital",
                    "Deepam Hospitals", "Rainbow Children's Hospital"
                ),
                "Guindy" to listOf(
                    "Apollo Hospitals, Guindy", "Fortis Malar Hospital", "Balaji Dental & Craniofacial Hospital",
                    "Kings Hospitals", "Be Well Hospital, Guindy", "St. Thomas Hospital", "SIMS Hospital"
                ),
                "Madipakkam" to listOf(
                    "Kamatchi Memorial Hospital", "Annai Theresa Hospital", "Swaram Hospital",
                    "Vasan Eye Care", "Apollo Clinic", "Be Well Hospital, Madipakkam"
                ),
                "Thoraipakkam" to listOf(
                    "Apollo Cradle & Children's Hospital", "Vijaya Hospital", "Kauvery Hospital, OMR",
                    "Gleneagles Global Health City", "Prashanth Hospital", "Sugan Hospital"
                ),
                "Perungudi" to listOf(
                    "Apollo Speciality Hospitals, OMR", "SIMS Hospital", "Kauvery Hospital, Perungudi",
                    "Sugan Hospital", "Vasan Eye Care", "Apollo White Dental"
                ),
                "Ambattur" to listOf(
                    "Sir Ivan Stedeford Hospital", "Sundaram Medical Foundation", "Aishwarya Hospitals",
                    "OMR Apollo Clinic", "Care 24 Hospital", "MGM Healthcare"
                ),
                "Padi" to listOf(
                    "Frontier Lifeline Hospital", "Apollo Hospitals", "Arun Hospital",
                    "Sooriya Hospital", "Be Well Hospital, Padi", "Raghavendra Hospital"
                ),
                "Thiruvanmiyur" to listOf(
                    "Apollo Clinic, Thiruvanmiyur", "Kauvery Hospital, Thiruvanmiyur",
                    "Aakash Hospital", "Dr. Kamakshi Memorial Hospital", "Fortis Malar Hospital"
                ),
                "Nungambakkam" to listOf(
                    "Apollo Hospitals, Greams Road", "Billroth Hospitals", "Dr. Mehta’s Hospital",
                    "Kauvery Hospital", "Cloudnine Hospital, Nungambakkam", "SIMS Hospital"
                ),
                "Kodambakkam" to listOf(
                    "SIMS Hospital", "Sundaram Medical Foundation", "Kauvery Hospital, Kodambakkam",
                    "Be Well Hospital", "Vijaya Hospital", "Dr. Mehta’s Hospital"
                ),
                "Ashok Nagar" to listOf(
                    "Vijaya Hospital", "Be Well Hospital", "Kauvery Hospital, Ashok Nagar",
                    "Apollo Clinic", "SIMS Hospital", "Sundaram Medical Foundation"
                ),
                "West Mambalam" to listOf(
                    "Sundaram Medical Foundation", "Kauvery Hospital, West Mambalam",
                    "Vijaya Hospital", "Apollo Clinic", "Billroth Hospitals"
                ),
                "Chromepet" to listOf(
                    "Deepam Hospitals", "Sudharsanam Hospital", "Parvathy Hospital",
                    "Annai Arul Hospital", "Dr. Rela Institute & Medical Centre"
                ),
                "Pallavaram" to listOf(
                    "Deepam Hospitals", "Annai Arul Hospital", "Kamatchi Memorial Hospital",
                    "Cloudnine Hospital", "Sudharsanam Hospital"
                ),
                "Vadapalani" to listOf(
                    "SIMS Hospital", "Vijaya Hospital", "Kauvery Hospital, Vadapalani",
                    "Apollo Clinic", "Sundaram Medical Foundation"
                ),
                "Perambur" to listOf(
                    "Sundaram Medical Foundation", "Apollo Clinic, Perambur",
                    "Kauvery Hospital, Perambur", "Dr. Mehta’s Hospital", "Sri Balaji Hospital"
                ),
                "Aminjikarai" to listOf(
                    "Billroth Hospitals", "Apollo Clinic", "Be Well Hospital, Aminjikarai",
                    "Sundaram Medical Foundation", "Kauvery Hospital"
                ),
                "Royapettah" to listOf(
                    "Kauvery Hospital, Royapettah", "Apollo Spectra Hospitals", "Sundaram Medical Foundation",
                    "Cloudnine Hospital", "Billroth Hospitals"
                ),
                "Teynampet" to listOf(
                    "Apollo Hospitals, Teynampet", "SIMS Hospital", "Cloudnine Hospital",
                    "Kauvery Hospital, Teynampet", "Be Well Hospital"
                ),
                "Egmore" to listOf(
                    "Apollo Children's Hospital", "Sundaram Medical Foundation", "Billroth Hospitals",
                    "SIMS Hospital", "Kauvery Hospital, Egmore"
                ),
                "Koyambedu" to listOf(
                    "Apollo Clinic, Koyambedu", "Be Well Hospital", "Dr. Mehta’s Hospital",
                    "Sundaram Medical Foundation", "Kauvery Hospital, Koyambedu"
                ),
                "Poonamallee" to listOf(
                    "Be Well Hospital, Poonamallee", "Apollo Clinic", "Vijaya Hospital",
                    "Sundaram Medical Foundation", "Kauvery Hospital"
                ),
                "Manali" to listOf(
                    "Be Well Hospital, Manali", "Sri Balaji Hospital", "Apollo Clinic",
                    "Sundaram Medical Foundation", "Kauvery Hospital, Manali"
                ),
                "Medavakkam" to listOf(
                    "Gleneagles Global Health City", "Kamatchi Memorial Hospital",
                    "Cloudnine Hospital", "Kauvery Hospital", "Apollo Clinic"
                )
            ),
            "Bangalore" to mapOf(
                "MG Road" to listOf(
                    "Manipal Hospitals, MG Road", "Fortis Hospital, MG Road", "Apollo Spectra Hospital",
                    "HOSMAT Hospital", "Sita Bhateja Speciality Hospital", "St. Philomena’s Hospital"
                ),
                "Koramangala" to listOf(
                    "Apollo Spectra Hospital, Koramangala", "Cloudnine Hospital", "St. John’s Medical College Hospital",
                    "Beams Multi-speciality Hospital", "Pristyn Care", "Marvel Multispeciality Hospital"
                ),
                "Indiranagar" to listOf(
                    "Manipal Hospital, Old Airport Road", "Cloudnine Hospital", "Columbia Asia Hospital",
                    "Apollo Clinic", "Sakra Premium Clinic", "CMH Hospital", "Chinmaya Mission Hospital"
                ),
                "Whitefield" to listOf(
                    "Manipal Hospital, Whitefield", "Sakra World Hospital", "Vydehi Institute of Medical Sciences",
                    "Cloudnine Hospital, Whitefield", "Narayana Multispeciality Hospital",
                    "Columbia Asia Hospital, Whitefield"
                ),
                "Marathahalli" to listOf(
                    "Apollo Cradle & Children's Hospital", "Sakra World Hospital", "Vydehi Hospital",
                    "Rainbow Children's Hospital", "Jeevika Hospital", "Columbia Asia Hospital"
                ),
                "Jayanagar" to listOf(
                    "Apollo Speciality Hospital, Jayanagar", "Cloudnine Hospital, Jayanagar",
                    "Sagar Hospitals", "Jayadeva Institute of Cardiology", "Agadi Hospital",
                    "BGS Global Hospital"
                ),
                "JP Nagar" to listOf(
                    "Fortis Hospital, JP Nagar", "Cloudnine Hospital", "Apollo Hospitals, Bannerghatta Road",
                    "Sagar Hospitals", "Spandana Hospital", "Rajshekar Multi Speciality Hospital"
                ),
                "Electronic City" to listOf(
                    "Narayana Institute of Cardiac Sciences", "Vimalalaya Hospital",
                    "Cloudnine Hospital, Electronic City", "Sparsh Hospital", "Narayana Health City"
                ),
                "Hebbal" to listOf(
                    "Manipal Hospital, Hebbal", "Baptist Hospital", "Columbia Asia Hospital",
                    "Motherhood Hospital", "Cloudnine Hospital", "Aster CMI Hospital"
                ),
                "Yeshwanthpur" to listOf(
                    "People Tree Hospitals", "Columbia Asia Referral Hospital, Yeshwanthpur",
                    "Sparsh Hospital", "Fortis Hospital, Rajajinagar", "Ramaiah Memorial Hospital"
                ),
                "Rajajinagar" to listOf(
                    "Fortis Hospital, Rajajinagar", "Suguna Hospital", "Narayana Nethralaya",
                    "Columbia Asia Hospital", "Sanjeevini Hospital", "Vikram Hospital"
                ),
                "Banashankari" to listOf(
                    "Sagar Hospitals", "BGS Gleneagles Global Hospitals", "Sri Balaji Hospital",
                    "Cloudnine Hospital, Banashankari", "Bangalore Hospital", "Vasan Eye Care"
                ),
                "Malleshwaram" to listOf(
                    "Columbia Asia Hospital, Malleshwaram", "Apollo Clinic, Malleshwaram",
                    "Sparsh Hospital", "Suguna Hospital", "Manipal Northside Hospital"
                ),
                "RT Nagar" to listOf(
                    "Baptist Hospital", "Manipal Hospital", "Cloudnine Hospital",
                    "Nethradhama Superspeciality Eye Hospital", "Chinmaya Mission Hospital"
                ),
                "Kengeri" to listOf(
                    "BGS Global Hospital", "ESIC Model Hospital", "Unity Hospital",
                    "Sri Rajiv Gandhi Hospital", "Mallige Hospital", "Sparsh Hospital"
                ),
                "Sarjapur Road" to listOf(
                    "Motherhood Hospital, Sarjapur", "Cloudnine Hospital, Sarjapur",
                    "Columbia Asia Hospital", "Apollo Clinic", "Sakra World Hospital"
                ),
                "Bellandur" to listOf(
                    "Sakra World Hospital", "Columbia Asia Hospital", "Cloudnine Hospital",
                    "Motherhood Hospital", "Vydehi Hospital", "Manipal Hospitals, Whitefield"
                ),
                "Hennur" to listOf(
                    "Regal Hospital", "Motherhood Hospital", "Manipal Northside Hospital",
                    "Columbia Asia Hospital", "Aster CMI Hospital", "Baptist Hospital"
                ),
                "Ulsoor" to listOf(
                    "Manipal Hospital, Ulsoor", "Cloudnine Hospital", "Hosmat Hospital",
                    "Sita Bhateja Hospital", "Columbia Asia Hospital", "Vikram Hospital"
                ),
                "KR Puram" to listOf(
                    "Sri Lakshmi Super Speciality Hospital", "Narayana Multispeciality Hospital",
                    "Vydehi Hospital", "Manipal Hospital", "Columbia Asia Hospital"
                ),
                "Kalyan Nagar" to listOf(
                    "Motherhood Hospital", "Cloudnine Hospital", "Regal Hospital",
                    "Columbia Asia Hospital", "Manipal Northside Hospital"
                ),
                "Hosur Road" to listOf(
                    "Narayana Health City", "Fortis Hospital, Bannerghatta",
                    "Apollo Speciality Hospital", "Cloudnine Hospital", "Aster CMI Hospital"
                ),
                "Majestic" to listOf(
                    "Victoria Hospital", "St. Martha’s Hospital", "Mallya Hospital",
                    "Sparsh Hospital", "Bowring & Lady Curzon Hospital", "Manipal Hospital"
                ),
                "Shivajinagar" to listOf(
                    "Bowring & Lady Curzon Hospital", "Hosmat Hospital", "Cloudnine Hospital",
                    "Fortis Hospital, Cunningham Road", "St. Martha’s Hospital"
                ),
                "Bommanahalli" to listOf(
                    "Narayana Multispeciality Hospital", "Prashanth Hospital",
                    "Cloudnine Hospital", "Apollo Clinic", "BGS Gleneagles Global Hospitals"
                ),
                "Vijayanagar" to listOf(
                    "Sanjeevini Hospital", "Manipal Hospitals", "Vikram Hospital",
                    "Cloudnine Hospital", "Sparsh Hospital"
                ),
                "Chandapura" to listOf(
                    "Vimalalaya Hospital", "Narayana Health City", "Cloudnine Hospital",
                    "Sri Krishna Hospital", "Apollo Clinic"
                ),
                "HSR Layout" to listOf(
                    "Narayana Multispeciality Hospital", "Cloudnine Hospital", "Motherhood Hospital",
                    "Columbia Asia Hospital", "Sakra World Hospital"
                ),
                "Jakkur" to listOf(
                    "Columbia Asia Hospital", "Baptist Hospital", "Motherhood Hospital",
                    "Aster CMI Hospital", "Cloudnine Hospital"
                ),
                "Horamavu" to listOf(
                    "Cloudnine Hospital", "Motherhood Hospital", "Manipal Hospital",
                    "Columbia Asia Hospital", "Narayana Multispeciality Hospital"
                ),
                "Yelahanka" to listOf(
                    "Aster CMI Hospital", "Motherhood Hospital", "Cloudnine Hospital",
                    "Columbia Asia Hospital", "Manipal Hospital"
                )
            )
        )
    }

    val availableCities = hospitals.keys.toList()
    val availableAreas = hospitals[city]?.keys?.toList() ?: emptyList()
    val nearbyHospitals = hospitals[city]?.get(area) ?: emptyList()

    val handleSubmit = {
        if (patientName.isNotEmpty() && contactNumber.isNotEmpty() && streetName.isNotEmpty() &&
            area.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && selectedHospital != null
        ) {
            snackbarVisible = true
            val result = dbHelper.insertAmbulanceDetails(
                patientName,
                contactNumber,
                streetName,
                area,
                city,
                state,
                selectedHospital ?: "",
                symptoms,
                emergency
            )
            if (result) {
                Toast.makeText(context, "Ambulance booked successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to book ambulance!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            Footer(navController, email, dbHelper, context)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Ambulance Booking",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007BFF)
                    )

                    CustomTextField("Patient Name", patientName) { patientName = it }
                    CustomTextField("Contact Number", contactNumber, keyboardType = KeyboardType.Phone) { contactNumber = it }
                    CustomTextField("Street Name", streetName) { streetName = it }
                    val cityToStateMap = mapOf(
                        "Chennai" to "Tamil Nadu",
                        "Bangalore" to "Karnataka",
                        "Mumbai" to "Maharashtra",
                        "Delhi" to "Delhi"
                    )

                    val availableCities = cityToStateMap.keys.toList()

                    DropdownField("City", city, availableCities) { selectedCity ->
                        city = selectedCity
                        state = cityToStateMap[selectedCity] ?: ""
                    }
                    CustomTextField("State", state) { }
                    DropdownField("Area", area, availableAreas) { area = it }



                    Button(
                        onClick = { showHospitalDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF007BFF)),
                        enabled = area.isNotEmpty() && city.isNotEmpty()
                    ) {
                        Text("Select Nearby Hospital", fontSize = 16.sp, color = Color.White)
                    }

                    selectedHospital?.let {
                        Text(
                            "Selected Hospital: $it",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    CustomTextField("Symptoms (Optional)", symptoms) { symptoms = it }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = emergency,
                            onCheckedChange = { emergency = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF007BFF),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            "Emergency Case?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007BFF)
                        )
                    }

                    Button(
                        onClick = handleSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF007BFF))
                    ) {
                        Text("Submit", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }

        if (showHospitalDialog) {
            HospitalSelectionDialog(
                showDialog = showHospitalDialog,
                hospitals = nearbyHospitals,
                onHospitalSelected = { hospital ->
                    selectedHospital = hospital
                    showHospitalDialog = false
                },
                onDismiss = { showHospitalDialog = false }
            )
        }

        if (snackbarVisible) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { snackbarVisible = false }) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            ) {
                Text(snackbarMessage)
            }
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions.Default
    )
}


@Composable
fun DropdownField(label: String, selectedValue: String, options: List<String>, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }, // Ensure clicking on text field opens dropdown
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}





@Composable
fun HospitalSelectionDialog(
    showDialog: Boolean,
    hospitals: List<String>,
    onHospitalSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "General", "Specialty", "Emergency")

    // Filter hospitals based on search input
    val filteredHospitals = hospitals.filter {
        it.contains(searchQuery, ignoreCase = true) || selectedCategory == "All"
    }

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select a Hospital",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007BFF)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Hospital") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Category Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        categories.forEach { category ->
                            Button(
                                onClick = { selectedCategory = category },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (selectedCategory == category) Color(0xFF007BFF) else Color.LightGray
                                ),
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            ) {
                                Text(category, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Hospital List
                    if (filteredHospitals.isEmpty()) {
                        Text("No hospitals found.", fontSize = 16.sp, color = Color.Gray)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(hospitals) { hospital -> // ✅ CORRECT
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onHospitalSelected(hospital) },
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocalHospital, contentDescription = "Hospital Icon", tint = Color(0xFF007BFF))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(hospital, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Text("⭐ 4.5 Rating", fontSize = 14.sp, color = Color.Gray) // Placeholder rating
                                        }
                                    }
                                }
                            }
                        }

                    }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Cancel Button
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            }
        }
}

