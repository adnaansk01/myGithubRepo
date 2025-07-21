package com.ad.pocketpilot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ad.pocketpilot.data.local.MetricType
import com.ad.pocketpilot.data.local.TransactionEntity
import com.ad.pocketpilot.ui.theme.ExpenseRed
import com.ad.pocketpilot.ui.theme.IncomeGreen
import com.ad.pocketpilot.ui.theme.PocketPilotTheme
import com.ad.pocketpilot.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddExpenseClick: () -> Unit,
    navController: NavHostController
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val transactionViewModel: TransactionViewModel = viewModel()
    var categories: State<List<TransactionEntity>>?
    val totalIncome by transactionViewModel.getTotalIncome().collectAsState()
    val totalExpense by transactionViewModel.getTotalExpense().collectAsState()
    val totalBalance by transactionViewModel.getTotalBalance().collectAsState()
    var selectedMetricType by remember { mutableStateOf<MetricType?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    selectedMetricType?.let { metricType ->
        ModalBottomSheet(
            onDismissRequest = {selectedMetricType = null},
            sheetState = bottomSheetState
        ) {
            AnalyticBottomSheetContent(metricType)
        }
    }

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
                .padding(16.dp)
        ) {
            DashboardSection(totalBalance,totalExpense,totalIncome,
                onCardClick = {
                    metricType ->
                    selectedMetricType = metricType
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransactionFilterChips(
                selectedFilter = selectedCategory,
                onFilterSelected = {selectedCategory = it}
            )
            Spacer(modifier = Modifier.height(8.dp))

            categories = if(selectedCategory == "All") {
                transactionViewModel.getAllTransactions().collectAsState()
            } else {
                transactionViewModel.getByType(selectedCategory).collectAsState()
            }
            if(categories.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories.value) {
                            transaction ->
                        TransactionItem(
                            transaction,
                            onDelete = { transactionViewModel.delete(transaction.id) },
                            onEdit = { navController.navigate("addexpensescreen/${transaction.id}") }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun DashboardSection(
    totalBalance: Double,
    totalExpense: Double,
    totalIncome: Double,
    onCardClick: (MetricType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // Total Balance
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF388E3C)),
            modifier = Modifier.fillMaxWidth().clickable(
                enabled = true,
                onClick = { onCardClick(MetricType.BALANCE) }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Total Balance",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${formatAmount(totalBalance)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expenses and Budget side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            cardComposable(
                totalIncome,
                Color(0xFF1976D2),
                "Income"
            ) { onCardClick(MetricType.INCOME) }
            cardComposable(
                totalExpense,
                Color(0xFFD32F2F),
                "Expenses"
            ) { onCardClick(MetricType.EXPENSE) }
        }
    }
}

@Composable
private fun RowScope.cardComposable(
    totalAmount: Double,
    cardColor: Color,
    cardCategory: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.weight(1f).clickable(
            enabled = true,
            onClick = onClick
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(cardCategory, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${formatAmount(totalAmount)}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun formatAmount(amount: Double): String {
    return String.format("%,.2f",amount)
}

@Composable
fun TransactionFilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All", "Income", "Expense")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(text = filter)
                },
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null,
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

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
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onDelete: (TransactionEntity) -> Unit,
    onEdit: (TransactionEntity) -> Unit)
{

    val dismissState = rememberDismissState(
        confirmStateChange = {
            when (it) {
                DismissValue.DismissedToStart -> {
                    onDelete(transaction)
                    false // Prevent auto-dismiss
                }
                DismissValue.DismissedToEnd -> {
                    onEdit(transaction)
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                DismissDirection.StartToEnd -> Color(0xFF1976D2) // Edit - Blue
                DismissDirection.EndToStart -> Color(0xFFD32F2F) // Delete - Red
                null -> Color.Transparent
            }

            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Edit
                DismissDirection.EndToStart -> Icons.Default.Delete
                else -> null
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .padding(horizontal = 16.dp),
                contentAlignment = if (direction == DismissDirection.StartToEnd)
                    Alignment.CenterStart else Alignment.CenterEnd
            ) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = Color.White)
                }
            }
        },
        directions = setOf(
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart
        ),
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(transaction.title, style = MaterialTheme.typography.titleMedium)
                        Text("📅 ${transaction.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("Category: ${transaction.category}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF3B82F6))
                    }
                    Text(
                        text = (if (transaction.type == "Expense") "- ₹" else "+ ₹") + transaction.amount,
                        color = if (transaction.type == "Expense") ExpenseRed else IncomeGreen
                    )
                }
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PocketPilotTheme {
        HomeScreen(onAddExpenseClick = {}, rememberNavController())
    }
}