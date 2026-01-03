```kotlin
@Preview(name = "Basic UI")
@Composable
fun InterestsItemPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Android Development",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "Learn about the latest trends in Android development",
            )
        }
    }
}

@Preview(name = "Long name")
@Composable
fun InterestsItemPreviewLongName() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "This is a Very Long Title That Should Overflow and Be Truncated",
                following = true,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "Short description",
            )
        }
    }
}

@Preview(name = "Long description")
@Composable
fun InterestsItemPreviewLongDescription() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Android Development",
                following = true,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "This is a very long description that should overflow and be truncated to fit within the available space in the UI component.",
            )
        }
    }
}

@Preview(name = "Empty description")
@Composable
fun InterestsItemPreviewEmptyDescription() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Android Development",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "",
            )
        }
    }
}

@Preview(name = "Selected state")
@Composable
fun InterestsItemPreviewSelected() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Android Development",
                following = true,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "Learn about the latest trends in Android development",
                isSelected = true,
            )
        }
    }
}
```