package br.com.mochila.util

import br.com.mochila.model.Subject
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil


// Calculo de limite de faltas por disciplina
object AbsenceLimit {

    // Converte string de exibicao em LocalDate
    fun parseDisplayDate(date: String): LocalDate? {
        if (!DateValidator.isValid(date)) return null
        val parts = date.split("/")
        return try {
            LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
        } catch (_: Exception) {
            null
        }
    }

    // Semanas entre inicio e fim do periodo letivo
    fun weeksInPeriod(startDate: String, endDate: String): Int {
        val start = parseDisplayDate(startDate) ?: return 1
        val end = parseDisplayDate(endDate) ?: return 1
        if (end < start) return 1
        val days = start.daysUntil(end) + 1
        return maxOf(1, (days + 6) / 7)
    }

    fun weeklyClassCountFor(subject: Subject): Int =
        subject.weeklyClasses.coerceAtLeast(1)

    fun weeklyHours(weeklyClassCount: Int, hoursPerClass: Int): Int =
        weeklyClassCount.coerceAtLeast(1) * hoursPerClass.coerceAtLeast(0)

    fun weeklyHours(subject: Subject): Int =
        weeklyHours(weeklyClassCountFor(subject), subject.classHours)

    fun totalClassSessionsInPeriod(
        startDate: String,
        endDate: String,
        weeklyClassCount: Int,
    ): Int {
        val weeks = weeksInPeriod(startDate, endDate)
        return maxOf(1, weeks * weeklyClassCount.coerceAtLeast(1))
    }

    fun totalClassSessionsInPeriod(subject: Subject): Int =
        totalClassSessionsInPeriod(
            subject.startDate,
            subject.endDate,
            weeklyClassCountFor(subject),
        )

    fun totalHoursInPeriod(subject: Subject): Int =
        totalClassSessionsInPeriod(subject) * subject.classHours.coerceAtLeast(0)

    // Maximo de faltas permitido pela frequencia minima
    fun maxAllowedAbsences(subject: Subject): Int {
        val sessions = totalClassSessionsInPeriod(subject)
        if (subject.minFrequency <= 0) return 1
        return maxOf(1, sessions * (100 - subject.minFrequency) / 100)
    }

    fun isAtOrOverLimit(absenceCount: Int, subject: Subject): Boolean =
        absenceCount >= maxAllowedAbsences(subject)

    // Mensagem exibida ao atingir o limite
    fun warningMessage(subjectName: String, absenceCount: Int, maxAllowed: Int): String =
        "Atenção: você atingiu ou ultrapassou o limite de faltas em \"$subjectName\" " +
            "($absenceCount de $maxAllowed aulas permitidas). Verifique sua frequência mínima na disciplina."
}
