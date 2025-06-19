package com.ad.pocketpilot.repository

import com.ad.pocketpilot.data.local.TransactionDao
import com.ad.pocketpilot.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val dao: TransactionDao) {

    fun getAllTransactions() : Flow<List<TransactionEntity>> {
        return dao.getAllTransactions();
    }

    suspend fun insert(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    suspend fun delete(id: Int) {
        dao.deleteTransaction(id)
    }

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> {
        return dao.getTransactionsByType(type)
    }

    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> {
        return dao.getTransactionsByCategory(category)
    }

    fun getTotalExpense() : Flow<Double> = dao.getSumByType("Expense").map { it ?: 0.0 }

    fun getTotalIncome() : Flow<Double> = dao.getSumByType("Income").map { it ?: 0.0 }
}
