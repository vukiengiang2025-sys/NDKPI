package com.example.lib

object KpiCalculator {
    fun calcSalesKPI(actual: Double, target: Double): Double {
        if (target <= 0) return 0.0
        val ratio = actual / target
        if (ratio < 0.8) return 0.0
        return Math.min(ratio * 80.0, 80.0 * 1.875) // Max 150
    }

    fun calcCoverageKPI(actual: Double, target: Double): Double {
        if (target <= 0) return 0.0
        val ratio = actual / target
        if (ratio < 0.8) return 0.0
        return Math.min(ratio * 10.0, 10.0 * 1.5) // Max 15
    }

    fun calcSKUKPI(sku: Double): Double {
        return when {
            sku >= 4.7 -> 7.5
            sku >= 4.5 -> 7.0
            sku >= 4.3 -> 6.5
            sku >= 4.1 -> 6.0
            sku >= 3.9 -> 5.5
            sku >= 3.7 -> 5.0
            sku >= 3.5 -> 4.5
            sku >= 3.3 -> 4.0
            else -> 0.0
        }
    }
    
    fun calcTotalKPI(salesActual: Double, salesTarget: Double, covActual: Double, covTarget: Double, sku: Double): Double {
        return calcSalesKPI(salesActual, salesTarget) + 
               calcCoverageKPI(covActual, covTarget) + 
               calcSKUKPI(sku)
    }

    fun calculateBonus(n1: Double, n2: Double, n3: Double, kpiPoints: Double): Double {
        val baseBonus = n1 * 0.03 + n2 * 0.05 + n3 * 0.08
        
        val kpiCoeff = when {
            kpiPoints >= 150 -> 1.5
            kpiPoints >= 80 -> kpiPoints / 100.0
            kpiPoints >= 70 -> 0.5
            else -> 0.0
        }

        val extraBonus = when {
            kpiCoeff >= 1.5 -> 1500000.0
            kpiCoeff >= 1.3 -> 1000000.0
            kpiCoeff >= 1.2 -> 500000.0
            else -> 0.0
        }

        return baseBonus * kpiCoeff + extraBonus
    }
}
