package com.ad.pocketpilot.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ad.pocketpilot.PocketPilotApp
import com.ad.pocketpilot.data.local.TransactionEntity
import com.ad.pocketpilot.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val _repository: TransactionRepository
    private val _allTransactions:StateFlow<List<TransactionEntity>>
    private val _transactionsByCategory = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val _transactionsByType = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val _totalIncome: StateFlow<Double>
    private val _totalExpense: StateFlow<Double>
    private val _totalBalance: StateFlow<Double>

    init {
        val dao = (application as PocketPilotApp).getDatabaseInstance().transactionDao()
        _repository = TransactionRepository(dao)
//        fetchAllTransactions()
        _allTransactions = _repository.getAllTransactions().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(), emptyList()
        )
        _totalIncome = _repository.getTotalIncome().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(), 0.0
        )
        _totalExpense = _repository.getTotalExpense().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(), 0.0
        )
        _totalBalance = combine(_totalIncome, _totalExpense) {
            income, expense ->
            income - expense
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
    }

//    private fun fetchAllTransactions() {
//        viewModelScope.launch {
//            _repository.getAllTransactions().collect {
//                _allTransactions.value = it
//            }
//        }
//
//    }

    fun insert(transaction: TransactionEntity) = viewModelScope.launch {
        _repository.insert(transaction)
    }

    fun delete(id: Int) = viewModelScope.launch {
        _repository.delete(id)
    }

    fun getByType(type: String): StateFlow<List<TransactionEntity>> {
        viewModelScope.launch {
            _repository.getTransactionsByType(type).collect {
                _transactionsByType.value = it
            }
        }
        return _transactionsByType
    }

    fun getAllTransactions() : StateFlow<List<TransactionEntity>>  = _allTransactions

    fun fetchAllTransactionsByCategories(category: String) : StateFlow<List<TransactionEntity>> {
        viewModelScope.launch {
            _repository.getTransactionsByCategory(category).collect {
                _transactionsByCategory.value = it
            }
        }
        return _transactionsByCategory
    }

    fun getTotalIncome() : StateFlow<Double> = _totalIncome

    fun getTotalExpense() : StateFlow<Double> = _totalExpense

    fun getTotalBalance() : StateFlow<Double> = _totalBalance
}