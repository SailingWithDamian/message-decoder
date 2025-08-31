package eu.sailwithdamian.message_decoder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.sailwithdamian.message_decoder.DecodeMessage
import eu.sailwithdamian.message_decoder.ExportMessages
import eu.sailwithdamian.message_decoder.Message

@Composable
fun DecoderScreen() {
    val messages = remember { mutableStateListOf<Message>() }
    val inputMessage = remember { mutableStateOf("") }

    val alertMessage = remember { mutableStateOf("") }
    val showAlert = remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = inputMessage.value,
                onValueChange = { newText -> inputMessage.value = newText },
                label = { Text("Enter message") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (inputMessage.value.length > 0) {
                        val decodedMessage = DecodeMessage(inputMessage.value);
                        if (decodedMessage == null) {
                            alertMessage.value = "Failed to decode message"
                            showAlert.value = true
                        } else {
                            val expectedMessageNumber = messages.size + 1;
                            var messageIsValid = false;

                            if (messages.size == 0) {
                                if (decodedMessage.number != 1) {
                                    alertMessage.value = "Expected first message, got ${decodedMessage.number}"
                                    showAlert.value = true
                                } else {
                                    // First message - setup
                                    messageIsValid = true;
                                }
                            } else {
                                // Not first message - verify in sequence
                                if (decodedMessage.number != expectedMessageNumber) {
                                    alertMessage.value = "Invalid Message - Expected index ${expectedMessageNumber} got ${decodedMessage.number}"
                                    showAlert.value = true
                                } else if (decodedMessage.type != messages[0].type) {
                                    alertMessage.value = "Invalid Message - Expected type ${messages[0].type} got ${decodedMessage.type}"
                                    showAlert.value = true
                                } else {
                                    messageIsValid = true;
                                }
                            }

                            if (messageIsValid) {
                                // Valid message - add to array
                                messages.add(decodedMessage)
                                if (decodedMessage.number == decodedMessage.total) {
                                    // Last message - process
                                    val outputFileName = ExportMessages(messages)
                                    if (outputFileName == null) {
                                        alertMessage.value = "Failed to write message"
                                        showAlert.value = true
                                    } else {
                                        alertMessage.value = "Output to ${outputFileName}"
                                        showAlert.value = true
                                    }
                                    messages.clear()
                                }
                            }
                        }
                        inputMessage.value = ""
                    }
                },
                modifier = Modifier.size(width = 1000.dp, height = 100.dp)
            ) {
                Text("Add")
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    if (messages.isNotEmpty()) {
                        Column {
                            Text("Expected Messages: ${messages[0].total}")
                            Text("Current Messages: ${messages.size}")
                        }
                    } else {
                        Text("Waiting for first message")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    messages.clear();
                },
                modifier = Modifier.size(width = 1000.dp, height = 100.dp)
            ) {
                Text("Reset")
            }
            if (showAlert.value) {
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = {
                        TextButton(onClick = {
                            showAlert.value = false
                        }) {
                            Text("OK")
                        }
                    },
                    text = { Text(alertMessage.value) }
                )
            }
        }
    }
}
