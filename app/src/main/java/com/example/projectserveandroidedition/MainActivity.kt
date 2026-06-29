package com.example.projectserveandroidedition

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.projectserveandroidedition.ui.theme.ProjectServeAndroidEditionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectServeAndroidEditionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectServeAndroidEditionTheme {
        HomeScreen()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputScreen(onDismiss: () -> Unit) {
    var participantInput by rememberSaveable { mutableStateOf("") }
    var station by rememberSaveable { mutableStateOf("") }
    var score by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Fitness Test Scoring",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = participantInput,
                    onValueChange = { participantInput = it },
                    label = { Text("Participant") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = station,
                    onValueChange = { station = it },
                    label = { Text("Station") },
                    placeholder = {
                        Text("e.g. Push-ups, Sit-ups, Shuttle Run")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = score,
                    onValueChange = { score = it },
                    label = { Text(scoreUnitLabel(station)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Done")
                    }

                    Button(
                        onClick = {
                            val db = Firebase.firestore

                            val studentData = hashMapOf(
                                "participant" to participantInput,
                                "station" to station,
                                "score" to score,
                                "timestamp" to System.currentTimeMillis()
                            )

                            db.collection("students")
                                .add(studentData)
                                .addOnSuccessListener {
                                    println("Saved successfully!")
                                    onDismiss()
                                }
                                .addOnFailureListener {
                                    println("Firebase error: ${it.message}")
                                }
                        },
                        enabled = participantInput.isNotBlank()
                                && station.isNotBlank()
                                && score.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

fun scoreUnitLabel(station: String): String {
    return when (station.lowercase()) {
        "shuttle run", "1.6km run", "run1.6km", "run 1.6km" -> "Score (sec)"
        "standing broad jump" -> "Score (cm)"
        else -> "Score (reps)"
    }
}


@Composable
fun ScannerScreen(onDismiss: () -> Unit) {
    var records by remember { mutableStateOf(listOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FC.toInt()))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = onDismiss) {
                Text("← Back")
            }

            Text(
                text = "Fitness Test Scoring",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Card {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Scan QR Code", fontWeight = FontWeight.Bold)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Camera is off")
                    }

                    Button(
                        onClick = {
                            records = records + "Scanned Participant • Push-ups • 25 reps"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Scanning")
                    }
                }
            }

            Card {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Activity Log Records", fontWeight = FontWeight.Bold)

                    if (records.isEmpty()) {
                        Text(
                            "No records yet",
                            modifier = Modifier.padding(vertical = 30.dp)
                        )
                    } else {
                        records.forEach {
                            Text(
                                text = it,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}