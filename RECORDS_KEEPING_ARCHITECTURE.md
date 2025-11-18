# Records Keeping UI - Architecture & Data Flow Diagram

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        RECORDS KEEPING SYSTEM                        │
└─────────────────────────────────────────────────────────────────────┘

                            ┌──────────────────┐
                            │  RecordsScreen   │ (Main Dashboard)
                            │   (Updated)      │
                            └────────┬─────────┘
                                     │
                  ┌──────────────────┼──────────────────┐
                  │                  │                  │
                  ▼                  ▼                  ▼
            ┌──────────┐        ┌──────────┐      ┌──────────┐
            │ Patches  │        │Activities│      │Expenses  │
            │ Screen   │        │ Screen   │      │ Screen   │
            └────┬─────┘        └────┬─────┘      └────┬─────┘
                 │                   │                  │
                 ▼                   ▼                  ▼
          ┌────────────┐      ┌──────────────┐  ┌──────────────┐
          │Patch List  │      │Create        │  │Create        │
          ├────────────┤      │Activity      │  │Expense       │
          │- View all  │      │Screen        │  │Screen        │
          │- Edit      │      └──────────────┘  └──────────────┘
          │- Delete    │
          │- Create    │           ▼
          └────────────┘      ┌────────────────┐
                 │            │ Yields Screen  │
                 │            └────────┬───────┘
                 │                     │
                 │                     ▼
                 │            ┌──────────────────┐
                 │            │Create Yield      │
                 │            │Screen            │
                 │            └──────────────────┘
                 │
                 └──────────────┬────────────────┘
                                ▼
                        ┌──────────────────┐
                        │  API Integration │
                        └──────────────────┘
```

## 📊 Data Flow - Creating a Record

```
USER INTERACTION
      │
      ▼
┌─────────────────────────┐
│   User Form Input       │
│   (All Composables)     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Form State Management                  │
│  - var description by remember {...}    │
│  - var category by remember {...}       │
│  - var selectedPatchId by remember {...}│
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Validation                             │
│  - FormFieldLabel (required marker)     │
│  - Error messages display               │
│  - Button enable/disable logic          │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  DTO Creation                           │
│  FarmExpenseRequest(                    │
│    category = "FERTILIZER",             │
│    amount = 2500.0,                     │
│    patchId = 3,                         │
│    ...                                  │
│  )                                      │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Validation: isValid()                  │
│  - amount > 0?                          │
│  - category not empty?                  │
│  - description not empty?               │
└────────┬────────────────────────────────┘
         │ YES ✓
         ▼
┌─────────────────────────────────────────┐
│  API Call (ViewModel)                   │
│  POST /api/farm-expenses                │
│  Body: FarmExpenseRequest               │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Response Handling                      │
│  - Success: Navigate back               │
│  - Error: Show error message            │
│  - Loading: Show progress               │
└────────┬────────────────────────────────┘
         │
         ▼
    COMPLETE ✓
```

## 🔄 Component Hierarchy

```
RecordsScreen
│
├── RecordCategoryCard
│   ├── Surface (icon background)
│   ├── Icon
│   ├── Text (title)
│   ├── Text (description)
│   ├── Text (count)
│   └── Icon (arrow)
│
├── StatCard
│   ├── Icon
│   ├── Text (value)
│   └── Text (label)
│
└── [Navigate to sub-screens]
    │
    ├── PatchesScreen
    │   ├── PatchCard
    │   │   ├── Surface
    │   │   ├── Text (name)
    │   │   ├── PatchBadge
    │   │   ├── IconButton (edit)
    │   │   └── IconButton (delete)
    │   │
    │   └── CreatePatchDialog
    │       ├── FormTextField
    │       ├── SeasonDropdown
    │       ├── AreaUnitDropdown
    │       ├── FormDateField
    │       └── FormSubmitButton
    │
    ├── CreateActivityScreen
    │   ├── FormSection ("Basic Information")
    │   │   ├── ActivityTypeDropdown
    │   │   ├── FormDateField
    │   │   └── FormTextField
    │   │
    │   ├── FormSection ("Products & Inputs")
    │   │   ├── FormTextField
    │   │   ├── FormNumberField
    │   │   └── ...
    │   │
    │   ├── AdvancedOptionsSection
    │   │   └── [Collapsible content]
    │   │
    │   └── FormSubmitButton
    │
    ├── CreateExpenseScreen
    │   ├── FormSection ("Expense Information")
    │   │   ├── ExpenseCategoryDropdown
    │   │   ├── FormTextField
    │   │   └── FormNumberField
    │   │
    │   ├── FormSection ("Payment Information")
    │   │   ├── PaymentMethodDropdown
    │   │   └── GrowthStageDropdown
    │   │
    │   ├── AdvancedOptionsSection
    │   │   └── [Collapsible content]
    │   │
    │   └── FormSubmitButton
    │
    └── CreateYieldScreen
        ├── FormSection ("Harvest Information")
        │   ├── FormDateField
        │   ├── FormNumberField
        │   └── YieldUnitDropdown
        │
        ├── FormSection ("Harvest Area")
        │   ├── FormNumberField
        │   └── Card (auto-calculated yield)
        │
        ├── FormSection ("Market Information")
        │   ├── FormNumberField
        │   └── Card (auto-calculated revenue)
        │
        ├── AdvancedOptionsSection
        │   └── [Collapsible content]
        │
        └── FormSubmitButton
```

## 📦 Data Model Relationships

```
┌──────────────────────────────────────────────────────────┐
│                    MaizePatchDTO                          │
│  - id: Long                                              │
│  - year: Int                                             │
│  - season: String (Season enum)                          │
│  - name: String                                          │
│  - area: Double                                          │
│  - areaUnit: String (AreaUnit enum)                      │
│  - plantingDate: LocalDate                               │
│  - expectedHarvestDate: LocalDate                        │
│  - location: String                                      │
│  - notes: String                                         │
└──────────────────────────────┬───────────────────────────┘
                               │
                   ┌───────────┴───────────┐
                   │                       │
                   ▼                       ▼
    ┌────────────────────────┐  ┌────────────────────────┐
    │FarmActivityRequest     │  │FarmExpenseRequest      │
    ├────────────────────────┤  ├────────────────────────┤
    │- patchId: Long ─────────→ │- patchId: Long ────────┐
    │- activityType: String  │  │- category: String     │
    │- activityDate: Date    │  │- expenseDate: Date    │
    │- description: String   │  │- amount: Double       │
    │- weatherConditions     │  │- paymentMethod        │
    │- soilConditions        │  │- growthStage: String  │
    │- cost: Double          │  │- isRecurring: Boolean │
    │- seedVarietyName       │  └────────┬───────────────┘
    │- fertilizerProductName │           │
    └──────────┬─────────────┘           │
               │                         │
               ▼                         ▼
    ┌────────────────────────┐  ┌────────────────────────┐
    │FarmActivityResponse    │  │FarmExpenseResponse    │
    ├────────────────────────┤  ├────────────────────────┤
    │+ id: Long              │  │+ id: Long              │
    │+ patchName: String     │  │+ patchName: String    │
    │+ createdAt: DateTime   │  │+ createdAt: DateTime  │
    │+ yieldTrend: String    │  │+ updatedAt: DateTime  │
    └────────────────────────┘  └────────────────────────┘
                                          │
                                          │
                                ┌─────────▼──────────┐
                                │YieldRecordRequest  │
                                ├────────────────────┤
                                │- patchId: Long ────┘
                                │- harvestDate: Date
                                │- yieldAmount: Double
                                │- unit: String
                                │- areaHarvested: Double
                                │- marketPrice: Double
                                │- qualityGrade: String
                                └────────┬───────────┘
                                         │
                                         ▼
                                ┌─────────────────────┐
                                │YieldRecordResponse  │
                                ├─────────────────────┤
                                │+ id: Long           │
                                │+ patchName: String │
                                │+ totalRevenue:     │
                                │  Double             │
                                │+ costPerKgProduced  │
                                │+ profitPerKg        │
                                └─────────────────────┘
```

## 🎨 Enum Relationships

```
┌─────────────────────────────────────────────────────────┐
│                    ENUMS (RecordsEnums.kt)              │
└─────────────────────────────────────────────────────────┘

Used in Patches:
  Season ──────────────► (LONG_RAIN, SHORT_RAIN, DRY)
  AreaUnit ────────────► (HA, ACRES, M2)

Used in Activities:
  ActivityType ────────► (20+ types: PLANTING, WEEDING, ...)
  WeatherCondition ───► (SUNNY, RAINY, CLOUDY, WINDY, MIXED)
  SoilCondition ──────► (WET, DRY, WELL_DRAINED, MUDDY)
  AreaUnit ───────────► (HA, ACRES, M2)

Used in Expenses:
  ExpenseCategory ────► (SEEDS, FERTILIZER, PESTICIDES, ...)
  PaymentMethod ──────► (CASH, MPESA, CHEQUE, TRANSFER)
  GrowthStage ────────► (PRE_PLANTING, PLANTING, ...)
  RecurringFrequency ─► (WEEKLY, MONTHLY, QUARTERLY)

Used in Yields:
  YieldUnit ──────────► (KG, BAGS, TONS, LITERS)
  QualityGrade ──────► (GRADE_A, GRADE_B, GRADE_C, ...)
```

## 🔌 API Integration Points

```
┌──────────────────────────────────────────────────┐
│              BACKEND API ENDPOINTS                │
└──────────────────────────────────────────────────┘

PATCHES:
  POST   /api/patches
  GET    /api/patches
  GET    /api/patches/{id}
  PUT    /api/patches/{id}
  DELETE /api/patches/{id}
         ↑
         └─── MaizePatchDTO

ACTIVITIES:
  POST   /api/farm-activities
  GET    /api/farm-activities
  GET    /api/farm-activities/{id}
  PUT    /api/farm-activities/{id}
  DELETE /api/farm-activities/{id}
         ↑
         └─── FarmActivityRequest/Response

EXPENSES:
  POST   /api/farm-expenses
  GET    /api/farm-expenses
  GET    /api/farm-expenses/{id}
  PUT    /api/farm-expenses/{id}
  DELETE /api/farm-expenses/{id}
         ↑
         └─── FarmExpenseRequest/Response

YIELDS:
  POST   /api/yield-records
  GET    /api/yield-records
  GET    /api/yield-records/{id}
  PUT    /api/yield-records/{id}
  DELETE /api/yield-records/{id}
         ↑
         └─── YieldRecordRequest/Response

ANALYTICS:
  GET    /api/farm-analytics/patches/{patchId}/summary
         ↑
         └─── PatchSummaryDTO
```

## 🎯 User Journey Map

```
START
  │
  ▼
┌─────────────────────────────┐
│   RecordsScreen (Dashboard) │  ← View overview, statistics
└──────────┬──────────────────┘
           │
      ┌────┴────┬────────┬──────────┐
      │          │        │          │
      ▼          ▼        ▼          ▼
   PATCHES   ACTIVITIES EXPENSES   YIELDS
      │          │        │          │
      ▼          ▼        ▼          ▼
  Manage    Record New  Record New Record New
  Patches   Activity    Expense    Yield
      │          │        │          │
      ├─ Create  ├─ Form  ├─ Form   ├─ Form
      ├─ View    ├─ Link  ├─ Link   ├─ Link
      ├─ Edit    │ Patch  │ Patch   │ Patch
      └─ Delete  │        │         │
                 ├─ Cost  ├─ Amount ├─ Revenue
                 │        │         │   (auto)
                 ├─ Verify├─ Verify ├─ Verify
                 │        │         │
                 └─ Save  └─ Save   └─ Save
                    │        │          │
                    └────┬───┴──────────┘
                         ▼
                    ┌──────────────┐
                    │ Success ✓    │
                    │ Navigate     │
                    │ back         │
                    └──────────────┘
```

## 🗂️ File Organization

```
app/src/main/java/com/app/shamba_bora/
│
├── data/model/
│   ├── RecordsEnums.kt
│   │   └── 12 Enums for all dropdowns
│   │
│   └── RecordsKeepingModels.kt
│       ├── MaizePatchDTO
│       ├── FarmActivityRequest/Response
│       ├── FarmExpenseRequest/Response
│       ├── YieldRecordRequest/Response
│       └── PatchSummaryDTO
│
├── ui/components/records/
│   ├── RecordsDropdowns.kt
│   │   ├── Generic <T> RecordsDropdown
│   │   ├── SeasonDropdown
│   │   ├── ActivityTypeDropdown
│   │   ├── ExpenseCategoryDropdown
│   │   ├── PaymentMethodDropdown
│   │   ├── PatchSelectorDropdown
│   │   └── 6 more specialized dropdowns
│   │
│   └── FormComponents.kt
│       ├── FormFieldLabel
│       ├── FormTextField
│       ├── FormNumberField
│       ├── FormDateField
│       ├── DatePickerCard
│       ├── FormSection
│       ├── FormSubmitButton
│       └── AdvancedOptionsSection
│
└── ui/screens/records/
    ├── RecordsScreen.kt (UPDATED)
    │   ├── RecordsScreen (Composable)
    │   ├── RecordCategoryCard
    │   ├── StatCard
    │   └── RecordCategoriesSection
    │
    ├── PatchesScreen.kt
    │   ├── PatchesScreen (Composable)
    │   ├── PatchCard
    │   ├── PatchBadge
    │   └── CreatePatchDialog
    │
    ├── CreateActivityScreen.kt
    │   └── CreateActivityScreen (Composable)
    │
    ├── CreateExpenseScreen.kt
    │   └── CreateExpenseScreen (Composable)
    │
    └── CreateYieldScreen.kt
        └── CreateYieldScreen (Composable)
```

---

**Last Updated**: November 17, 2025  
**Architecture Version**: 1.0  
**Status**: ✅ READY FOR IMPLEMENTATION
