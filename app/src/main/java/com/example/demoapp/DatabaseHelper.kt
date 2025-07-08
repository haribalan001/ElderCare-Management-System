    package com.example.demoapp

    import android.content.ContentValues
    import android.content.Context
    import android.database.Cursor
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteOpenHelper
    import android.util.Log

    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "user_database"
            private const val DATABASE_VERSION = 9  // Incremented version to reflect changes

            // Ambulance Booking Table and Columns (New)
            const val TABLE_AMBULANCE = "ambulance_bookings"
            const val COLUMN_AMBULANCE_ID = "id"
            const val COLUMN_AMBULANCE_PATIENT_NAME = "patient_name"
            const val COLUMN_AMBULANCE_ADDRESS = "address"
            const val COLUMN_AMBULANCE_EMERGENCY = "emergency" // 0 for no, 1 for yes

            // Add these constants with your other table definitions
            private const val TABLE_SETTINGS = "user_settings"
            private const val COLUMN_SETTINGS_EMAIL = "email"  // Same as COLUMN_EMAIL from users table
            private const val COLUMN_DARK_MODE = "dark_mode"
            private const val COLUMN_FONT_SIZE = "font_size"
            private const val COLUMN_NOTIFICATIONS = "notifications"


            // User table columns
            const val TABLE_USER = "users"
            const val COLUMN_ID = "id"
            const val COLUMN_NAME = "name"
            const val COLUMN_EMAIL = "email"
            const val COLUMN_PASSWORD = "password"
            const val COLUMN_MOBILE = "mobile"
            const val COLUMN_DOB = "dob"
            const val COLUMN_PROFILE_IMAGE_URI = "profile_image_uri"


            // Patient table columns
            const val TABLE_PATIENTS = "patients"
            const val COLUMN_PATIENT_ID = "id"
            const val COLUMN_GUARDIAN_NAME = "guardian_name"
            const val COLUMN_GUARDIAN_CONTACT = "guardian_contact"
            const val COLUMN_ADDRESS = "address"
            const val COLUMN_PATIENT_EMAIL = "email"
            const val COLUMN_PATIENT_PHONE = "phone"
            const val COLUMN_PATIENT_PASSWORD = "password"
            const val COLUMN_PRESCRIPTION_URI = "prescription_uri"


            private const val TABLE_APPOINTMENTS = "appointments"
            private const val COLUMN_APPOINTMENT_ID = "id"
            private const val COLUMN_USER_EMAIL = "user_email"
            private const val COLUMN_HOSPITAL = "hospital"
            private const val COLUMN_DOCTOR = "doctor"
            private const val COLUMN_DATE = "date"
            private const val COLUMN_TIME = "time"

            // Elder care table columns
            const val TABLE_ELDER_CARE = "elder_care"
            const val COLUMN_ELDER_CARE_ID = "id"
            const val COLUMN_PATIENT_NAME = "patient_name"
            const val COLUMN_GENDER = "gender"
            const val COLUMN_SHIFT_START = "shift_start"
            const val COLUMN_SHIFT_END = "shift_end"



            // Elder care table columns (new)
            const val TABLE_CAREGIVERS = "caregivers"
            const val COLUMN_CAREGIVER_ID = "id"
            const val COLUMN_CAREGIVER_NAME = "name"
            const val COLUMN_CAREGIVER_GENDER = "gender"
            const val COLUMN_CAREGIVER_AVAILABLE =
                "is_available" // 1 for available, 0 for not available
            const val COLUMN_CAREGIVER_SHIFT_START = "shift_start"
            const val COLUMN_CAREGIVER_SHIFT_END = "shift_end"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            try {
                // Create User Table
                db?.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS $TABLE_USER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_MOBILE TEXT,
                $COLUMN_PROFILE_IMAGE_URI TEXT
            )
            """
                )

                // Create Elder Care Table (✅ FIXED)
                // Ensure `patient_name` is in the CREATE TABLE statement
                db?.execSQL(
                    """
    CREATE TABLE IF NOT EXISTS $TABLE_ELDER_CARE (
        $COLUMN_ELDER_CARE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_PATIENT_NAME TEXT,  -- ✅ This line ensures the column exists
        $COLUMN_GENDER TEXT,
        $COLUMN_SHIFT_START TEXT,
        $COLUMN_SHIFT_END TEXT
    )
    """
                )

                // Add this with your other table creation statements
                db?.execSQL("""
    CREATE TABLE IF NOT EXISTS $TABLE_SETTINGS (
        $COLUMN_SETTINGS_EMAIL TEXT PRIMARY KEY,
        $COLUMN_DARK_MODE INTEGER DEFAULT 0,
        $COLUMN_FONT_SIZE REAL DEFAULT 1.0,
        $COLUMN_NOTIFICATIONS INTEGER DEFAULT 1,
        FOREIGN KEY ($COLUMN_SETTINGS_EMAIL) REFERENCES $TABLE_USER($COLUMN_EMAIL)
    )
""".trimIndent())


                // Create Caregivers Table
                db?.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS $TABLE_CAREGIVERS (
                $COLUMN_CAREGIVER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CAREGIVER_NAME TEXT,
                $COLUMN_CAREGIVER_GENDER TEXT,
                $COLUMN_CAREGIVER_AVAILABLE INTEGER DEFAULT 1,
                $COLUMN_CAREGIVER_SHIFT_START TEXT,
                $COLUMN_CAREGIVER_SHIFT_END TEXT
            )
            """
                )

                // Create Patients Table
                db?.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS $TABLE_PATIENTS (
                $COLUMN_PATIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_GUARDIAN_NAME TEXT,
                $COLUMN_GUARDIAN_CONTACT TEXT,
                $COLUMN_ADDRESS TEXT,
                $COLUMN_PATIENT_EMAIL TEXT UNIQUE,
                $COLUMN_PATIENT_PHONE TEXT,
                $COLUMN_PATIENT_PASSWORD TEXT,
                $COLUMN_PRESCRIPTION_URI TEXT
            )
            """
                )

                // Create Ambulance Booking Table
                db?.execSQL(
                    """
    CREATE TABLE IF NOT EXISTS $TABLE_AMBULANCE (
        $COLUMN_AMBULANCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_AMBULANCE_PATIENT_NAME TEXT,
        contact_number TEXT,  
        street_name TEXT,
        area TEXT,
        city TEXT,
        state TEXT,
        selected_hospital TEXT,
        symptoms TEXT,
        $COLUMN_AMBULANCE_EMERGENCY INTEGER DEFAULT 0
    )
    """
                )
                val createAppointmentsTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_APPOINTMENTS (
                $COLUMN_APPOINTMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_EMAIL TEXT,
                $COLUMN_HOSPITAL TEXT,
                $COLUMN_DOCTOR TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT
            )
        """.trimIndent()
                db?.execSQL(createAppointmentsTable)

                prePopulateCaregivers(db) // ✅ Populate caregivers
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error creating tables", e)
            }
        }



        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            // Add missing column to elder_care table (if needed)
            if (!columnExists(db, TABLE_ELDER_CARE, COLUMN_PATIENT_NAME)) {
                db?.execSQL("ALTER TABLE $TABLE_ELDER_CARE ADD COLUMN $COLUMN_PATIENT_NAME TEXT")
            }

            // Drop and recreate tables that have major changes
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_AMBULANCE")

            // Recreate tables
            onCreate(db)
        }
        private fun columnExists(db: SQLiteDatabase?, tableName: String, columnName: String): Boolean {
            val cursor = db?.rawQuery("PRAGMA table_info($tableName)", null)
            cursor?.use {
                while (it.moveToNext()) {
                    val existingColumnName = it.getString(it.getColumnIndexOrThrow("name"))
                    if (existingColumnName == columnName) {
                        return true
                    }
                }
            }
            return false
        }


        fun prePopulateCaregivers(db: SQLiteDatabase?) {
            val caregivers = listOf(
                Caregiver("Lakshmi", "Female", "09:00", "17:00",true),
                Caregiver("Harish", "Male", "08:00", "16:00",true),
                Caregiver("Janarth", "Male", "10:00", "18:00",true),
                Caregiver("Jegan", "Male", "07:00", "15:00",true),
                Caregiver("Ram", "Male", "11:00", "19:00",true),
                Caregiver("Sarah", "Female", "09:30", "17:30",true),
                Caregiver("Kavya", "Female", "08:30", "16:30",true),
                Caregiver("Ragavi", "Female", "10:30", "18:30",true),
                Caregiver("Hariharan", "Male", "07:30", "15:30",true),
                Caregiver("Preethi", "Female", "11:30", "19:30",true),
                Caregiver("Vikram", "Male", "09:00", "17:00",true),
                Caregiver("Reena", "Female", "08:00", "16:00",true),
                Caregiver("Velu", "Male", "10:00", "18:00",true),
                Caregiver("Kanmani", "Female", "07:00", "15:00",true),
                Caregiver("Kavin", "Male", "11:00", "19:00",true)
            )

            caregivers.forEach { caregiver ->
                val contentValues = ContentValues().apply {
                    put(COLUMN_CAREGIVER_NAME, caregiver.name)
                    put(COLUMN_CAREGIVER_GENDER, caregiver.gender)
                    put(COLUMN_CAREGIVER_SHIFT_START, caregiver.shiftStart)
                    put(COLUMN_CAREGIVER_SHIFT_END, caregiver.shiftEnd)
                    put(COLUMN_CAREGIVER_AVAILABLE, 1) // Default to available
                }
                db?.insert(TABLE_CAREGIVERS, null, contentValues)
            }
        }

        fun insertElderCareDetails(patientName: String, gender: String, shiftStart: String, shiftEnd: String): Boolean {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(COLUMN_PATIENT_NAME, patientName)
                put(COLUMN_GENDER, gender)
                put(COLUMN_SHIFT_START, shiftStart)
                put(COLUMN_SHIFT_END, shiftEnd)
            }

            return try {
                val result = db.insert(TABLE_ELDER_CARE, null, contentValues)
                if (result == -1L) {
                    Log.e("DatabaseHelper", "❌ Failed to insert elder care details")
                } else {
                    Log.d("DatabaseHelper", "✅ Elder care details inserted successfully")
                }
                result != -1L
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "❌ Error inserting elder care details: ${e.message}", e)
                false
            } finally {
                db.close()
            }
        }


        // Method to validate user credentials
        fun validateUserCredentials(email: String, password: String): Boolean {
            val db = readableDatabase
            var cursor: Cursor? = null

            return try {
                cursor = db.query(
                    TABLE_USER,
                    arrayOf(COLUMN_ID),
                    "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
                    arrayOf(email, password),
                    null,
                    null,
                    null
                )

                val isValid = cursor != null && cursor.count > 0
                if (!isValid) {
                    Log.e("DatabaseHelper", "❌ Invalid email or password")
                }
                isValid
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "❌ Error validating user: ${e.message}", e)
                false
            } finally {
                cursor?.close()
                db.close()
            }
        }


        // Method to insert user details
        fun insertUserDetails(
            email: String,
            password: String,
            mobile: String,
            name: String
        ): Boolean {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(COLUMN_EMAIL, email)
                put(COLUMN_PASSWORD, password)
                put(COLUMN_MOBILE, mobile)
                put(COLUMN_NAME, name)  // Insert user's name
            }
            return try {
                val result = db.insert(TABLE_USER, null, contentValues)
                result != -1L // Return true if insertion is successful
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error inserting user details", e)
                false
            } finally {
                db.close()
            }
        }

        // Method to fetch user details by email
        fun getUserProfile(email: String): User? {
            val db = readableDatabase
            val cursor: Cursor? = db.query(
                TABLE_USER, null, "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null
            )

            return try {
                if (cursor != null && cursor.moveToFirst()) {
                    User(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                        mobile = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE)),
                        profileImageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE_URI))
                    )
                } else null
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error fetching user details", e)
                null
            } finally {
                cursor?.close()
                db.close()
            }
        }


        // Method to update user profile details including DOB
        fun updateUserProfile(
            email: String,
            name: String,
            mobile: String,
            dob: String?,  // Added DOB
            profileImageUri: String?
        ): Boolean {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_MOBILE, mobile)
                dob?.let { put(COLUMN_DOB, it) }  // Update DOB if provided
                profileImageUri?.let { put(COLUMN_PROFILE_IMAGE_URI, it) }
            }
            return try {
                val rowsUpdated = db.update(
                    TABLE_USER,
                    contentValues,
                    "$COLUMN_EMAIL = ?",
                    arrayOf(email)
                )
                rowsUpdated > 0  // Return true if at least one row was updated
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error updating user profile", e)
                false
            } finally {
                db.close()
            }
        }


        // Method to fetch patient details by ID
        fun getPatientDetailsById(patientId: Int): Patient? {
            val db = readableDatabase
            val cursor: Cursor = db.query(
                TABLE_PATIENTS,
                null,
                "$COLUMN_PATIENT_ID = ?",
                arrayOf(patientId.toString()),
                null,
                null,
                null
            )

            return try {
                if (cursor.moveToFirst()) {
                    Patient(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ID)),
                        guardianName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_GUARDIAN_NAME
                            )
                        ),
                        guardianContact = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_GUARDIAN_CONTACT
                            )
                        ),
                        patientAddress = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ADDRESS
                            )
                        ),
                        patientEmail = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_PATIENT_EMAIL
                            )
                        ),
                        patientPhone = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_PATIENT_PHONE
                            )
                        ),
                        patientPassword = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_PATIENT_PASSWORD
                            )
                        ),
                        prescriptionUri = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_PRESCRIPTION_URI
                            )
                        )
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error fetching patient details", e)
                null
            } finally {
                cursor.close()
                db.close()
            }
        }

        // Method to get an available caregiver based on elder's gender preference and shift availability
        fun getAvailableCaregiver(preferredGender: String, assignAny: Boolean = false): Caregiver? {
            val db = readableDatabase

            // Query for an AVAILABLE caregiver of the preferred gender
            val queryAvailable = """
        SELECT * FROM $TABLE_CAREGIVERS 
        WHERE $COLUMN_CAREGIVER_GENDER = ? 
        AND $COLUMN_CAREGIVER_AVAILABLE = 1
        ORDER BY RANDOM() LIMIT 1
    """

            val cursor = db.rawQuery(queryAvailable, arrayOf(preferredGender))
            var caregiver: Caregiver? = null

            if (cursor.moveToFirst()) {
                caregiver = Caregiver(
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAREGIVER_NAME)),
                    gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAREGIVER_GENDER)),
                    shiftStart = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAREGIVER_SHIFT_START)),
                    shiftEnd = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAREGIVER_SHIFT_END)),
                    available = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAREGIVER_AVAILABLE)) == 1
                )
                Log.d("DatabaseHelper", "✅ Available caregiver found: ${caregiver.name}")
            }

            cursor.close()

            // If no caregiver is available AND user allowed "assign any", pick a random one
            if (caregiver == null && assignAny) {
                val queryRandom = """
            SELECT * FROM $TABLE_CAREGIVERS 
            ORDER BY RANDOM() LIMIT 1
        """
                val cursorRandom = db.rawQuery(queryRandom, null)

                if (cursorRandom.moveToFirst()) {
                    caregiver = Caregiver(
                        name = cursorRandom.getString(cursorRandom.getColumnIndexOrThrow(COLUMN_CAREGIVER_NAME)),
                        gender = cursorRandom.getString(cursorRandom.getColumnIndexOrThrow(COLUMN_CAREGIVER_GENDER)),
                        shiftStart = cursorRandom.getString(cursorRandom.getColumnIndexOrThrow(COLUMN_CAREGIVER_SHIFT_START)),
                        shiftEnd = cursorRandom.getString(cursorRandom.getColumnIndexOrThrow(COLUMN_CAREGIVER_SHIFT_END)),
                        available = cursorRandom.getInt(cursorRandom.getColumnIndexOrThrow(COLUMN_CAREGIVER_AVAILABLE)) == 1
                    )
                    Log.d("DatabaseHelper", "✅ Assigned a random caregiver: ${caregiver.name}")
                } else {
                    Log.e("DatabaseHelper", "❌ No caregivers available at all!")
                }

                cursorRandom.close()
            }

            db.close()
            return caregiver
        }


        // Settings functions
        fun saveUserSettings(email: String, darkMode: Boolean, fontSize: Float, notifications: Boolean): Boolean {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_SETTINGS_EMAIL, email)
                put(COLUMN_DARK_MODE, if (darkMode) 1 else 0)
                put(COLUMN_FONT_SIZE, fontSize)
                put(COLUMN_NOTIFICATIONS, if (notifications) 1 else 0)
            }

            return try {
                db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1L
            } finally {
                db.close()
            }
        }

        fun getUserSettings(email: String): UserSettings? {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_SETTINGS,
                arrayOf(COLUMN_DARK_MODE, COLUMN_FONT_SIZE, COLUMN_NOTIFICATIONS),
                "$COLUMN_SETTINGS_EMAIL = ?",
                arrayOf(email),
                null, null, null
            )

            return try {
                if (cursor.moveToFirst()) {
                    UserSettings(
                        darkMode = cursor.getInt(0) == 1,
                        fontSize = cursor.getFloat(1),
                        notifications = cursor.getInt(2) == 1
                    )
                } else {
                    null
                }
            } finally {
                cursor.close()
                db.close()
            }
        }

        fun logoutUser(email: String): Boolean {
            // Implement any necessary logout cleanup
            return true
        }



        // Method to mark a caregiver as unavailable
        fun markCaregiverAsUnavailable(caregiverId: Long): Boolean {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(COLUMN_CAREGIVER_AVAILABLE, 0)
            }

            return try {
                val rowsUpdated = db.update(
                    TABLE_CAREGIVERS,
                    contentValues,
                    "$COLUMN_CAREGIVER_ID = ?",
                    arrayOf(caregiverId.toString())
                )
                rowsUpdated > 0
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error marking caregiver as unavailable", e)
                false
            } finally {
                db.close()
            }
        }


        // Add this function in your DatabaseHelper class
        // ✅ Check if a doctor is available at a specific time
        fun isDoctorAvailable(doctor: String, date: String, time: String): Boolean {
            val db = readableDatabase
            val query = """
        SELECT 1 FROM $TABLE_APPOINTMENTS 
        WHERE $COLUMN_DOCTOR = ? AND $COLUMN_DATE = ? AND $COLUMN_TIME = ? 
        LIMIT 1
    """
            db.rawQuery(query, arrayOf(doctor, date, time)).use { cursor ->
                val isAvailable = cursor.count == 0  // If count is 0, doctor is available
                db.close()
                return isAvailable
            }
        }

        // ✅ Get the next available time for a doctor
        fun getNextAvailableTime(doctor: String, date: String): String {
            val db = readableDatabase
            val query = """
        SELECT $COLUMN_TIME FROM $TABLE_APPOINTMENTS 
        WHERE $COLUMN_DOCTOR = ? AND $COLUMN_DATE = ? 
        ORDER BY $COLUMN_TIME ASC
    """
            val cursor = db.rawQuery(query, arrayOf(doctor, date))

            val bookedTimes = mutableListOf<String>()
            cursor.use {
                while (it.moveToNext()) {
                    bookedTimes.add(it.getString(0))
                }
            }
            db.close()

            // Generate available time slots (e.g., 9 AM - 6 PM, every 30 mins)
            val possibleTimes = generateTimeSlots()

            // Find the first available slot
            for (timeSlot in possibleTimes) {
                if (timeSlot !in bookedTimes) {
                    return timeSlot
                }
            }
            return "No Available Slots"
        }

        // ✅ Generate 30-minute time slots from 9 AM to 6 PM
        private fun generateTimeSlots(): List<String> {
            val timeSlots = mutableListOf<String>()
            for (hour in 9..17) { // 9 AM to 5 PM
                timeSlots.add(String.format("%02d:00 %s", if (hour == 12) 12 else if (hour < 12) hour else hour - 12, if (hour < 12) "AM" else "PM"))
                timeSlots.add(String.format("%02d:30 %s", if (hour == 12) 12 else if (hour < 12) hour else hour - 12, if (hour < 12) "AM" else "PM"))
            }
            return timeSlots
        }

        // ✅ Book an appointment
        fun bookAppointment(email: String, hospital: String, doctor: String, date: String, time: String): Boolean {
            val db = writableDatabase

            // 🔍 Check if an appointment already exists
            val query = """
        SELECT 1 FROM $TABLE_APPOINTMENTS 
        WHERE $COLUMN_DOCTOR = ? AND $COLUMN_DATE = ? AND $COLUMN_TIME = ? 
        LIMIT 1
    """
            db.rawQuery(query, arrayOf(doctor, date, time)).use { cursor ->
                if (cursor.count > 0) {
                    Log.e("DatabaseHelper", "Appointment already exists for $doctor on $date at $time")
                    return false  // ❌ Appointment already booked
                }
            }

            // ✅ No existing appointment, proceed with booking
            val values = ContentValues().apply {
                put(COLUMN_USER_EMAIL, email)
                put(COLUMN_HOSPITAL, hospital)
                put(COLUMN_DOCTOR, doctor)
                put(COLUMN_DATE, date)
                put(COLUMN_TIME, time)
            }
            val result = db.insert(TABLE_APPOINTMENTS, null, values)
            db.close()

            return if (result != -1L) {
                Log.d("DatabaseHelper", "Appointment booked successfully for $doctor on $date at $time")
                true
            } else {
                Log.e("DatabaseHelper", "Failed to book appointment")
                false
            }
        }


        // Method to insert ambulance booking details
        fun insertAmbulanceDetails(
            patientName: String,
            contactNumber: String,
            streetName: String,
            area: String,
            city: String,
            state: String,
            selectedHospital: String,
            symptoms: String?,
            emergency: Boolean
        ): Boolean {
            val db = writableDatabase
            val contentValues = ContentValues().apply {
                put(COLUMN_AMBULANCE_PATIENT_NAME, patientName)
                put("contact_number", contactNumber)  // New field for Contact Number
                put("street_name", streetName)
                put("area", area)
                put("city", city)
                put("state", state)
                put("selected_hospital", selectedHospital)  // Stores the chosen hospital
                put("symptoms", symptoms ?: "")  // Optional Symptoms
                put(COLUMN_AMBULANCE_EMERGENCY, if (emergency) 1 else 0)  // 1 for emergency, 0 otherwise
            }

            return try {
                val result = db.insert(TABLE_AMBULANCE, null, contentValues)
                if (result == -1L) {
                    Log.e("DatabaseHelper", "❌ Failed to insert ambulance booking details")
                } else {
                    Log.d("DatabaseHelper", "✅ Ambulance booking details inserted successfully")
                }
                result != -1L
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "❌ Error inserting ambulance details: ${e.message}", e)
                false
            } finally {
                db.close()
            }
        }


        fun getWalletBalance(userId: Int): Int {
            val db = this.readableDatabase
            var balance = 0

            val query = "SELECT balance FROM wallet WHERE user_id = ?"
            val cursor = db.rawQuery(query, arrayOf(userId.toString()))

            if (cursor.moveToFirst()) {
                balance = cursor.getInt(cursor.getColumnIndexOrThrow("balance"))
            }

            cursor.close()
            db.close()
            return balance
        }



        data class Caregiver(
            val name: String,
            val gender: String,
            val shiftStart: String,
            val shiftEnd: String,
            val available: Boolean
        )


        // User data class
        data class User(
            val id: Int,
            val name: String,
            val email: String,
            val password: String,
            val mobile: String,
            val dob: String? = null,  // Added nullable DOB field
            val profileImageUri: String? = null // Nullable URI for profile image
        )


        // Patient data class
        data class Patient(
            val id: Int,
            val guardianName: String,
            val guardianContact: String,
            val patientAddress: String,
            val patientEmail: String,
            val patientPhone: String,
            val patientPassword: String,
            val prescriptionUri: String? = null
        )

        // Add this with your other data classes
        data class UserSettings(
            val darkMode: Boolean,
            val fontSize: Float,
            val notifications: Boolean
        )

        // ElderCare data class (new)
        data class ElderCare(
            val id: Long,
            val patientName: String,
            val gender: String,
            val shiftStart: String,
            val shiftEnd: String
        )
    }
