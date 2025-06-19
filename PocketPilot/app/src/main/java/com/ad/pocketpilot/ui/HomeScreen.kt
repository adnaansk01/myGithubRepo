package com.ad.pocketpilot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ad.pocketpilot.data.local.TransactionEntity
import com.ad.pocketpilot.ui.theme.PocketPilotTheme
import com.ad.pocketpilot.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    onAddExpenseClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val transactionViewModel: TransactionViewModel = viewModel()
    var categories: State<List<TransactionEntity>>? = null
    val totalIncome by transactionViewModel.getTotalIncome().collectAsState()
    val totalExpense by transactionViewModel.getTotalExpense().collectAsState()
    val totalBalance by transactionViewModel.getTotalBalance().collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = {onAddExpenseClick()}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add expense")
            }
        },
        topBar = {
            TopAppBar(
                title = {Text("PocketPilotApp", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)})
        }

    ) {
        padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(text = "Hello, $userName", style = MaterialTheme.typography.titleMedium)
            Text(text = "Track your expenses & control your budget",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF7FF) )
            ) {
                Column(modifier = Modifier.padding(16.dp) ) {
                    Text("Balance: ₹%.2f".format(totalBalance), style = MaterialTheme.typography.headlineSmall, color = Color(0xFF3B82F6))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Income: ₹%.2f".format(totalIncome), color = Color(0xFF388E3C))
                        Text("Expense: ₹%.2f".format(totalExpense), color = Color(0xFFD32F2F))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Filter by Category:", style = MaterialTheme.typography.titleSmall)
            CategoryDropdownMenu(
                selectedCategory = selectedCategory,
                onSelected = {selectedCategory = it},
                listOf("All", "Income", "Expense")
            )
            Spacer(modifier = Modifier.height(8.dp))

            categories = if(selectedCategory == "All") {
                transactionViewModel.getAllTransactions().collectAsState()
//                transactionViewModel.fetchAllTransactionsByCategories(selectedCategory).collectAsState()
            } else {
                transactionViewModel.getByType(selectedCategory).collectAsState()
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.value) {
                    expense ->
                    ExpenseItem(expense)
                }
            }
        }

    }
}


/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories : List<String>
) {
    val categories = listOf("All", "Food", "Transport", "Shopping", "Bills", "Entertainment", "Other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            colors = TextFieldDefaults.colors().copy(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownMenu(
    selectedCategory: String,
    onSelected: (String) -> Unit,
    categories : List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            colors = TextFieldDefaults.colors().copy(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onSelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF7FF))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(expense.title, style = MaterialTheme.typography.titleMedium)
                Text("📅 ${expense.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Category: ${expense.category}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF3B82F6))
            }
            Text("₹${expense.amount}", style = MaterialTheme.typography.titleMedium,
                color = if(expense.type.equals("Income",true)) {
                Color(0xFF388E3C) } else { Color(0xFFD32F2F) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PocketPilotTheme {
        HomeScreen("AD",onAddExpenseClick = {})
    }
}