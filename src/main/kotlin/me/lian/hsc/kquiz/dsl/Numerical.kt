package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Fraction
import me.lian.hsc.kquiz.data.Numerical
import me.lian.hsc.kquiz.data.Text

/**
 * DSL for a [Numerical] question.
 */
@KQuizMarker
class NumericalDsl : QuestionDsl<Numerical>() {

  var defaultGrade: Double = 1.0

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val answers = mutableListOf<Numerical.Answer>()
  private val units = UnitsDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  fun answer(value: Double, tolerance: Double? = null, block: NumericalAnswerDsl.() -> Unit = {}) {
    answers += NumericalAnswerDsl(value, tolerance).apply(block).build()
  }

  fun units(block: UnitsDsl.() -> Unit) {
    units.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): Numerical {
    check(answers.isNotEmpty()) { "numerical needs at least one answer" }
    val question = HtmlBuilder().apply(questionBlock)
    return Numerical(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      answers.toList(),
      units.build(),
      multipleTries.build(),
    )
  }

}

/**
 * DSL for a single answer of a [Numerical] question.
 * @property value the accepted value
 * @property tolerance the tolerance around [value] that is still accepted
 */
@KQuizMarker
class NumericalAnswerDsl(private val value: Double, private val tolerance: Double?) {

  var fraction: Fraction.Positive = Fraction.Positive.One

  private var feedbackBlock: (HtmlBuilder.() -> Unit)? = null

  fun feedback(block: HtmlBuilder.() -> Unit) {
    feedbackBlock = block
  }

  internal fun build() = Numerical.Answer(
    value.toString(),
    emptyList(),
    Text.Format.PlainText,
    fraction,
    feedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
    tolerance,
  )

}
