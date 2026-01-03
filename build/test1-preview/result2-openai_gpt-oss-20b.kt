@Preview(name = "Basic UI")
@Composable
fun InterestsItemPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Technology",
                following = false,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(name = "Long Name")
@Composable
fun InterestsItemLongNamePreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Artificial Intelligence and Machine Learning Research Community",
                following = true,
                topicImageUrl = "",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(name = "Long Description")
@Composable
fun InterestsItemLongDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Health",
                following = false,
                topicImageUrl = "",
                description = "Explore the latest advancements in medical science, wellness practices, and healthcare technology to stay informed about how we can improve our well-being and quality of life.",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(name = "Empty Description")
@Composable
fun InterestsItemEmptyDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Travel",
                following = true,
                topicImageUrl = "",
                description = "",
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}

@Preview(name = "Selected State")
@Composable
fun InterestsItemSelectedPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Music",
                following = false,
                topicImageUrl = "",
                isSelected = true,
                onClick = {},
                onFollowButtonClick = {}
            )
        }
    }
}