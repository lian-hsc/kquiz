package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.CombinedFeedback
import me.lian.hsc.kquiz.data.MultipleTries
import me.lian.hsc.kquiz.data.Question
import me.lian.hsc.kquiz.data.Text
import me.lian.hsc.kquiz.data.WrappedText

/**
 * Base DSL for the properties shared by every [Question].
 */
@KQuizMarker
sealed class QuestionDsl<Q : Question> {

  var name: String by Required()
  var hidden: Boolean? = null

  private val tagList = mutableListOf<String>()
  private var generalFeedbackBlock: (HtmlBuilder.() -> Unit)? = null

  fun tag(value: String) {
    tagList += value
  }

  fun generalFeedback(block: HtmlBuilder.() -> Unit) {
    generalFeedbackBlock = block
  }

  protected fun buildName() = WrappedText(name)
  protected fun buildTags() = tagList.map { WrappedText(it) }
  protected fun buildGeneralFeedback() = generalFeedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() }

  internal abstract fun build(): Q

}

/**
 * DSL for [MultipleTries].
 */
@KQuizMarker
class MultipleTriesDsl {

  var penalty: MultipleTries.Penalty? = null

  private val hints = mutableListOf<MultipleTries.Hint>()

  fun hint(block: HintDsl.() -> Unit) {
    hints += HintDsl().apply(block).build()
  }

  fun hint(text: String, showCorrect: Boolean = false, clearWrong: Boolean = false) {
    hints += MultipleTries.Hint(text, emptyList(), Text.Format.PlainText, showCorrect, clearWrong)
  }

  internal fun build() = MultipleTries(penalty, hints)

}

/**
 * DSL for a single [MultipleTries.Hint].
 */
@KQuizMarker
class HintDsl {

  var showCorrect: Boolean = false
  var clearWrong: Boolean = false

  private var textBlock: (HtmlBuilder.() -> Unit) by Required("text")

  fun text(block: HtmlBuilder.() -> Unit) {
    textBlock = block
  }

  internal fun build(): MultipleTries.Hint {
    val text = HtmlBuilder().apply(textBlock)
    return MultipleTries.Hint(text.build(), text.files, Text.Format.HTML, showCorrect, clearWrong)
  }

}

/**
 * DSL for [CombinedFeedback].
 */
@KQuizMarker
class CombinedFeedbackDsl {

  var showCorrectChecks: Boolean = false

  private var correctBlock: (HtmlBuilder.() -> Unit)? = null
  private var partiallyCorrectBlock: (HtmlBuilder.() -> Unit)? = null
  private var incorrectBlock: (HtmlBuilder.() -> Unit)? = null

  fun correct(block: HtmlBuilder.() -> Unit) {
    correctBlock = block
  }

  fun partiallyCorrect(block: HtmlBuilder.() -> Unit) {
    partiallyCorrectBlock = block
  }

  fun incorrect(block: HtmlBuilder.() -> Unit) {
    incorrectBlock = block
  }

  internal fun build() = CombinedFeedback(
    correctBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
    partiallyCorrectBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
    showCorrectChecks,
    incorrectBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
  )

}
