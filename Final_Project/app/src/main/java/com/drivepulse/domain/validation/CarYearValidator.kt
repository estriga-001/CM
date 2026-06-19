package com.drivepulse.domain.validation

import java.time.Year

object CarYearValidator {

    const val MIN_YEAR = 1886

    val maxYear: Int
        get() = Year.now().value

    fun isValid(year: Int): Boolean {
        return year in MIN_YEAR..maxYear
    }
}
