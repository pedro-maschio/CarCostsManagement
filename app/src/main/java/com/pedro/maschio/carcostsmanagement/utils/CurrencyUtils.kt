package com.pedro.maschio.carcostsmanagement.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val ptBrLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)

    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
}
