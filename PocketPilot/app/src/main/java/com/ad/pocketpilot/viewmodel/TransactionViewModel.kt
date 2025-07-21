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
    private val _uiState = MutableStateFlow(TransactionUIState())

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

    fun update(transaction: TransactionEntity) = viewModelScope.launch {
        _repository.update(transaction)
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

    fun loadTransactionById(id: Int) {
        viewModelScope.launch {
            val transaction = _repository.getTransactionById(id)
            transaction?.let {
                _uiState.value = TransactionUIState(
                    id = it.id,
                    title = it.title,
                    amount = it.amount.toString(),
                    category = it.category,
                    type = it.type
                )
            }
        }
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

    fun getUiState() = _uiState

    fun updateTitle(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun updateAmount(newAmount: String) {
        _uiState.value = _uiState.value.copy(amount = newAmount)
    }

    fun updateCategory(newCategory: String) {
        _uiState.value = _uiState.value.copy(category = newCategory)
    }

    fun updateType(newType: String) {
        _uiState.value = _uiState.value.copy(type = newType)
    }
}


data class TransactionUIState(
    val id: Int? = null,
    val title: String = "",
    val amount: String = "",
    val category: String = "Select Category",
    val type: String = "Income"
)