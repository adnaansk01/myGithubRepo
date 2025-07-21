package com.ad.pocketpilot.ui

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ad.pocketpilot.data.local.TransactionEntity
import com.ad.pocketpilot.ui.theme.PocketPilotTheme
import com.ad.pocketpilot.viewmodel.TransactionViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    navController: NavHostController,
    transactionId: Int? = null
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val transactionViewModel: TransactionViewModel = viewModel()
    val categories = transactionViewModel.getAllTransactions().collectAsState()
    val uiState by transactionViewModel.getUiState().collectAsState()

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            transactionViewModel.loadTransactionById(transactionId)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId == null) {"Add Transaction" }
                        else {"Edit Transaction"},
                    style = MaterialTheme.typography.titleLarge)},
                navigationIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus(force = true)
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { transactionViewModel.updateTitle(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { if (it.all { char -> char.isDigit() }) transactionViewModel.updateAmount(it)},
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            TransactionTypeSelector(
                selectedType = uiState.type,
                onTypeChange = { transactionViewModel.updateType(it) }
            )

            // Category Dropdown
            CategoryDropdownMenu(
                selectedCategory = uiState.category,
                onSelected = {transactionViewModel.updateCategory(it)},
                if (uiState.type == "Income") {
                    listOf("Salary", "Freelance", "Investment", "Gift", "Refund", "Other")
                } else {
                    listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Other")
                }
            )

            // Date Picker
            OutlinedButton(onClick = {
                val picker = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth
                )
                picker.show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Select Date: $selectedDate")
            }

            // Save Button
            Button(
                onClick = {
                    if (uiState.title.isNotBlank() && uiState.amount.isNotBlank() && uiState.category != "Select Category") {
                        val transaction = TransactionEntity(
                            id = uiState.id ?: 0,
                            title = uiState.title,
                            amount = uiState.amount.toDouble(),
                            category = uiState.category,
                            type = uiState.type,
                            date = selectedDate.toString()
                        )
                        if(transactionId == null) {
                            transactionViewModel.insert(transaction)
                        } else {
                            transactionViewModel.update(transaction)
                        }

                        Log.d("AddExpenseScreen","${categories.value}")
                        transactionViewModel.updateTitle("")
                        transactionViewModel.updateAmount("")
                        navController.popBackStack()
                    } else {
                        Log.d("AddExpenseScreen"," Title is not empty = ${uiState.title.isNotBlank()} : Amount is not empty= ${uiState.amount.isNotBlank()}")
                        Toast.makeText(context,"Please fill the required fields!!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White,
                    disabledContentColor = Color.Gray, disabledContainerColor = Color.Gray)
            ) {
                Text(text =
                    if (transactionId == null) { "Add" }
                    else { "Update" }
                )
            }
        }
    }
}


@Composable
fun TransactionTypeSelector(
    selectedType: String,
    onTypeChange: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf("Income", "Expense").forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedType == type,
                    onClick = {
                        onTypeChange(type)
                    }
                )
                Text(type)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    PocketPilotTheme {
        AddExpenseScreen({}, rememberNavController(), 1)
    }
}