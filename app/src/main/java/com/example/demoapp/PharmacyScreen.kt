package com.example.demoapp.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.demoapp.R


data class Medicine(
    val name: String,
    val price: String,
    val imageRes: Int,
    val category: String,
    val type: String
)

@Composable
fun PharmacyScreen(navController: NavController, context: Context, cartItems: MutableMap<Medicine, Int>) {
    val medicines = listOf(
        Medicine("Cold Relief Syrup", "₹15", R.drawable.cold_relief_syrup, "Cold", "Syrup"),
        Medicine("Vicks VapoRub", "₹20", R.drawable.vicks_vaporub, "Cold", "Ointment"),
        Medicine("Cetirizine", "₹7", R.drawable.cetirizine, "Cold", "Tablet"),
        Medicine("Sudafed", "₹18", R.drawable.sudafed, "Cold", "Tablet"),
        Medicine("Montelukast", "₹45", R.drawable.montelukast, "Cold", "Tablet"),
        Medicine("Coldex Syrup", "₹12", R.drawable.coldex_syrup, "Cold", "Syrup"),
        Medicine("Phenylephrine", "₹25", R.drawable.phenylephrine, "Cold", "Syrup"),
        Medicine("Dextromethorphan", "₹35", R.drawable.dextromethorphan, "Cold", "Syrup"),

        // Cough
        Medicine("Cough Syrup", "₹10", R.drawable.cough_syrup, "Cough", "Syrup"),
        Medicine("Benadryl", "₹18", R.drawable.cough_syrup, "Cough", "Syrup"),
        Medicine("Codeine Syrup", "₹50", R.drawable.codeine_syrup, "Cough", "Syrup"),
        Medicine("Robitussin", "₹25", R.drawable.robitussin, "Cough", "Syrup"),
        Medicine("Dextromethorphan", "₹30", R.drawable.dextromethorphan, "Cough", "Syrup"),
        Medicine("Guaifenesin", "₹22", R.drawable.guaifenesin, "Cough", "Syrup"),

        // Fever
        Medicine("Paracetamol", "₹5", R.drawable.paracetamol, "Fever", "Tablet"),
        Medicine("Dolo 650", "₹10", R.drawable.dolo_650, "Fever", "Tablet"),
        Medicine("Acetaminophen", "₹10", R.drawable.acetaminophen, "Fever", "Tablet"),
        Medicine("Nimesulide", "₹18", R.drawable.nimesulide, "Fever", "Tablet"),
        Medicine("Ibuprofen + Paracetamol", "₹22", R.drawable.ibuprofen_paracetamol, "Fever", "Tablet"),
        Medicine("Metamizole", "₹28", R.drawable.paracetamol, "Fever", "Tablet"),

        // Body Pain
        Medicine("Ibuprofen", "₹8", R.drawable.ibuprofen, "Body Pain", "Tablet"),
        Medicine("Aspirin", "₹6", R.drawable.aspirin, "Body Pain", "Tablet"),
        Medicine("Diclofenac Gel", "₹50", R.drawable.diclofenac_gel, "Body Pain", "Ointment"),
        Medicine("Antiseptic Lotion", "₹60", R.drawable.antiseptic_lotion, "Body Pain", "Ointment"),
        Medicine("Naproxen", "₹28", R.drawable.naproxen, "Body Pain", "Tablet"),
        Medicine("Capsaicin Cream", "₹50", R.drawable.capsaicin_cream, "Body Pain", "Ointment"),
        Medicine("Tramadol", "₹80", R.drawable.tramadol, "Body Pain", "Tablet"),

        // Infection
        Medicine("Amoxicillin", "₹25", R.drawable.amoxicillin, "Infection", "Tablet"),
        Medicine("Azithromycin", "₹30", R.drawable.azithromycin, "Infection", "Tablet"),
        Medicine("Erythromycin", "₹20", R.drawable.erythromycin, "Infection", "Tablet"),
        Medicine("Tetracycline", "₹22", R.drawable.tetracycline, "Infection", "Tablet"),
        Medicine("Metronidazole", "₹25", R.drawable.metronidazole, "Infection", "Tablet"),
        Medicine("Doxycycline", "₹35", R.drawable.doxycycline, "Infection", "Tablet"),
        Medicine("Ciprofloxacin", "₹40", R.drawable.ciprofloxacin, "Infection", "Tablet"),
        Medicine("Ketoconazole Cream", "₹45", R.drawable.ketoconazole_cream, "Infection", "Ointment"),
        Medicine("Levofloxacin", "₹50", R.drawable.levofloxacin, "Infection", "Tablet"),
        Medicine("Ofloxacin", "₹55", R.drawable.ofloxacin, "Infection", "Tablet"),
        Medicine("Hydrocortisone Cream", "₹30", R.drawable.hydrocortisone_cream, "Infection", "Ointment")
    )

    val categories = listOf("All", "Cold", "Cough", "Fever", "Body Pain", "Infection")
    val types = listOf("All", "Tablet", "Syrup", "Ointment")

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }

    // Filter Medicines
    val filteredMedicines = medicines.filter { medicine ->
        (selectedCategory == "All" || medicine.category == selectedCategory) &&
                (selectedType == "All" || medicine.type == selectedType)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("cartScreen") },
                backgroundColor = Color(0xFF1976D2)
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = Color.White)
            }
        },
        bottomBar = {
            BottomAppBar(backgroundColor = Color(0xFF1976D2)) {
                Button(
                    onClick = { navController.navigate("cartScreen") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text("Checkout (${cartItems.size})", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // **Category Filter**
            CategorySection(categories, selectedCategory) { selectedCategory = it }

            // **Filter Options**
            FilterSection(categories, types, selectedCategory, selectedType,
                onCategoryChange = { selectedCategory = it },
                onTypeChange = { selectedType = it }
            )

            // **Medicine List**
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filteredMedicines) { medicine ->
                    MedicineItem(medicine, cartItems)
                }
            }
        }
    }
}




@Composable
fun FilterSection(
    categories: List<String>,
    types: List<String>,
    selectedCategory: String,
    selectedType: String,
    onCategoryChange: (String) -> Unit,
    onTypeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text("Filter by Category", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Row {
            categories.forEach { category ->
                FilterChip(category, category == selectedCategory) { onCategoryChange(category) }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Filter by Type", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Row {
            types.forEach { type ->
                FilterChip(type, type == selectedType) { onTypeChange(type) }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        backgroundColor = if (isSelected) Color(0xFF1976D2) else Color.LightGray,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White
        )
    }
}

@Composable
fun CategorySection(categories: List<String>, selectedCategory: String, onCategorySelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(categories) { category ->
            CategoryChip(category, category == selectedCategory) { onCategorySelected(category) }
        }
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        backgroundColor = if (isSelected) Color(0xFF1976D2) else Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun MedicineItem(medicine: Medicine, cartItems: MutableMap<Medicine, Int>) {
    val quantity = cartItems[medicine] ?: 0

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = medicine.imageRes),
                contentDescription = medicine.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Text(
                text = medicine.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = medicine.price,
                fontSize = 14.sp,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (quantity > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    IconButton(onClick = {
                        if (quantity > 1) {
                            cartItems[medicine] = quantity - 1
                        } else {
                            cartItems.remove(medicine)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = "Decrease Quantity",
                            tint = Color.Gray
                        )
                    }

                    Text(
                        text = "$quantity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {
                        cartItems[medicine] = quantity + 1
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Increase Quantity",
                            tint = Color.Gray
                        )
                    }
                }
            } else {
                Button(
                    onClick = { cartItems[medicine] = 1 },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00A99D)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("ADD TO CART", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun CartScreen(navController: NavController, cartItems: MutableMap<Medicine, Int>) {
    // Calculate total amount
    val totalAmount = cartItems.entries.sumOf { (medicine, quantity) ->
        val price = medicine.price.replace("₹", "").toIntOrNull() ?: 0
        price * quantity
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart", color = Color.White, fontWeight = FontWeight.Bold) },
                backgroundColor = Color(0xFF1976D2),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (cartItems.isEmpty()) {
                Text("Your cart is empty!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            } else {
                LazyColumn {
                    items(cartItems.keys.toList()) { medicine ->
                        CartItem(medicine, cartItems)
                    }
                }

                Button(
                    onClick = {
                        // Navigate to PaymentScreen with the correct route
                        navController.navigate("payment/${totalAmount}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00A99D))
                ) {
                    Text("Proceed to Payment", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}





@Composable
fun CartItem(medicine: Medicine, cartItems: MutableMap<Medicine, Int>) {
    var quantity by remember { mutableStateOf(cartItems[medicine] ?: 1) }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medicine Image
            Image(
                painter = rememberAsyncImagePainter(medicine.imageRes),
                contentDescription = medicine.name,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = medicine.price,
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2)
                )
            }

            // Quantity Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (quantity > 1) {
                        quantity--
                        cartItems[medicine] = quantity
                    } else {
                        cartItems.remove(medicine) // Remove item if quantity is 0
                    }
                }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Remove", tint = Color.Gray)
                }

                Text(
                    text = "$quantity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    quantity++
                    cartItems[medicine] = quantity
                }) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add", tint = Color.Gray)
                }
            }
        }
    }
}

