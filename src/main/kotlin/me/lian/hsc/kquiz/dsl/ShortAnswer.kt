package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Fraction
import me.lian.hsc.kquiz.data.ShortAnswer
import me.lian.hsc.kquiz.data.Text

/**
 * DSL for a [ShortAnswer] question.
 */
@KQuizMarker
class ShortAnswerDsl : QuestionDsl<ShortAnswer>() {

  var defaultGrade: Double = 1.0
  var caseSensitive: Boolean = false

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val answers = mutableListOf<ShortAnswer.Answer>()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  fun answer(text: String, block: ShortAnswerOptionDsl.() -> Unit = {}) {
    answers += ShortAnswerOptionDsl(text).apply(block).build()
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): ShortAnswer {
    check(answers.isNotEmpty()) { "shortAnswer needs at least one answer" }
    val question = HtmlBuilder().apply(questionBlock)
    return ShortAnswer(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      answers.toList(),
      caseSensitive,
      multipleTries.build(),
    )
  }

}

/**
 * DSL for a single answer of a [ShortAnswer] question.
 * @property text the accepted text
 */
@KQuizMarker
class ShortAnswerOptionDsl(private val text: String) {

  var fraction: Fraction = Fraction.Positive.One

  private var feedbackBlock: (HtmlBuilder.() -> Unit)? = null

  fun feedback(block: HtmlBuilder.() -> Unit) {
    feedbackBlock = block
  }

  internal fun build() = ShortAnswer.Answer(
    text,
    emptyList(),
    Text.Format.PlainText,
    fraction,
    feedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
  )

}
