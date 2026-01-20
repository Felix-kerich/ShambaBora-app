package com.app.shamba_bora.data.constants

/**
 * Common farming inputs to reduce typing for farmers
 */
object FarmingInputs {
    
    // Common fertilizers used in East African farming
    val FERTILIZERS = listOf(
        "DAP (Di-ammonium Phosphate)",
        "CAN (Calcium Ammonium Nitrate)",
        "Urea",
        "NPK 17:17:17",
        "NPK 23:23:0",
        "NPK 20:10:10",
        "TSP (Triple Super Phosphate)",
        "Manure (Organic)",
        "Compost",
        "Foliar Fertilizer",
        "Other"
    )
    
    // Common maize seed varieties
    val MAIZE_SEEDS = listOf(
        "DH04",
        "H513",
        "H614",
        "H625",
        "H628",
        "H629",
        "Pannar 413",
        "Pannar 691",
        "Pannar 67",
        "Pioneer 3253",
        "SC Duma 43",
        "WH505",
        "WH507",
        "Local Variety",
        "Other"
    )
    
    // Common pesticides and herbicides
    val PESTICIDES = listOf(
        "Duduthrin 1.75 EC",
        "Bulldock Star 062.5 SC",
        "Karate 5 EC",
        "Bestox 50 EC",
        "Pyrinex Quick 44 EC",
        "Ema Super 19 EC",
        "Other"
    )
    
    val HERBICIDES = listOf(
        "Glyphosate (Roundup)",
        "Atrazine",
        "2,4-D",
        "Dual Gold 960 EC",
        "Wematch 250 EC",
        "Agil 100 EC",
        "Other"
    )
    
    // Common fungicides
    val FUNGICIDES = listOf(
        "Ridomil Gold MZ 68 WP",
        "Mancozeb",
        "Copper Oxychloride",
        "Metalaxyl",
        "Other"
    )
    
    // Equipment commonly used
    val EQUIPMENT = listOf(
        "Tractor",
        "Plough",
        "Harrow",
        "Planter",
        "Sprayer (Knapsack)",
        "Sprayer (Motorized)",
        "Harvester",
        "Wheelbarrow",
        "Hoe",
        "Machete/Panga",
        "Irrigation System",
        "Other"
    )
    
    // Storage locations
    val STORAGE_LOCATIONS = listOf(
        "Homestead Store",
        "Warehouse",
        "Silo",
        "NCPB Store",
        "Community Store",
        "On-Farm Storage",
        "Other"
    )
    
    // Common buyers
    val BUYERS = listOf(
        "NCPB (National Cereals and Produce Board)",
        "Local Trader",
        "Cooperative Society",
        "Direct Consumer",
        "Broker",
        "Export Company",
        "Processing Company",
        "Other"
    )
    
    // Common suppliers
    val SUPPLIERS = listOf(
        "Local Agro-Dealer",
        "Kenya Seed Company",
        "Simlaw Seeds",
        "Western Seed Company",
        "Farmers Choice",
        "Cooperative Society",
        "NCPB",
        "Other"
    )
    
    // Activity descriptions
    val ACTIVITY_DESCRIPTIONS = mapOf(
        "PLANTING" to listOf(
            "Maize planting - broadcast method",
            "Maize planting - line planting",
            "Maize planting - mechanized planter",
            "Custom description"
        ),
        "SEEDING" to listOf(
            "Maize planting - broadcast method",
            "Maize planting - line planting",
            "Maize planting - mechanized planter",
            "Custom description"
        ),
        "FERTILIZING" to listOf(
            "Basal fertilizer application (DAP/NPK)",
            "Top dressing - First application (CAN/Urea)",
            "Top dressing - Second application",
            "Foliar feeding",
            "Custom description"
        ),
        "TOP_DRESSING" to listOf(
            "Top dressing - First application (CAN/Urea)",
            "Top dressing - Second application",
            "Side dressing application",
            "Custom description"
        ),
        "WEEDING" to listOf(
            "First weeding - hand weeding",
            "Second weeding - hand weeding",
            "Chemical weeding - pre-emergence",
            "Chemical weeding - post-emergence",
            "Custom description"
        ),
        "PEST_CONTROL" to listOf(
            "Stalk borer control",
            "Fall armyworm control",
            "Aphid control",
            "General pest spray",
            "Custom description"
        ),
        "DISEASE_CONTROL" to listOf(
            "Fungal disease control",
            "Leaf blight treatment",
            "Rust disease control",
            "Custom description"
        ),
        "SPRAYING" to listOf(
            "Pest spray application",
            "Disease spray application",
            "Foliar spray",
            "Custom description"
        ),
        "HARVESTING" to listOf(
            "Green maize harvesting",
            "Dry maize harvesting",
            "Mechanized harvesting",
            "Custom description"
        )
    )
}
