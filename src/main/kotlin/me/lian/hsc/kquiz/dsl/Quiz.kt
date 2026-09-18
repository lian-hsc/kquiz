package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Category
import me.lian.hsc.kquiz.data.Quiz
import me.lian.hsc.kquiz.data.QuizEntry
import me.lian.hsc.kquiz.data.SimpleText
import me.lian.hsc.kquiz.data.Text
import me.lian.hsc.kquiz.data.WrappedText

/**
 * Base DSL shared by [QuizDsl] and [CategoryDsl] for adding questions.
 */
@KQuizMarker
sealed class QuizEntriesDsl(protected val entries: MutableList<QuizEntry>) {

  /**
   * Starts a new category. All questions added within [block] are put into this category,
   * which stays the current category for every following entry until the next [category].
   */
  fun category(block: CategoryDsl.() -> Unit) {
    entries += CategoryDsl().apply(block).build()
  }

  fun embeddedAnswer(block: EmbeddedAnswerDsl.() -> Unit) {
    entries += EmbeddedAnswerDsl().apply(block).build()
  }

  fun selectMissingWords(block: SelectMissingWordsDsl.() -> Unit) {
    entries += SelectMissingWordsDsl().apply(block).build()
  }

  fun ordering(block: OrderingDsl.() -> Unit) {
    entries += OrderingDsl().apply(block).build()
  }

  fun multipleChoice(block: MultipleChoiceDsl.() -> Unit) {
    entries += MultipleChoiceDsl().apply(block).build()
  }

  fun trueFalse(block: TrueFalseDsl.() -> Unit) {
    entries += TrueFalseDsl().apply(block).build()
  }

  fun shortAnswer(block: ShortAnswerDsl.() -> Unit) {
    entries += ShortAnswerDsl().apply(block).build()
  }

  fun numerical(block: NumericalDsl.() -> Unit) {
    entries += NumericalDsl().apply(block).build()
  }

  fun matching(block: MatchingDsl.() -> Unit) {
    entries += MatchingDsl().apply(block).build()
  }

  fun calculated(block: CalculatedDsl.() -> Unit) {
    entries += CalculatedDsl().apply(block).build()
  }

  fun calculatedMultipleChoice(block: CalculatedMultipleChoiceDsl.() -> Unit) {
    entries += CalculatedMultipleChoiceDsl().apply(block).build()
  }

  fun dragAndDropOntoImage(block: DragAndDropOntoImageDsl.() -> Unit) {
    entries += DragAndDropOntoImageDsl().apply(block).build()
  }

  fun dragAndDropIntoText(block: DragAndDropIntoTextDsl.() -> Unit) {
    entries += DragAndDropIntoTextDsl().apply(block).build()
  }

  fun dragAndDropMarkers(block: DragAndDropMarkersDsl.() -> Unit) {
    entries += DragAndDropMarkersDsl().apply(block).build()
  }

}

/**
 * The root DSL for building a [Quiz].
 */
@KQuizMarker
class QuizDsl : QuizEntriesDsl(mutableListOf()) {

  internal fun build(): Quiz = Quiz(entries.toList())

}

/**
 * DSL for a [Category] and the questions within it.
 */
@KQuizMarker
class CategoryDsl : QuizEntriesDsl(mutableListOf()) {

  var name: String by Required()

  private var infoBlock: (HtmlBuilder.() -> Unit)? = null

  fun info(block: HtmlBuilder.() -> Unit) {
    infoBlock = block
  }

  internal fun build(): List<QuizEntry> {
    val info = infoBlock?.let { HtmlBuilder().apply(it).toSimpleText() }
      ?: SimpleText("", emptyList(), Text.Format.MoodleAutoFormat)
    return listOf(Category(WrappedText(name), info)) + entries
  }

}

/**
 * Builds a [Quiz].
 */
fun kquiz(block: QuizDsl.() -> Unit): Quiz = QuizDsl().apply(block).build()
