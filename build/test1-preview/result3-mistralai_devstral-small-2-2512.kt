@Preview(name = "Basic UI - Standard usage with typical content")
@Composable
fun InterestsItemPreview_Basic() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Technology",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "Explore the latest in tech innovations and trends"
            )
        }
    }
}

@Preview(name = "Long name - Test text overflow handling for the title")
@Composable
fun InterestsItemPreview_LongName() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "This is a very long interest name that should test how the text overflows in the UI component",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "Description"
            )
        }
    }
}

@Preview(name = "Long description - Test text overflow handling for supporting text")
@Composable
fun InterestsItemPreview_LongDescription() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Science",
                following = true,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "This is a very long description that should test how the supporting text overflows in the UI component and wraps properly according to the available space"
            )
        }
    }
}

@Preview(name = "Empty description - Test layout when description is absent")
@Composable
fun InterestsItemPreview_EmptyDescription() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Art",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(name = "Selected state - Test visual appearance when card is selected")
@Composable
fun InterestsItemPreview_Selected() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Music",
                following = true,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {},
                description = "Explore various music genres and artists",
                isSelected = true
            )
        }
    }
}