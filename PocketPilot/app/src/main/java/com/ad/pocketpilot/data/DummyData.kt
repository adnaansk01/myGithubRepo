package com.ad.pocketpilot.data

data class ExpenseUI(
    val title: String,
    val amount: Double,
    val category: String,
    val date: String
)

val sampleExpenses = listOf(
    ExpenseUI("Groceries", 600.0, "Food", "Jun 14"),
    ExpenseUI("Cab Fare", 300.0, "Transport", "Jun 13"),
    ExpenseUI("Restaurant", 850.0, "Food", "Jun 12"),
    ExpenseUI("Movie", 1200.0, "Entertainment", "Jun 10"),
    ExpenseUI("Electricity Bill", 2100.0, "Bills", "Jun 07")
)
