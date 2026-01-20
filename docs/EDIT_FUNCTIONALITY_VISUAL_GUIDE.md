# Edit Functionality - Quick Visual Guide

## User Experience Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Activity Detail Screen                   │
│                                                             │
│  ← Activity Details        ✏️ [Edit] 🔔 🗑️                  │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│  🌾 Activity Information                                    │
│  ├─ Activity Type: Planting                               │
│  ├─ Crop Type: Maize                                      │
│  ├─ Activity Date: 2024-12-08                             │
│  └─ Description: Spring planting season                   │
│                                                             │
│  Reminders:                                                │
│  ├─ Time to water the crops (Dec 15, 2024)               │
│  └─ Apply fertilizer (Dec 22, 2024)                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                              ↓
                        [User clicks ✏️ Edit]
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Edit Activity Screen                     │
│                                                             │
│  ← Edit Activity        (Back button)                      │
│  ─────────────────────────────────────────────────────────  │
│     Update farm activity details                           │
│                                                             │
│  Activity Type: [Dropdown with current selection]         │
│  Crop Type: Maize                                          │
│  Activity Date: 2024-12-08 [Date picker]                  │
│  Description: Spring planting season [Text field]         │
│  Area Size: 5.0 [Number field] HA                         │
│  Product Used: [Text field]                               │
│  Weather Conditions: [Dropdown]                           │
│  Soil Conditions: [Dropdown]                              │
│  Cost: 15000 [Number field]                               │
│  Labor Hours: 8 [Number field]                            │
│  Notes: [Text area]                                        │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│  [Cancel]              [Update Activity]                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                              ↓
            [User modifies fields and clicks Update]
                              ↓
                    [Loading indicator shown]
                              ↓
         [On Success: Returns to Detail Screen]
         [On Error: Shows error message with retry]
```

---

## Detail Screens with Edit Buttons

### 1. Activity Detail Screen
```
Top App Bar:
[←] Activity Details          [✏️] [🔔] [🗑️]
                             │
                             └─→ Clicks edit to navigate to
                                 EditActivityScreen with activityId
```

### 2. Expense Detail Screen
```
Top App Bar:
[←] Expense Details           [✏️] [🗑️]
                             │
                             └─→ Clicks edit to navigate to
                                 EditExpenseScreen with expenseId
```

### 3. Yield Detail Screen
```
Top App Bar:
[←] Yield Details             [✏️] [🗑️]
                             │
                             └─→ Clicks edit to navigate to
                                 EditYieldScreen with yieldId
```

### 4. Patch Detail Screen
```
Top App Bar:
[←] Patch Name                [✏️]
                             │
                             └─→ Clicks edit to navigate to
                                 EditPatchScreen with patchId
```

---

## Navigation Architecture

```
DetailScreen (View Mode)
    ↓
    onNavigateToEdit(id) callback triggered
    ↓
    AppNavHost routes to EditScreen
    ↓
EditScreenWrapper
    ├─ Loads existing record from ViewModel
    ├─ Shows loading state while fetching
    └─ Renders EditScreen with pre-populated data
    ↓
EditScreen (Edit Mode)
    ├─ User modifies fields
    ├─ Clicks "Update" button
    └─ Calls onUpdateRecord(updatedData)
    ↓
ViewModel.updateRecord()
    ├─ Makes API call to update
    ├─ Shows loading state
    └─ On success: triggers Navigation back
    ↓
DetailScreenWrapper
    └─ Reloads data and displays updated information
```

---

## Key Features Implemented

### ✏️ Edit Icons
- Located in top app bar of each detail screen
- Uses Material Design Edit icon
- Placed between other action icons

### 📝 Pre-populated Forms
All edit forms show:
- Current values from database
- Properly formatted dates and numbers
- Same layout as create screens

### ⏳ Loading States
- Progress indicator shown during submission
- Buttons disabled while loading
- Prevents duplicate submissions

### ❌ Error Handling
- Error messages displayed to user
- Retry option provided
- Graceful error states

### 🔙 Navigation
- Type-safe routing with sealed classes
- Proper back stack management
- Maintains navigation history

---

## Code Structure

```
UI Screens:
├── ActivityDetailScreen.kt (Added onNavigateToEdit)
├── ExpenseDetailScreen.kt (Added onNavigateToEdit)
├── YieldDetailScreen.kt (Added onNavigateToEdit)
├── PatchDetailScreen.kt (Added onNavigateToEdit)
└── NEW: EditActivityScreen.kt
    NEW: EditExpenseScreen.kt
    NEW: EditYieldScreen.kt
    NEW: EditPatchScreen.kt

Navigation:
├── Screen.kt (Added 4 edit route definitions)
└── AppNavHost.kt (Added 4 edit route compositions)

ViewModels (Expected):
├── FarmActivityViewModel (needs updateActivity())
├── FarmExpenseViewModel (needs updateExpense())
├── YieldRecordViewModel (needs updateYieldRecord())
└── PatchViewModel (needs updatePatch())
```

---

## Action Buttons Layout

```
Edit Screens have consistent button layout:

┌─────────────────────────────────────────┐
│                                         │
│              [Form Fields]              │
│                                         │
├─────────────────────────────────────────┤
│  [Cancel]        [Update Record]        │
└─────────────────────────────────────────┘
```

- **Cancel Button**: OutlinedButton, pops back stack
- **Update Button**: Primary Button, submits data
- Both buttons disabled during loading

---

## Testing Scenarios

### ✅ Success Flow
1. Open detail screen (Activity/Expense/Yield/Patch)
2. Click edit icon
3. Form shows with pre-filled data
4. Modify one or more fields
5. Click Update
6. See loading indicator
7. Return to detail screen with updated data

### ✅ Cancel Flow
1. Open detail screen
2. Click edit icon
3. Modify fields
4. Click Cancel
5. Return to detail screen (data unchanged)

### ✅ Error Handling
1. Open detail screen
2. Click edit icon
3. Network error during load
4. See error message with retry button
5. Click retry to reload

---

## Implementation Notes

**Files Modified:** 6
- ActivityDetailScreen.kt
- ExpenseDetailScreen.kt
- YieldDetailScreen.kt
- PatchDetailScreen.kt
- Screen.kt
- AppNavHost.kt

**Files Created:** 4
- EditActivityScreen.kt
- EditExpenseScreen.kt
- EditYieldScreen.kt
- EditPatchScreen.kt

**Routes Added:** 4
- EditActivity
- EditExpense
- EditYield
- EditPatch

**Compilation Status:** ✅ No errors

