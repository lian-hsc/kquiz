package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.DragAndDropIntoText

/**
 * DSL for a [DragAndDropIntoText] question.
 */
@KQuizMarker
class DragAndDropIntoTextDsl : QuestionDsl<DragAndDropIntoText>() {

  var defaultGrade: Double = 1.0
  var shuffle: Boolean = true

  private var questionBlock: (DragTextBuilder.() -> Unit) by Required("question")

  // Kept separate from [distractors] for the same reason as in SelectMissingWordsDsl: `question { }`
  // only allocates drag indices once [build] runs, so distractors must not be mixed in beforehand.
  private val drags = mutableListOf<DragAndDropIntoText.Dragbox>()
  private val distractors = mutableListOf<DragAndDropIntoText.Dragbox>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: DragTextBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds a dragbox that is shown but never referenced by [DragTextBuilder.drag], i.e. a distractor.
   */
  fun distractor(text: String, group: Int = 1) {
    distractors += DragAndDropIntoText.Dragbox(text, group, false)
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  internal fun registerDrag(text: String, group: Int, infinite: Boolean): Int {
    drags += DragAndDropIntoText.Dragbox(text, group, infinite)
    return drags.size
  }

  override fun build(): DragAndDropIntoText {
    val question = DragTextBuilder(this).apply(questionBlock)
    val dragboxes = drags + distractors
    check(dragboxes.isNotEmpty()) { "dragAndDropIntoText needs at least one drag" }
    return DragAndDropIntoText(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      dragboxes,
      shuffle,
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}

/**
 * Text builder for a [DragAndDropIntoText] question, allowing dragboxes to be inserted inline via [drag].
 */
@KQuizMarker
class DragTextBuilder(private val dsl: DragAndDropIntoTextDsl) : HtmlBuilder() {

  /**
   * Inserts a dropzone at this point in the text, whose correct dragbox reads [text].
   */
  fun drag(text: String, group: Int = 1, infinite: Boolean = false) {
    raw("[[${dsl.registerDrag(text, group, infinite)}]]")
  }

}
