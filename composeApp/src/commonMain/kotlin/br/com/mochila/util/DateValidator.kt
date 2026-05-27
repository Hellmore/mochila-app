package br.com.mochila.util


// Validacao de datas no formato DD/MM/AAAA
object DateValidator {

    fun isValid(date: String): Boolean {
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(date)) return false

        val parts = date.split("/")
        val day   = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year  = parts[2].toIntOrNull() ?: return false

        if (month !in 1..12) return false

        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11            -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
            else                   -> return false
        }

        return day in 1..daysInMonth
    }
}