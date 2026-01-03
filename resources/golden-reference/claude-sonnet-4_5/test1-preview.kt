@Preview
@Composable
private fun InterestsItemPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Android Development",
                following = false,
                topicImageUrl = "https://example.com/android.jpg",
                description = "Learn about the latest in Android development",
                onClick = {},
                onFollowButtonClick = {},
                isSelected = false,
            )
        }
    }
}

@Preview
@Composable
private fun InterestsItemLongNamePreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Advanced Machine Learning and Artificial Intelligence for Mobile Applications",
                following = false,
                topicImageUrl = "https://example.com/ml.jpg",
                description = "Explore AI and ML technologies",
                onClick = {},
                onFollowButtonClick = {},
                isSelected = false,
            )
        }
    }
}

@Preview
@Composable
private fun InterestsItemLongDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Kotlin",
                following = true,
                topicImageUrl = "https://example.com/kotlin.jpg",
                description = "Kotlin is a modern programming language that makes developers happier. It's concise, safe, interoperable with Java and other languages, and provides many ways to reuse code between multiple platforms for productive programming.",
                onClick = {},
                onFollowButtonClick = {},
                isSelected = false,
            )
        }
    }
}

@Preview
@Composable
private fun InterestsItemEmptyDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Jetpack Compose",
                following = false,
                topicImageUrl = "",
                description = "",
                onClick = {},
                onFollowButtonClick = {},
                isSelected = false,
            )
        }
    }
}

@Preview
@Composable
private fun InterestsItemSelectedPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "UI Design",
                following = true,
                topicImageUrl = "https://example.com/design.jpg",
                description = "Master the principles of user interface design",
                onClick = {},
                onFollowButtonClick = {},
                isSelected = true,
            )
        }
    }
}