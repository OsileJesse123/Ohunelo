package com.jesse.ohunelo.util

enum class BottomSheetBehaviorStateWrapper {
    STATE_COLLAPSED,
    STATE_EXPANDED
}

enum class RecipeImageSize(val size: String) {
    SIZE1("90x90"),
    SIZE2("240x150"),
    SIZE3("312x150"),
    SIZE4("312x231"),
    SIZE5("480x360"),
    SIZE6("556x370"),
    SIZE7("636x393")
}

enum class IngredientImageSize(val size: String){
    SIZE1("100x100"),
    SIZE2("250x250"),
    SIZE3("500x500"),
}

enum class NotificationType{
    FOOD_JOKE, FOOD_TRIVIA
}

enum class RecipeViewHolderType {
    RANDOM_RECIPE, RECIPE_BY_CATEGORY
}

enum class UserType(val userType: String){
    EMAIL_PASSWORD("email/password"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    TWITTER("twitter");

    companion object {
        fun getUserType(userTypeText: String?): UserType?{
            return when(userTypeText){
                EMAIL_PASSWORD.userType -> EMAIL_PASSWORD
                GOOGLE.userType -> GOOGLE
                FACEBOOK.userType -> FACEBOOK
                TWITTER.userType -> TWITTER
                else -> null
            }
        }
    }
}

enum class UpdateStatus{
    SUCCESS,
    REAUTHENTICATE
}