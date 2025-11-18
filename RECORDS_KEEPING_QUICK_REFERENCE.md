# Records Keeping - Quick Reference Guide

## 🚀 Quick Start

### Using Dropdowns
All dropdowns follow the same pattern:
```kotlin
var selectedValue by remember { mutableStateOf(Season.LONG_RAIN) }

SeasonDropdown(
    selectedSeason = selectedValue,
    onSeasonChange = { selectedValue = it }
)
```

### Creating a Patch
```kotlin
val patch = MaizePatchDTO(
    name = "Block A - 2025",
    year = 2025,
    season = "LONG_RAIN",
    areaUnit = "ha",
    area = 1.25,
    location = "Field 1",
    plantingDate = LocalDate.of(2025, 3, 10),
    expectedHarvestDate = LocalDate.of(2025, 8, 20)
)

// Validate before saving
if (patch.isValid()) {
    // Save to API
}
```

### Recording an Activity
```kotlin
val activity = FarmActivityRequest(
    activityType = "PLANTING",
    cropType = "Maize",
    activityDate = LocalDate.now(),
    description = "Planting H511 variety",
    areaSize = 1.25,
    units = "ha",
    seedVarietyName = "H511",
    patchId = 3,
    weatherConditions = "SUNNY",
    soilConditions = "WELL_DRAINED"
)

if (activity.isValid()) {
    // Save to API
}
```

### Recording an Expense
```kotlin
val expense = FarmExpenseRequest(
    cropType = "Maize",
    category = "FERTILIZER",
    description = "Urea 50kg bags",
    amount = 2500.00,
    expenseDate = LocalDate.now(),
    supplier = "Local Agro-Dealer",
    paymentMethod = "MPESA",
    growthStage = "VEGETATIVE",
    patchId = 3
)

if (expense.isValid()) {
    // Save to API
}
```

### Recording a Yield
```kotlin
val yield = YieldRecordRequest(
    cropType = "Maize",
    harvestDate = LocalDate.now(),
    yieldAmount = 2000.00,
    unit = "kg",
    areaHarvested = 1.25,
    marketPrice = 40.00,
    qualityGrade = "GRADE_A",
    patchId = 3
)

if (yield.isValid()) {
    // Save to API
}
```

## 📋 Available Dropdowns

### Season Selector
```kotlin
SeasonDropdown(
    selectedSeason = season,
    onSeasonChange = { season = it }
)
```
Options: LONG_RAIN, SHORT_RAIN, DRY

### Area Unit Selector
```kotlin
AreaUnitDropdown(
    selectedUnit = areaUnit,
    onUnitChange = { areaUnit = it }
)
```
Options: HA (hectares), ACRES, M2 (square meters)

### Activity Type Selector
```kotlin
ActivityTypeDropdown(
    selectedType = activityType,
    onTypeChange = { activityType = it }
)
```
Options: PLANTING, WEEDING, FERTILIZING, TOP_DRESSING, SPRAYING, HARVESTING, etc. (20+ types)

### Expense Category Selector
```kotlin
ExpenseCategoryDropdown(
    selectedCategory = category,
    onCategoryChange = { category = it }
)
```
Options: SEEDS, FERTILIZER, PESTICIDES, LABOR, EQUIPMENT, TRANSPORT, IRRIGATION, STORAGE, MARKETING, ADMINISTRATIVE, MAINTENANCE, OTHER

### Growth Stage Selector
```kotlin
GrowthStageDropdown(
    selectedStage = growthStage,
    onStageChange = { growthStage = it }
)
```
Options: PRE_PLANTING, PLANTING, EARLY_GROWTH, VEGETATIVE, FLOWERING, FRUITING, MATURITY, HARVEST, POST_HARVEST, STORAGE

### Payment Method Selector
```kotlin
PaymentMethodDropdown(
    selectedMethod = paymentMethod,
    onMethodChange = { paymentMethod = it }
)
```
Options: CASH, MPESA, CHEQUE, TRANSFER

### Weather Condition Selector
```kotlin
WeatherConditionDropdown(
    selectedCondition = weatherConditions,
    onConditionChange = { weatherConditions = it }
)
```
Options: SUNNY, RAINY, CLOUDY, WINDY, MIXED

### Soil Condition Selector
```kotlin
SoilConditionDropdown(
    selectedCondition = soilConditions,
    onConditionChange = { soilConditions = it }
)
```
Options: WET, DRY, WELL_DRAINED, MUDDY

### Yield Unit Selector
```kotlin
YieldUnitDropdown(
    selectedUnit = unit,
    onUnitChange = { unit = it }
)
```
Options: KG, BAGS, TONS, LITERS

### Quality Grade Selector
```kotlin
QualityGradeDropdown(
    selectedGrade = qualityGrade,
    onGradeChange = { qualityGrade = it }
)
```
Options: GRADE_A, GRADE_B, GRADE_C, GRADE_1, GRADE_2

### Patch Selector
```kotlin
PatchSelectorDropdown(
    patches = patchList,
    selectedPatchId = selectedPatchId,
    onPatchSelect = { selectedPatchId = it }
)
```
Displays all user's patches with year and season info

## 🎨 Form Field Components

### Text Field
```kotlin
FormTextField(
    label = "Description",
    value = description,
    onValueChange = { description = it },
    placeholder = "Enter description",
    isRequired = true,
    minLines = 2,
    maxLines = 3,
    errorMessage = errorMsg
)
```

### Number Field
```kotlin
FormNumberField(
    label = "Amount",
    value = amount,
    onValueChange = { amount = it },
    placeholder = "0.00",
    isRequired = true,
    errorMessage = errorMsg
)
```

### Date Field
```kotlin
FormDateField(
    label = "Harvest Date",
    selectedDate = harvestDate,
    onDateChange = { harvestDate = it },
    isRequired = true,
    errorMessage = errorMsg
)
```

### Form Section
```kotlin
FormSection(title = "Section Title") {
    // Add form fields here
}
```

### Advanced Options (Collapsible)
```kotlin
AdvancedOptionsSection(title = "Advanced Options") {
    // Optional fields here
}
```

### Submit Button
```kotlin
FormSubmitButton(
    text = "Save Record",
    onClick = { /* Submit logic */ },
    enabled = true,
    isLoading = false
)
```

## 🔧 Enums - Quick Reference

### How to Use Enums
```kotlin
// Convert string to enum
val season = Season.fromString("LONG_RAIN") // Returns Season.LONG_RAIN

// Get display name
val displayName = Season.LONG_RAIN.displayName // "Long Rain"

// List all options
Season.values().toList() // [LONG_RAIN, SHORT_RAIN, DRY]
```

### All Enums Location
All enums defined in: `data/model/RecordsEnums.kt`

## 📱 Screen Navigation

### Main Records Dashboard
```
RecordsScreen
├── My Patches (new)
├── Farm Activities
├── Expenses
├── Yields
└── Statistics
```

### Patches Screen
```
PatchesScreen
├── Patch List
└── Create Patch Dialog
    ├── Patch Name
    ├── Location
    ├── Season (Dropdown)
    ├── Year
    ├── Area Size + Unit (Dropdown)
    ├── Planting Date
    ├── Expected Harvest Date
    └── Notes
```

### Create Activity Screen
```
CreateActivityScreen
├── Basic Information
│   ├── Activity Type (Dropdown)
│   ├── Activity Date
│   └── Description
├── Location & Area
├── Patch Selection
├── Products & Inputs
├── Conditions
├── Activity Costs
├── Advanced Options (collapsible)
└── Save Button
```

### Create Expense Screen
```
CreateExpenseScreen
├── Expense Information
│   ├── Category (Dropdown)
│   ├── Description
│   └── Amount
├── Date & Supplier
│   ├── Expense Date
│   ├── Supplier Name
│   └── Invoice Number
├── Payment Information
│   ├── Payment Method (Dropdown)
│   └── Growth Stage (Dropdown)
├── Patch Assignment
├── Recurring Expense (Checkbox)
├── Advanced Options (collapsible)
└── Save Button
```

### Create Yield Screen
```
CreateYieldScreen
├── Harvest Information
│   ├── Harvest Date
│   ├── Yield Amount
│   └── Yield Unit (Dropdown)
├── Harvest Area
│   └── Auto-calculated: Yield per Unit Area
├── Quality & Grading
│   ├── Quality Grade (Dropdown)
│   └── Storage Location
├── Market Information
│   ├── Market Price
│   ├── Buyer Name
│   └── Auto-calculated: Projected Revenue
├── Patch Assignment
├── Advanced Options (collapsible)
└── Save Button
```

## ✅ Validation Rules

| Field | Rule |
|-------|------|
| Patch Name | Required, non-blank |
| Year | Positive integer |
| Season | Required, use dropdown |
| Activity Type | Required, use dropdown |
| Description | Required, non-blank |
| Amount (Expense) | Required, must be > 0 |
| Yield Amount | Required, must be > 0 |
| Area Size | Optional, but if provided must be > 0 |
| Market Price | Optional, but if provided must be > 0 |
| Dates | Valid date, not future (except expectedHarvestDate) |

## 🎯 Common Tasks

### Task: Add a new Activity
1. User taps "Farm Activities" → "Add Activity" button
2. Screen shows `CreateActivityScreen`
3. User fills form with dropdowns and fields
4. Form validates before allowing submit
5. API call made with `FarmActivityRequest`
6. Success → navigate back to Activities list

### Task: Manage Patches
1. User taps "My Patches" 
2. Screen shows `PatchesScreen` with list
3. Can create new patch via dialog form
4. Can edit/delete existing patches
5. Patches used in other forms for linking

### Task: Record Expense with Recurring Option
1. User creates expense via `CreateExpenseScreen`
2. Checks "This is a recurring expense"
3. Selects frequency from dropdown
4. System will remind for recurring payments

### Task: Calculate Yield Efficiency
1. User enters yield amount and area harvested
2. System auto-calculates yield per unit area
3. Shown in metric card (read-only)
4. Helps user see crop efficiency

## 🔗 File Locations

```
📁 data/model/
   ├── RecordsEnums.kt              ← All enums
   └── RecordsKeepingModels.kt      ← All DTOs

📁 ui/components/records/
   ├── RecordsDropdowns.kt          ← Dropdown components
   └── FormComponents.kt            ← Form field components

📁 ui/screens/records/
   ├── RecordsScreen.kt            ← Main dashboard
   ├── PatchesScreen.kt            ← Patch management
   ├── CreateActivityScreen.kt     ← Activity form
   ├── CreateExpenseScreen.kt      ← Expense form
   └── CreateYieldScreen.kt        ← Yield form
```

## 💡 Pro Tips

✅ Always validate DTOs with `isValid()` before API calls
✅ Use patch selector dropdowns to link records to patches
✅ Leverage auto-calculation cards for user feedback
✅ Use collapsible advanced sections to keep forms lean
✅ Provide helpful placeholders in text fields
✅ Show error messages inline with fields
✅ Use Material 3 colors from theme for consistency
✅ All date pickers support manual day/month/year entry
✅ Dropdowns efficiently handle large option lists

## 🐛 Troubleshooting

**Issue**: Dropdown not showing selected value
- **Solution**: Ensure enum is not null, use default in remember state

**Issue**: Form won't submit
- **Solution**: Check `isValid()` method on DTO, ensure all required fields filled

**Issue**: Date picker not working
- **Solution**: Ensure valid day/month/year combination (e.g., no Feb 30)

**Issue**: Patch dropdown empty
- **Solution**: Ensure patches list is loaded from API before rendering screen

---

**Last Updated**: November 17, 2025  
**Version**: 1.0  
**Status**: ✅ Ready for Production
