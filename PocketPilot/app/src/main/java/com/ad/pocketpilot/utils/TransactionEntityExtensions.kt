package com.ad.pocketpilot.utils

import android.util.Log
import com.ad.pocketpilot.data.local.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun List<TransactionEntity>.groupByMonth(transactionType: String): Map<String, Float> {
    val inputDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    return this
        .filter { it.type.equals(transactionType,ignoreCase = true) && it.date.isNotBlank() } // or Income
        .mapNotNull { txn ->
            try {
                val date = inputDate.parse(txn.date) ?:
                return@mapNotNull null
                val monthKey = sdf.format(date)
                monthKey to txn.amount
            } catch (e: Exception) {
                Log.d("groupByMonth","Exception: ${e.message}")
                null
            }
        }
        .groupBy({it.first}, {it.second})
        .mapValues { (_,txnList) -> txnList.sum().toFloat() }
}