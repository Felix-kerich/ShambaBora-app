package com.app.shamba_bora.data.model

/**
 * Enums for Records Keeping based on FRONTEND_RECORDS_KEEPING documentation
 */

enum class Season(val displayName: String) {
    LONG_RAIN("Long Rain"),
    SHORT_RAIN("Short Rain"),
    DRY("Dry");

    companion object {
        fun fromString(value: String?): Season {
            return values().firstOrNull { it.name == value } ?: LONG_RAIN
        }
    }
}

enum class AreaUnit(val displayName: String) {
    HA("Hectares (ha)"),
    ACRES("Acres"),
    M2("Square Meters (m²)");

    companion object {
        fun fromString(value: String?): AreaUnit {
            return values().firstOrNull { it.name == value } ?: HA
        }
    }
}

enum class ActivityType(val displayName: String) {
    LAND_PREPARATION("Land Preparation"),
    PLANTING("Planting"),
    SEEDING("Seeding"),
    TRANSPLANTING("Transplanting"),
    IRRIGATION("Irrigation"),
    FERTILIZING("Fertilizing"),
    TOP_DRESSING("Top Dressing"),
    SPRAYING("Spraying"),
    PEST_CONTROL("Pest Control"),
    DISEASE_CONTROL("Disease Control"),
    WEEDING("Weeding"),
    PRUNING("Pruning"),
    THINNING("Thinning"),
    HARVESTING("Harvesting"),
    YIELD_RECORDING("Yield Recording"),
    STORAGE("Storage"),
    TRANSPORT("Transport"),
    MARKETING("Marketing"),
    MAINTENANCE("Maintenance"),
    OTHER("Other");

    companion object {
        fun fromString(value: String?): ActivityType {
            return values().firstOrNull { it.name == value } ?: OTHER
        }
    }
}

enum class ExpenseCategory(val displayName: String) {
    SEEDS("Seeds"),
    FERTILIZER("Fertilizer"),
    PESTICIDES("Pesticides"),
    LABOR("Labor"),
    EQUIPMENT("Equipment"),
    TRANSPORT("Transport"),
    IRRIGATION("Irrigation"),
    STORAGE("Storage"),
    MARKETING("Marketing"),
    ADMINISTRATIVE("Administrative"),
    MAINTENANCE("Maintenance"),
    OTHER("Other");

    companion object {
        fun fromString(value: String?): ExpenseCategory {
            return values().firstOrNull { it.name == value } ?: OTHER
        }
    }
}

enum class GrowthStage(val displayName: String) {
    PRE_PLANTING("Pre-Planting"),
    PLANTING("Planting"),
    EARLY_GROWTH("Early Growth"),
    VEGETATIVE("Vegetative"),
    FLOWERING("Flowering"),
    FRUITING("Fruiting"),
    MATURITY("Maturity"),
    HARVEST("Harvest"),
    POST_HARVEST("Post-Harvest"),
    STORAGE("Storage");

    companion object {
        fun fromString(value: String?): GrowthStage {
            return values().firstOrNull { it.name == value } ?: PRE_PLANTING
        }
    }
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    MPESA("M-Pesa"),
    CHEQUE("Cheque"),
    TRANSFER("Bank Transfer");

    companion object {
        fun fromString(value: String?): PaymentMethod {
            return values().firstOrNull { it.name == value } ?: CASH
        }
    }
}

enum class WeatherCondition(val displayName: String) {
    SUNNY("Sunny"),
    RAINY("Rainy"),
    CLOUDY("Cloudy"),
    WINDY("Windy"),
    MIXED("Mixed");

    companion object {
        fun fromString(value: String?): WeatherCondition {
            return values().firstOrNull { it.name == value } ?: SUNNY
        }
    }
}

enum class SoilCondition(val displayName: String) {
    WET("Wet"),
    DRY("Dry"),
    WELL_DRAINED("Well-Drained"),
    MUDDY("Muddy");

    companion object {
        fun fromString(value: String?): SoilCondition {
            return values().firstOrNull { it.name == value } ?: WELL_DRAINED
        }
    }
}

enum class YieldUnit(val displayName: String) {
    KG("Kilograms (kg)"),
    BAGS("Bags"),
    TONS("Tons"),
    LITERS("Liters");

    companion object {
        fun fromString(value: String?): YieldUnit {
            return values().firstOrNull { it.name == value } ?: KG
        }
    }
}

enum class QualityGrade(val displayName: String) {
    GRADE_A("Grade A"),
    GRADE_B("Grade B"),
    GRADE_C("Grade C"),
    GRADE_1("Grade 1"),
    GRADE_2("Grade 2");

    companion object {
        fun fromString(value: String?): QualityGrade {
            return values().firstOrNull { it.name == value } ?: GRADE_1
        }
    }
}

enum class InputType(val displayName: String) {
    SEED("Seed"),
    FERTILIZER("Fertilizer"),
    PESTICIDE("Pesticide"),
    HERBICIDE("Herbicide"),
    FUNGICIDE("Fungicide"),
    INSECTICIDE("Insecticide"),
    SOIL_AMENDMENT("Soil Amendment"),
    BIOFERTILIZER("Biofertilizer"),
    OTHER("Other");

    companion object {
        fun fromString(value: String?): InputType {
            return values().firstOrNull { it.name == value } ?: OTHER
        }
    }
}

enum class RecurringFrequency(val displayName: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly");

    companion object {
        fun fromString(value: String?): RecurringFrequency {
            return values().firstOrNull { it.name == value } ?: MONTHLY
        }
    }
}
