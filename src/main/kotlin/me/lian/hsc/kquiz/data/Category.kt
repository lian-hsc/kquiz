package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * A category represents a single category in a quiz.
 * All questions in a quiz are put in the category after which they appear.
 * A new category overrides the previous one.
 *
 * @property category the category name
 * @property info additional information about the category
 */
@JsonTypeName("category")
data class Category(val category: WrappedText<String>, val info: Text) : QuizEntry