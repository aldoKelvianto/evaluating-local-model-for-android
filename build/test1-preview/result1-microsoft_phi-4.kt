```kotlin
@Preview(showBackground = true)
@Composable
fun PreviewInterestsItemBasic() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Kotlin",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInterestsItemLongName() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "This is a very long title that should test text overflow handling in the UI",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInterestsItemLongDescription() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Kotlin",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "This is a very long supporting text that should test text overflow handling in the UI. It provides additional context about the interest."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInterestsItemEmptyDescription() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Kotlin",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = ""
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInterestsItemSelected() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Kotlin",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                isSelected = true
            )
        }
    }
}
```