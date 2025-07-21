package com.ad.pocketpilot.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ad.pocketpilot.PocketPilotApp
import com.ad.pocketpilot.data.local.MetricType
import com.ad.pocketpilot.repository.TransactionRepository
import com.ad.pocketpilot.utils.groupByMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: TransactionRepository
    private val _incomePerMonth = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val _expensePerMonth = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val _balancePerMonth = MutableStateFlow<Map<String, Float>>(emptyMap())

    init {
        val dao = (application as PocketPilotApp).getDatabaseInstance().transactionDao()
        _repository = TransactionRepository(dao)
    }

    fun loadAnalytics(metric: MetricType) {
        viewModelScope.launch {
            val transactions = _repository.getAllTransactions().first()

            Log.d("AnalyticsViewModel","transactions: $transactions")
            when (metric) {
                MetricType.INCOME -> _incomePerMonth.value = transactions.groupByMonth("Income")
                MetricType.EXPENSE -> _expensePerMonth.value = transactions.groupByMonth("Expense")
                MetricType.BALANCE -> {
                    val income = transactions.groupByMonth("Income")
                    val expense = transactions.groupByMonth("Expense")

                    val months = (income.keys + expense.keys).toSortedSet()
                    _balancePerMonth.value = months.associateWith { month ->
                        (income[month] ?: 0f) - (expense[month] ?: 0f)
                    }
                }
            }
        }
    }

    fun getExpensePerMonth() : StateFlow<Map<String, Float>> = _expensePerMonth

    fun getBalanceOverTime() : StateFlow<Map<String, Float>> = _balancePerMonth

    fun getIncomeOverTime() : StateFlow<Map<String, Float>> = _incomePerMonth

}