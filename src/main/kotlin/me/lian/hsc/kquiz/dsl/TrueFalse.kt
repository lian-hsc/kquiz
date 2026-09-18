package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.TrueFalse

/**
 * DSL for a [TrueFalse] question.
 */
@KQuizMarker
class TrueFalseDsl : QuestionDsl<TrueFalse>() {

  var defaultGrade: Double = 1.0
  var correctAnswer: Boolean by Required()
  var showStandardInstructions: Boolean = true

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private var trueFeedbackBlock: (HtmlBuilder.() -> Unit)? = null
  private var falseFeedbackBlock: (HtmlBuilder.() -> Unit)? = null

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  fun trueFeedback(block: HtmlBuilder.() -> Unit) {
    trueFeedbackBlock = block
  }

  fun falseFeedback(block: HtmlBuilder.() -> Unit) {
    falseFeedbackBlock = block
  }

  override fun build(): TrueFalse {
    val question = HtmlBuilder().apply(questionBlock)
    return TrueFalse(
      buildName(),
      question.toSimpleText(),
      correctAnswer,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      trueFeedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
      falseFeedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
      defaultGrade,
      showStandardInstructions,
    )
  }

}
