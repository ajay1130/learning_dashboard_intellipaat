package com.example.learning_dashboard.domain.model

/**
 * Whole-number percentage, rounded down so a course never shows 100% until
 * every lesson is actually done.
 */
fun progressPercent(completed: Int, total: Int): Int {
    if (total <= 0) return 0
    return completed.coerceIn(0, total) * 100 / total
}
