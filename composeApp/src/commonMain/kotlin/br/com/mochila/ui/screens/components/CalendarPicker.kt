package br.com.mochila.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Cabecalhos e nomes de mes para o calendario
private val dayHeaders = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

private val monthNamesFull = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
)

private val orange = Color(0xFFFFBA5E)
private val rosa = Color(0xFFFF6694)

private fun isLeapYear(year: Int) = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

private fun daysInMonth(month: Int, year: Int) = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (isLeapYear(year)) 29 else 28
    else -> 30
}

private fun parseDDMMYYYY(date: String): Triple<Int, Int, Int>? {
    val p = date.split("/")
    if (p.size != 3) return null
    val d = p[0].toIntOrNull() ?: return null
    val m = p[1].toIntOrNull() ?: return null
    val y = p[2].toIntOrNull() ?: return null
    return Triple(d, m, y)
}

// Seletor de data no formato DD/MM/AAAA
@Composable
fun CalendarPicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    accentColor: Color = orange,
    modifier: Modifier = Modifier,
) {
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val parsed = remember(selectedDate) { parseDDMMYYYY(selectedDate) }

    var displayMonth by remember(parsed) { mutableStateOf(parsed?.second ?: today.monthNumber) }
    var displayYear by remember(parsed) { mutableStateOf(parsed?.third ?: today.year) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val yearRange = (today.year - 5)..(today.year + 10)

    fun prevMonth() { if (displayMonth == 1) { displayMonth = 12; displayYear-- } else displayMonth-- }
    fun nextMonth() { if (displayMonth == 12) { displayMonth = 1; displayYear++ } else displayMonth++ }

    val firstDayOrdinal = run {
        val d = kotlinx.datetime.LocalDate(displayYear, displayMonth, 1)
        (d.dayOfWeek.ordinal + 1) % 7
    }
    val daysCount = daysInMonth(displayMonth, displayYear)
    val prevMonthYear = if (displayMonth == 1) displayYear - 1 else displayYear
    val prevMonthNum = if (displayMonth == 1) 12 else displayMonth - 1
    val prevDaysCount = daysInMonth(prevMonthNum, prevMonthYear)
    val nextMonthYear = if (displayMonth == 12) displayYear + 1 else displayYear
    val nextMonthNum = if (displayMonth == 12) 1 else displayMonth + 1

    val rowCount = ((firstDayOrdinal + daysCount + 6) / 7).coerceAtLeast(5)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, accentColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { prevMonth() },
                contentAlignment = Alignment.Center,
            ) {
                Text("<", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .border(1.dp, orange, RoundedCornerShape(4.dp))
                            .clickable { monthExpanded = true }
                            .padding(horizontal = 5.dp, vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(monthNamesFull[displayMonth - 1], color = rosa, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Text("▾", color = rosa, fontSize = 8.sp)
                    }
                    DropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                        monthNamesFull.forEachIndexed { i, name ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = name,
                                        fontSize = 12.sp,
                                        color = if (i + 1 == displayMonth) rosa else Color(0xFF333333),
                                        fontWeight = if (i + 1 == displayMonth) FontWeight.Bold else FontWeight.Normal,
                                    )
                                },
                                onClick = { displayMonth = i + 1; monthExpanded = false },
                            )
                        }
                    }
                }

                Box {
                    Row(
                        modifier = Modifier
                            .border(1.dp, orange, RoundedCornerShape(4.dp))
                            .clickable { yearExpanded = true }
                            .padding(horizontal = 5.dp, vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(displayYear.toString(), color = rosa, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Text("▾", color = rosa, fontSize = 8.sp)
                    }
                    DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                        yearRange.forEach { year ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = year.toString(),
                                        fontSize = 12.sp,
                                        color = if (year == displayYear) rosa else Color(0xFF333333),
                                        fontWeight = if (year == displayYear) FontWeight.Bold else FontWeight.Normal,
                                    )
                                },
                                onClick = { displayYear = year; yearExpanded = false },
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { nextMonth() },
                contentAlignment = Alignment.Center,
            ) {
                Text(">", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(3.dp))

        
        Row(modifier = Modifier.fillMaxWidth()) {
            dayHeaders.forEach { header ->
                Text(
                    text = header,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = orange,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(1.dp))

        
        repeat(rowCount) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
            ) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val (day, month, year) = when {
                        cellIndex < firstDayOrdinal -> Triple(
                            prevDaysCount - firstDayOrdinal + cellIndex + 1,
                            prevMonthNum,
                            prevMonthYear,
                        )
                        cellIndex < firstDayOrdinal + daysCount -> Triple(
                            cellIndex - firstDayOrdinal + 1,
                            displayMonth,
                            displayYear,
                        )
                        else -> Triple(
                            cellIndex - firstDayOrdinal - daysCount + 1,
                            nextMonthNum,
                            nextMonthYear,
                        )
                    }

                    val isCurrentMonth = month == displayMonth && year == displayYear
                    val isSelected = parsed != null && day == parsed.first && month == parsed.second && year == parsed.third
                    val isToday = day == today.dayOfMonth && month == today.monthNumber && year == today.year

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = 1.dp)
                            .clip(CircleShape)
                            .then(
                                when {
                                    isSelected -> Modifier.background(rosa)
                                    isToday && isCurrentMonth -> Modifier.background(rosa.copy(alpha = 0.15f))
                                    else -> Modifier
                                }
                            )
                            .clickable {
                                val d = day.toString().padStart(2, '0')
                                val m = month.toString().padStart(2, '0')
                                onDateSelected("$d/$m/$year")
                                displayMonth = month
                                displayYear = year
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = day.toString(),
                            color = when {
                                isSelected -> Color.White
                                !isCurrentMonth -> Color.Gray.copy(alpha = 0.3f)
                                else -> rosa
                            },
                            fontSize = 10.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
