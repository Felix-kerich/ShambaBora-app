# Patch Detail Screen - Analytics Integration

## Overview
Enhanced the `PatchDetailScreen.kt` with a comprehensive "View Analytics" feature that displays detailed patch-specific analytics in a beautiful modal dialog.

## Features Added

### 1. **View Analytics Button**
   - Located below the patch header card
   - Tertiary color scheme for visual distinction
   - Icon + Text layout with bold typography
   - Triggers the analytics modal
   - Auto-loads patch summary data

### 2. **Analytics Modal Dialog**
   - Responsive dialog that displays at 95% width
   - Smooth dismiss behavior
   - Click outside to close functionality
   - Maximum height of 600dp with scrolling

### 3. **Analytics Content Display**

#### **Header Section**
   - Patch name and analytics title
   - Close button for easy dismissal
   - Clean, professional layout

#### **Basic Information Pills**
   - Season and Year display
   - Crop type badge
   - Date range icon indicator

#### **Financial Summary Card**
   - **Total Revenue** - Green color (0xFF4CAF50)
   - **Total Expenses** - Red color (0xFFF44336)
   - **Net Profit** - Bold blue color (0xFF2196F3)
   - Divider separating total from net profit

#### **Yield & Production Section**
   - **Total Yield** - Cyan color (0xFF00BCD4)
     - Shows total harvest in kilograms
   - **Cost/Kg** - Orange color (0xFFFF9800)
     - Shows cost per kilogram produced

#### **Performance Metrics Section**
   - **Profit/Kg** - Purple color (0xFF9C27B0)
     - Revenue per kilogram of product
   - **ROI (Return on Investment)** - Pink color (0xFFE91E63)
     - Investment returns as percentage

#### **Farm Information**
   - Area measurement with unit (HA/Acres)
   - Conditionally displayed when available

#### **Activities Section**
   - List of farm activities performed
   - Green background with primary color text
   - Rounded corner chips design

#### **Expense Breakdown Section**
   - Detailed expense summaries
   - Light error container background
   - Grouped cost breakdowns (e.g., "LABOR: 3440.00")

### 4. **Loading States**
   - Circular progress indicator with "Loading Analytics..." text
   - Centered display with appropriate spacing

### 5. **Error Handling**
   - Error icon and message display
   - User-friendly error descriptions
   - Retry button for failed requests
   - Dismissal option

### 6. **Helper Composables**

#### **AnalyticsPill**
   - Icon + Label + Value layout
   - Primary container background color
   - Used for season, year, crop type display
   - Responsive and centered content

#### **AnalyticsRow**
   - Label and value in row format
   - Optional bold styling for important metrics
   - Customizable color coding
   - Used in financial summary

#### **AnalyticsCard**
   - Icon-less card version of pill
   - Color-coded background and text
   - Three-line layout (label + value)
   - Used for yield and performance metrics

## API Integration

### Endpoint
```
GET /api/farm-analytics/patches/{patchId}/summary
```

### Request
- Patch ID from the current patch being viewed
- Automatically loaded when "View Analytics" button is clicked

### Response Model (PatchSummaryDTO)
```kotlin
data class PatchSummaryDTO(
    val patchId: Long,
    val patchName: String,
    val year: Int,
    val season: String,
    val cropType: String,
    val area: Double?,
    val areaUnit: String,
    val totalExpenses: Double,
    val totalYield: Double,
    val totalRevenue: Double,
    val costPerKg: Double,
    val profit: Double,
    val profitPerKg: Double,
    val roiPercentage: Double,
    val activityTypes: List<String>,
    val inputSummaries: List<String>,
    val expenseSummaries: List<String>
)
```

## Data Flow

1. **User Action**: Clicks "View Analytics" button
2. **ViewModel Call**: `viewModel.loadPatchSummary(patch.id)` is triggered
3. **API Request**: Repository fetches data from `/api/farm-analytics/patches/{patchId}/summary`
4. **State Management**: `patchSummaryState` is updated with loading → success/error
5. **UI Update**: Modal displays appropriate content based on state

## Color Coding

| Metric | Color | Hex Code |
|--------|-------|----------|
| Revenue | Green | 0xFF4CAF50 |
| Expenses | Red | 0xFFF44336 |
| Profit | Blue | 0xFF2196F3 |
| Total Yield | Cyan | 0xFF00BCD4 |
| Cost/Kg | Orange | 0xFFFF9800 |
| Profit/Kg | Purple | 0xFF9C27B0 |
| ROI | Pink | 0xFFE91E63 |

## Layout Structure

```
PatchDetailScreen
├── TopAppBar (with back button)
├── LazyColumn
│   ├── PatchHeaderCard
│   ├── View Analytics Button ← NEW
│   ├── PatchInfoCard
│   ├── ActivitiesSection
│   ├── YieldsSection
│   └── ExpensesSection
└── PatchAnalyticsModal (Dialog) ← NEW
    └── PatchAnalyticsContent
        ├── Header with close button
        ├── Basic info pills
        ├── Financial summary card
        ├── Yield & production metrics
        ├── Performance metrics
        ├── Farm information
        ├── Activities list
        ├── Expense breakdown
        └── Close button
```

## Files Modified

- `/app/src/main/java/com/app/shamba_bora/ui/screens/records/PatchDetailScreen.kt`

## Usage Example

```kotlin
// In the patch detail screen
Button(
    onClick = {
        viewModel.loadPatchSummary(patch.id)
        showAnalyticsModal = true
    },
    // ... styling
) {
    Icon(Icons.Default.BarChart, ...)
    Text("View Analytics")
}
```

## State Management

```kotlin
// State variables
var showAnalyticsModal by remember { mutableStateOf(false) }
val patchSummaryState by viewModel.patchSummaryState.collectAsState()

// When modal is shown:
// - Modal triggers viewModel.loadPatchSummary(patchId)
// - ViewModel updates patchSummaryState with Resource<PatchSummaryDTO>
// - UI responds to state changes (Loading → Success/Error)
```

## Key Features

✅ **Responsive Design** - Adapts to different screen sizes  
✅ **Color-Coded Metrics** - Easy visual understanding of data  
✅ **Loading State** - User feedback during data fetch  
✅ **Error Handling** - User-friendly error messages with retry  
✅ **Smooth Animations** - Dialog transitions smoothly  
✅ **Accessibility** - Clear labels and readable text  
✅ **Professional UI** - Modern Material 3 design principles  
✅ **Easy Dismissal** - Multiple ways to close the modal  

## Testing Recommendations

1. ✓ Verify "View Analytics" button appears on patch detail screen
2. ✓ Test button click opens modal with loading state
3. ✓ Verify analytics data loads correctly from API
4. ✓ Test all metrics display with proper formatting
5. ✓ Verify color coding is applied correctly
6. ✓ Test error handling with invalid patch ID
7. ✓ Test modal dismiss functionality
8. ✓ Verify all composables render properly
9. ✓ Test modal on different screen sizes
10. ✓ Verify data updates when switching between patches

## Integration Notes

- Uses existing `PatchViewModel` from Hilt dependency injection
- Leverages existing `patchSummaryState` from ViewModel
- Compatible with existing patch detail screen flow
- No breaking changes to existing functionality
- Non-blocking modal allows dismissal anytime

