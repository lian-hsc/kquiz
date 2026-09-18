package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Matching
import me.lian.hsc.kquiz.data.Text
import me.lian.hsc.kquiz.data.WrappedText

/**
 * DSL for a [Matching] question.
 */
@KQuizMarker
class MatchingDsl : QuestionDsl<Matching>() {

  var defaultGrade: Double = 1.0

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val answers = mutableListOf<Matching.Answer>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds a sub-question and its correct match.
   */
  fun pair(question: String, answer: String) {
    answers += Matching.Answer(question, emptyList(), Text.Format.HTML, WrappedText(answer))
  }

  /**
   * Adds a match that is never the correct answer for any [pair], i.e. a distractor.
   */
  fun distractor(answer: String) {
    answers += Matching.Answer("", emptyList(), Text.Format.HTML, WrappedText(answer))
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): Matching {
    check(answers.size >= 2) { "matching needs at least 2 pairs" }
    val question = HtmlBuilder().apply(questionBlock)
    return Matching(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      answers.toList(),
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}
