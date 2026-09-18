package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Fraction
import me.lian.hsc.kquiz.data.MultipleChoice
import me.lian.hsc.kquiz.data.Text

/**
 * DSL for a [MultipleChoice] question.
 */
@KQuizMarker
class MultipleChoiceDsl : QuestionDsl<MultipleChoice>() {

  var defaultGrade: Double = 1.0
  var single: Boolean = true
  var shuffle: Boolean = false
  var showStandardInstructions: Boolean = true
  var numbering: MultipleChoice.Numbering = MultipleChoice.Numbering.AbcLowercase

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val answers = mutableListOf<MultipleChoice.Answer>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  fun option(text: String, block: MultipleChoiceOptionDsl.() -> Unit = {}) {
    answers += MultipleChoiceOptionDsl(text).apply(block).build()
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): MultipleChoice {
    check(answers.isNotEmpty()) { "multipleChoice needs at least one option" }
    check(answers.any { it.fraction.value > 0.0 }) { "multipleChoice needs at least one correct option" }
    val question = HtmlBuilder().apply(questionBlock)
    return MultipleChoice(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      answers.toList(),
      single,
      shuffle,
      showStandardInstructions,
      numbering,
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}

/**
 * DSL for a single option of a [MultipleChoice] question.
 * @property text the option's text
 */
@KQuizMarker
class MultipleChoiceOptionDsl(private val text: String) {

  var fraction: Fraction = Fraction.Zero

  private var feedbackBlock: (HtmlBuilder.() -> Unit)? = null

  fun feedback(block: HtmlBuilder.() -> Unit) {
    feedbackBlock = block
  }

  internal fun build() = MultipleChoice.Answer(
    text,
    emptyList(),
    Text.Format.HTML,
    fraction,
    feedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
  )

}
