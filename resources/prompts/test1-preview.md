Context: Android developer working with Jetpack Compose UI components

Task: Generate `@Preview` composables for the provided Composables below

Requirements:
Create exactly 5 Jetpack Compose Preview functions covering these scenarios:
1. Basic UI - Standard usage with typical content
2. Long name - Test text overflow handling for the title
3. Long description - Test text overflow handling for supporting text
4. Empty description - Test layout when description is absent
5. Selected state - Test visual appearance when card is selected

Constraints:
- Use realistic sample data
- Include meaningful preview names for easy identification
- Use the attached source code as reference
- The output code will be appended at the end of the attached source code

Expected output:
- Only valid Kotlin code in plain text format — no comments, explanations, markdown, backticks, or any other non-code content.

Source Code:
```kotlin
package com.google.samples.apps.nowinandroid.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.ui.R.string

@Composable
fun InterestsItem(
    name: String,
    following: Boolean,
    topicImageUrl: String,
    onClick: () -> Unit,
    onFollowButtonClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    description: String = "",
    isSelected: Boolean = false,
) {
    ListItem(
        leadingContent = {
            InterestsIcon(topicImageUrl, iconModifier.size(48.dp))
        },
        headlineContent = {
            Text(text = name)
        },
        supportingContent = {
            Text(text = description)
        },
        trailingContent = {
            NiaIconToggleButton(
                checked = following,
                onCheckedChange = onFollowButtonClick,
                icon = {
                    Icon(
                        imageVector = NiaIcons.Add,
                        contentDescription = stringResource(
                            id = string.core_ui_interests_card_follow_button_content_desc,
                        ),
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = NiaIcons.Check,
                        contentDescription = stringResource(
                            id = string.core_ui_interests_card_unfollow_button_content_desc,
                        ),
                    )
                },
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                Color.Transparent
            },
        ),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                selected = isSelected
            }
            .clickable(enabled = true, onClick = onClick),
    )
}

@Composable
private fun InterestsIcon(topicImageUrl: String, modifier: Modifier = Modifier) {
    if (topicImageUrl.isEmpty()) {
        Icon(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            imageVector = NiaIcons.Person,
            // decorative image
            contentDescription = null,
        )
    } else {
        DynamicAsyncImage(
            imageUrl = topicImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}
```