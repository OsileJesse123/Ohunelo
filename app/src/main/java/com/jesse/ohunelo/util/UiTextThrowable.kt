package com.jesse.ohunelo.util

/**
 * The purpose of this is class is to enable wrapping of throwable error messages inside of UiText.
 * This ensures that suitable error messages are shown to the user and localization best practices are
 * followed.
 * */
class UiTextThrowable(val errorMessage: UiText): Throwable()