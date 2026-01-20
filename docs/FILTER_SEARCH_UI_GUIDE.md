# Filter & Search UI/UX Guide

## Visual Layout

### Activities, Yields & Expenses Screens

```
┌─────────────────────────────────────────────┐
│  ← Back                 Farm Activities      │  ← TopBar with title
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  🔍 Search activities...               ✕    │  ← Search Field
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  ☰ Filter by Patch                    ✕    │  ← Filter Button
└─────────────────────────────────────────────┘     (shows "Patch: X" when selected)

        ┌─────────────────────────────────┐
        │ ○ Patch 1 - Main Field          │
        │ ○ Patch 2 - Secondary Field     │  ← Patch Filter
        │ ○ Patch 3 - Test Area           │     Dropdown
        │ ─────────────────────────────── │
        │ ○ Clear Filter                  │
        └─────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  📊  Total Activities: 5                    │  ← Info Card
│     Track all your farming operations      │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  🚜 PLANTING                          ►     │  ← Activity Card 1
│     Maize crop                              │
│     📅 2025-11-17                          │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  🌾 HARVESTING                        ►     │  ← Activity Card 2
│     Maize crop                              │
│     📅 2025-11-18                          │
└─────────────────────────────────────────────┘

                    ...

┌─────────────────────────────────────────────┐
│                                         ➕  │  ← FAB (Add button)
└─────────────────────────────────────────────┘
```

---

## Interaction Flow

### Search Flow
```
User types in search
         ↓
Real-time filtering applied
         ↓
Results update instantly
         ↓
Matching records displayed
         ↓
User taps X to clear search
         ↓
All records shown again
```

### Patch Filter Flow
```
User taps "Filter by Patch"
         ↓
Dropdown expands showing patches
         ↓
User selects a patch
         ↓
Dropdown closes
         ↓
Records filtered to selected patch
         ↓
Filter button shows "Patch: X"
         ↓
User taps X to clear filter
         ↓
All records shown again
```

### Combined Filter Flow
```
Search for keyword + Select patch
         ↓
Both filters applied simultaneously
         ↓
Only records matching both criteria shown
         ↓
User can adjust search or patch independently
         ↓
Results update in real-time
```

---

## Search Field States

### Empty State (Initial)
```
┌─────────────────────────────────────────────┐
│  🔍 Search activities...                    │
└─────────────────────────────────────────────┘
    ↑ Placeholder text visible
```

### Active State (User Typing)
```
┌─────────────────────────────────────────────┐
│  🔍 fertiliser                         ✕    │
└─────────────────────────────────────────────┘
    ↑ Clear button appears
```

### Results
```
Results filtered to show only matching records
Example: User typed "fertiliser"
Shows: Fertiliser-related activities, yields, or expenses
```

---

## Filter Button States

### No Filter Selected
```
┌─────────────────────────────────────────────┐
│  ☰ Filter by Patch                         │
└─────────────────────────────────────────────┘
    ↑ Default state
```

### Filter Selected
```
┌─────────────────────────────────────────────┐
│  ☰ Patch: Patch 1                     ✕    │
└─────────────────────────────────────────────┘
    ↑ Shows selected patch     ↑ Clear button appears
```

---

## Filter Dropdown States

### Closed
```
┌─────────────────────────────────────────────┐
│  ☰ Filter by Patch                         │  ← User taps here
└─────────────────────────────────────────────┘
```

### Open
```
┌─────────────────────────────────────────────┐
│  ☰ Filter by Patch                    ✕    │
├─────────────────────────────────────────────┤
│ ┌───────────────────────────────────────┐   │
│ │ ○ Patch 1 - Main Field              │   │  ← Available patches
│ │ ○ Patch 2 - Secondary Field         │   │
│ │ ○ Patch 3 - Test Area               │   │
│ │ ─────────────────────────────────── │   │
│ │ ○ Clear Filter                      │   │  ← Clear option
│ └───────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

### With Selection
```
┌─────────────────────────────────────────────┐
│ ┌───────────────────────────────────────┐   │
│ │ ○ Patch 1 - Main Field              │   │
│ │ ● Patch 2 - Secondary Field         │   │  ← Selected (filled radio)
│ │ ○ Patch 3 - Test Area               │   │  ← Highlighted background
│ │ ─────────────────────────────────── │   │
│ │ ○ Clear Filter                      │   │
│ └───────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

---

## Empty States

### No Data + No Filters
```
┌─────────────────────────────────────────────┐
│                                             │
│              ℹ️  (Large Icon)               │
│                                             │
│          No activities yet                  │
│                                             │
│      Tap + to add your first activity       │
│                                             │
└─────────────────────────────────────────────┘
```

### No Results + Filters Applied
```
┌─────────────────────────────────────────────┐
│                                             │
│              ℹ️  (Large Icon)               │
│                                             │
│         No activities found                 │
│                                             │
│   Try adjusting your search or filters      │
│                                             │
└─────────────────────────────────────────────┘
```

---

## Color Scheme

### Search Field
- **Border (Unfocused)**: `colorScheme.outline`
- **Border (Focused)**: `colorScheme.primary`
- **Icon**: `colorScheme.primary`
- **Clear Button**: `colorScheme.primary`

### Filter Button
- **Background**: Outlined style (transparent with border)
- **Text**: `colorScheme.onBackground`
- **Icon**: `colorScheme.primary`
- **Close Button**: `colorScheme.primary`

### Filter Dropdown
- **Background**: `colorScheme.surfaceVariant`
- **Selected Item Background**: `colorScheme.primary.copy(alpha = 0.2f)`
- **Radio Button**: `colorScheme.primary`

---

## Accessibility Features

✅ **Touch Targets**: All interactive elements are ≥48dp
✅ **Labels**: All buttons have descriptive text
✅ **Icons**: Used in combination with text
✅ **Color**: Not the only differentiator (also uses text and radio buttons)
✅ **Feedback**: Clear visual feedback for selections
✅ **Focus**: Proper focus handling in text fields

---

## Responsive Behavior

### Portrait Mode (Default)
- Search field: Full width
- Filter button: Full width
- Results: Full width with padding

### Landscape Mode
- Search field: Full width
- Filter button: Full width
- Results: Full width with adjusted padding

### Tablet Mode
- Same layout with larger touch targets
- Dropdown positioned properly

---

## Animation & Transitions

1. **Search Clear**: Instant appearance of X button
2. **Filter Dropdown**: Smooth expand/collapse
3. **Item Selection**: Subtle highlight
4. **Results**: Instant update (list recomposes)

---

## Best Practices

✅ Filters are additive (both search AND patch filter apply)
✅ Clear buttons are always visible when needed
✅ Empty states guide users appropriately
✅ No loading states needed (local filtering)
✅ Search is case-insensitive
✅ Patch names shown with IDs as fallback

---

## Performance Notes

⚡ Real-time filtering (no debounce needed)
⚡ In-memory filtering (fast)
⚡ No network calls for filtering
⚡ Smooth UI updates
⚡ Efficient state management

