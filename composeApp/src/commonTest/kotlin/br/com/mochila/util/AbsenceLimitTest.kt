package br.com.mochila.util

import kotlin.test.Test
import kotlin.test.assertEquals

// Calcula limites de faltas com base no periodo e frequencia da disciplina
class AbsenceLimitTest {

    // Conta semanas inclusivas entre duas datas
    @Test fun `weeksInPeriod conta semanas inclusivas`() {
        assertEquals(18, AbsenceLimit.weeksInPeriod("01/03/2025", "30/06/2025"))
        assertEquals(1, AbsenceLimit.weeksInPeriod("01/03/2025", "07/03/2025"))
    }

    // Multiplica aulas por semana pelas horas de cada aula
    @Test fun `weeklyHours multiplica frequencia semanal por horas da aula`() {
        assertEquals(8, AbsenceLimit.weeklyHours(2, 4))
    }

    // Total de aulas no periodo da disciplina
    @Test fun `totalClassSessions multiplica semanas por aulas semanais`() {
        assertEquals(36, AbsenceLimit.totalClassSessionsInPeriod("01/03/2025", "30/06/2025", 2))
        assertEquals(2, AbsenceLimit.totalClassSessionsInPeriod("01/03/2025", "07/03/2025", 2))
    }

    // Horas totais de aula no periodo
    @Test fun `totalHours usa aulas no periodo e horas por aula`() {
        assertEquals(144, AbsenceLimit.totalClassSessionsInPeriod("01/03/2025", "30/06/2025", 2) * 4)
    }

    // Maximo de faltas permitidas com 75% de frequencia minima
    @Test fun `maxAllowedAbsences com duas aulas semanais`() {
        val sessions = AbsenceLimit.totalClassSessionsInPeriod("01/03/2025", "30/06/2025", 2)
        assertEquals(36, sessions)
        assertEquals(9, sessions * (100 - 75) / 100)
    }

}
