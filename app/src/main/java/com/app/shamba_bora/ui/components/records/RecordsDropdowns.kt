package com.app.shamba_bora.ui.components.records

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.shamba_bora.data.model.*

/**
 * Generic Dropdown component for Records Keeping forms
 */
@Composable
fun <T> RecordsDropdown(
    label: String,
    selectedValue: T?,
    options: List<T>,
    onValueChange: (T) -> Unit,
    displayText: (T) -> String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = true,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { if (enabled) expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = enabled,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedValue != null) displayText(selectedValue) else "Select $label",
                    color = if (selectedValue != null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(displayText(option)) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Season Dropdown
 */
@Composable
fun SeasonDropdown(
    selectedSeason: Season,
    onSeasonChange: (Season) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Season",
        selectedValue = selectedSeason,
        options = Season.values().toList(),
        onValueChange = onSeasonChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Area Unit Dropdown
 */
@Composable
fun AreaUnitDropdown(
    selectedUnit: AreaUnit,
    onUnitChange: (AreaUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Area Unit",
        selectedValue = selectedUnit,
        options = AreaUnit.values().toList(),
        onValueChange = onUnitChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Activity Type Dropdown
 */
@Composable
fun ActivityTypeDropdown(
    selectedType: ActivityType,
    onTypeChange: (ActivityType) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Activity Type",
        selectedValue = selectedType,
        options = ActivityType.values().toList(),
        onValueChange = onTypeChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Expense Category Dropdown
 */
@Composable
fun ExpenseCategoryDropdown(
    selectedCategory: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Category",
        selectedValue = selectedCategory,
        options = ExpenseCategory.values().toList(),
        onValueChange = onCategoryChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Growth Stage Dropdown
 */
@Composable
fun GrowthStageDropdown(
    selectedStage: GrowthStage,
    onStageChange: (GrowthStage) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Growth Stage",
        selectedValue = selectedStage,
        options = GrowthStage.values().toList(),
        onValueChange = onStageChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Payment Method Dropdown
 */
@Composable
fun PaymentMethodDropdown(
    selectedMethod: PaymentMethod,
    onMethodChange: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Payment Method",
        selectedValue = selectedMethod,
        options = PaymentMethod.values().toList(),
        onValueChange = onMethodChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Weather Condition Dropdown
 */
@Composable
fun WeatherConditionDropdown(
    selectedCondition: WeatherCondition,
    onConditionChange: (WeatherCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Weather",
        selectedValue = selectedCondition,
        options = WeatherCondition.values().toList(),
        onValueChange = onConditionChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Soil Condition Dropdown
 */
@Composable
fun SoilConditionDropdown(
    selectedCondition: SoilCondition,
    onConditionChange: (SoilCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Soil Condition",
        selectedValue = selectedCondition,
        options = SoilCondition.values().toList(),
        onValueChange = onConditionChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Yield Unit Dropdown
 */
@Composable
fun YieldUnitDropdown(
    selectedUnit: YieldUnit,
    onUnitChange: (YieldUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Yield Unit",
        selectedValue = selectedUnit,
        options = YieldUnit.values().toList(),
        onValueChange = onUnitChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Quality Grade Dropdown
 */
@Composable
fun QualityGradeDropdown(
    selectedGrade: QualityGrade,
    onGradeChange: (QualityGrade) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Quality Grade",
        selectedValue = selectedGrade,
        options = QualityGrade.values().toList(),
        onValueChange = onGradeChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Input Type Dropdown
 */
@Composable
fun InputTypeDropdown(
    selectedType: InputType,
    onTypeChange: (InputType) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Input Type",
        selectedValue = selectedType,
        options = InputType.values().toList(),
        onValueChange = onTypeChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Recurring Frequency Dropdown
 */
@Composable
fun RecurringFrequencyDropdown(
    selectedFrequency: RecurringFrequency,
    onFrequencyChange: (RecurringFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    RecordsDropdown(
        label = "Frequency",
        selectedValue = selectedFrequency,
        options = RecurringFrequency.values().toList(),
        onValueChange = onFrequencyChange,
        displayText = { it.displayName },
        modifier = modifier
    )
}

/**
 * Patch Selector Dropdown
 */
@Composable
fun PatchSelectorDropdown(
    patches: List<MaizePatchDTO>,
    selectedPatchId: Long?,
    onPatchSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    val selectedPatch = patches.firstOrNull { it.id == selectedPatchId }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedPatch?.name ?: "Select Patch",
                    color = if (selectedPatch != null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            patches.forEach { patch ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = patch.name,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${patch.year} - ${patch.season}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        patch.id?.let { onPatchSelect(it) }
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
