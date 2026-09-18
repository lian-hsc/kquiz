package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Calculated
import me.lian.hsc.kquiz.data.Fraction
import me.lian.hsc.kquiz.data.WrappedText

/**
 * DSL for a [Calculated] question.
 */
@KQuizMarker
class CalculatedDsl : QuestionDsl<Calculated>() {

  var defaultGrade: Double = 1.0
  var synchronizeWildcards: Calculated.SynchronizeWildcards = Calculated.SynchronizeWildcards.Private

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val answers = mutableListOf<Calculated.Answer>()
  private val units = UnitsDsl()
  private val multipleTries = MultipleTriesDsl()
  private val datasets = mutableListOf<Calculated.Dataset>()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  fun answer(text: String, block: CalculatedAnswerDsl.() -> Unit = {}) {
    answers += CalculatedAnswerDsl(text).apply(block).build()
  }

  fun units(block: UnitsDsl.() -> Unit) {
    units.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  /**
   * Adds a wildcard dataset, referenced from [question] and answers via `{name}`.
   */
  fun dataset(name: String, block: DatasetDsl.() -> Unit) {
    datasets += DatasetDsl(name).apply(block).build()
  }

  override fun build(): Calculated {
    check(answers.isNotEmpty()) { "calculated needs at least one answer" }
    val question = HtmlBuilder().apply(questionBlock)
    return Calculated(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      answers.toList(),
      synchronizeWildcards,
      units.build(),
      multipleTries.build(),
      datasets.toList(),
    )
  }

}

/**
 * DSL for a single answer of a [Calculated] question. [text] may reference wildcards, e.g. `{a} + {b}`.
 */
@KQuizMarker
class CalculatedAnswerDsl(private val text: String) {

  var fraction: Fraction = Fraction.Positive.One
  var tolerance: Double = 0.0
  var toleranceType: Calculated.Answer.ToleranceType = Calculated.Answer.ToleranceType.Nominal
  var answerLength: Int = 2
  var answerFormat: Calculated.Answer.AnswerFormat = Calculated.Answer.AnswerFormat.Decimal

  internal fun build() = Calculated.Answer(text, fraction, tolerance, toleranceType, answerLength, answerFormat)

}

/**
 * DSL for a wildcard dataset of a [Calculated] or [me.lian.hsc.kquiz.data.CalculatedMultipleChoice] question.
 * @property name the wildcard's name
 */
@KQuizMarker
class DatasetDsl(private val name: String) {

  var status: Calculated.Dataset.Status = Calculated.Dataset.Status.Private
  var distribution: Calculated.Dataset.Distribution = Calculated.Dataset.Distribution.Uniform
  var minimum: Double by Required()
  var maximum: Double by Required()
  var decimals: Int = 0

  private val items = mutableListOf<Calculated.Dataset.Item>()

  /**
   * Adds an already-generated value to the dataset.
   */
  fun item(value: Double) {
    items += Calculated.Dataset.Item(items.size + 1, value)
  }

  internal fun build(): Calculated.Dataset {
    check(items.isNotEmpty()) { "dataset \"$name\" needs at least one item" }
    return Calculated.Dataset(
      WrappedText(status),
      WrappedText(name),
      WrappedText(distribution),
      WrappedText(minimum),
      WrappedText(maximum),
      WrappedText(decimals),
      items.size,
      items.toList(),
    )
  }

}
