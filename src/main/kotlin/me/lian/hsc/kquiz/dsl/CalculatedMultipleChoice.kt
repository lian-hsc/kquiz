package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Calculated
import me.lian.hsc.kquiz.data.CalculatedMultipleChoice
import me.lian.hsc.kquiz.data.Fraction
import me.lian.hsc.kquiz.data.Text

/**
 * DSL for a [CalculatedMultipleChoice] question.
 */
@KQuizMarker
class CalculatedMultipleChoiceDsl : QuestionDsl<CalculatedMultipleChoice>() {

  var defaultGrade: Double = 1.0
  var single: Boolean = true
  var synchronizeWildcards: Calculated.SynchronizeWildcards = Calculated.SynchronizeWildcards.Private

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val answers = mutableListOf<CalculatedMultipleChoice.Answer>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()
  private val datasets = mutableListOf<Calculated.Dataset>()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds an option. [text] may reference wildcards, e.g. `{a} + {b}`, or a calculation via `{=...}`.
   */
  fun option(text: String, block: CalculatedMultipleChoiceOptionDsl.() -> Unit = {}) {
    answers += CalculatedMultipleChoiceOptionDsl(text).apply(block).build()
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  fun dataset(name: String, block: DatasetDsl.() -> Unit) {
    datasets += DatasetDsl(name).apply(block).build()
  }

  override fun build(): CalculatedMultipleChoice {
    check(answers.isNotEmpty()) { "calculatedMultipleChoice needs at least one option" }
    check(answers.any { it.fraction.value > 0.0 }) { "calculatedMultipleChoice needs at least one correct option" }
    val question = HtmlBuilder().apply(questionBlock)
    return CalculatedMultipleChoice(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      answers.toList(),
      single,
      synchronizeWildcards,
      combinedFeedback.build(),
      multipleTries.build(),
      datasets.toList(),
    )
  }

}

/**
 * DSL for a single option of a [CalculatedMultipleChoice] question.
 */
@KQuizMarker
class CalculatedMultipleChoiceOptionDsl(private val text: String) {

  var fraction: Fraction = Fraction.Zero
  var tolerance: Double = 0.0
  var toleranceType: Calculated.Answer.ToleranceType = Calculated.Answer.ToleranceType.Nominal
  var answerLength: Int = 2
  var answerFormat: Calculated.Answer.AnswerFormat = Calculated.Answer.AnswerFormat.Decimal

  internal fun build() = CalculatedMultipleChoice.Answer(
    text,
    emptyList(),
    Text.Format.HTML,
    fraction,
    tolerance,
    toleranceType,
    answerLength,
    answerFormat,
  )

}
