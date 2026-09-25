package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.SelectMissingWords

/**
 * DSL for a [SelectMissingWords] question.
 */
@KQuizMarker
class SelectMissingWordsDsl : QuestionDsl<SelectMissingWords>() {

  var defaultGrade: Double by Required()
  var shuffle: Boolean = true

  private var questionBlock: (GapSelectTextBuilder.() -> Unit) by Required("question")

  // Kept separate from [gaps] because `question { }` only runs its block, and so only allocates
  // gap indices, once [build] runs; keeping distractors apart avoids their declaration order relative
  // to `question { }` changing which index a gap ends up with.
  private val gaps = mutableListOf<SelectMissingWords.SelectOption>()
  private val distractors = mutableListOf<SelectMissingWords.SelectOption>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: GapSelectTextBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds an option that is not referenced by any [GapSelectTextBuilder.gap], i.e. a distractor.
   */
  fun option(text: String, group: Int = 1) {
    distractors += SelectMissingWords.SelectOption(text, group)
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  // A gap whose answer was already used by an earlier gap reuses that option, so the same answer
  // (e.g. a state appearing in several cells of a transition table) is offered only once per group.
  internal fun registerGap(text: String, group: Int): Int {
    val option = SelectMissingWords.SelectOption(text, group)
    val existing = gaps.indexOf(option)
    if (existing >= 0) return existing + 1

    gaps += option
    return gaps.size
  }

  override fun build(): SelectMissingWords {
    val question = GapSelectTextBuilder(this).apply(questionBlock)
    val options = gaps + distractors
    check(options.isNotEmpty()) { "selectMissingWords needs at least one gap or option" }
    return SelectMissingWords(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      options,
      shuffle,
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}

/**
 * Text builder for a [SelectMissingWords] question, allowing gaps to be inserted inline via [gap].
 */
@KQuizMarker
class GapSelectTextBuilder(private val dsl: SelectMissingWordsDsl) : HtmlBuilder() {

  /**
   * Inserts a gap at this point in the text, whose correct answer is [text]. Gaps with the same answer in the
   * same [group] share one option.
   */
  fun gap(text: String, group: Int = 1) {
    raw("[[${dsl.registerGap(text, group)}]]")
  }

}
