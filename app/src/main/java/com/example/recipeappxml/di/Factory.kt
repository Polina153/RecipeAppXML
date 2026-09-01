package com.example.recipeappxml.di

interface Factory<T> {
    fun create(): T
}