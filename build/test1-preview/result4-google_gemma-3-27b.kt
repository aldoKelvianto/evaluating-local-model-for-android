```kotlin
@Preview(name = "Basic UI")
@Composable
fun InterestsItemBasic() {
    NiaTheme {
        InterestsItem(
            name = "Kotlin",
            following = true,
            topicImageUrl = "https://via.placeholder.com/48",
            onClick = {},
            onFollowButtonClick = {}
        )
    }
}

@Preview(name = "Long name")
@Composable
fun InterestsItemLongName() {
    NiaTheme {
        InterestsItem(
            name = "This is a very long interest name to test text overflow",
            following = false,
            topicImageUrl = "https://via.placeholder.com/48",
            onClick = {},
            onFollowButtonClick = {}
        )
    }
}

@Preview(name = "Long description")
@Composable
fun InterestsItemLongDescription() {
    NiaTheme {
        InterestsItem(
            name = "Kotlin",
            following = true,
            topicImageUrl = "https://via.placeholder.com/48",
            onClick = {},
            onFollowButtonClick = {},
            description = "This is a very long description to test text overflow and wrapping within the card layout."
        )
    }
}

@Preview(name = "Empty description")
@Composable
fun InterestsItemEmptyDescription() {
    NiaTheme {
        InterestsItem(
            name = "Kotlin",
            following = false,
            topicImageUrl = "https://via.placeholder.com/48",
            onClick = {},
            onFollowButtonClick = {}
        )
    }
}

@Preview(name = "Selected state")
@Composable
fun InterestsItemSelectedState() {
    NiaTheme {
        InterestsItem(
            name = "Kotlin",
            following = true,
            topicImageUrl = "https://via.placeholder.com/48",
            onClick = {},
            onFollowButtonClick = {},
            isSelected = true
        )
    }
}
```