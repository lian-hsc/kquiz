package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.EmbeddedAnswer

/**
 * DSL for an [EmbeddedAnswer] question.
 */
@KQuizMarker
class EmbeddedAnswerDsl : QuestionDsl<EmbeddedAnswer>() {

  private var questionBlock: (ClozeTextBuilder.() -> Unit) by Required("question")
  private val multipleTries = MultipleTriesDsl()

  fun question(block: ClozeTextBuilder.() -> Unit) {
    questionBlock = block
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): EmbeddedAnswer {
    val question = ClozeTextBuilder().apply(questionBlock)
    return EmbeddedAnswer(
      buildName(),
      question.toSimpleText(),
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      multipleTries.build(),
    )
  }

}
