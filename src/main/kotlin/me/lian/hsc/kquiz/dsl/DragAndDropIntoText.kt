package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.DragAndDropIntoText
import me.lian.hsc.kquiz.data.SimpleText

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
  private val declared = mutableListOf<DragAndDropIntoText.Dragbox>()
  private val distractors = mutableListOf<DragAndDropIntoText.Dragbox>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: DragTextBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds a dragbox that is shown but never referenced by [DragTextBuilder.drag], i.e. a distractor. An
   * [infinite] one can be dragged any number of times.
   */
  fun distractor(text: String, group: Int = 1, infinite: Boolean = false) {
    distractors += DragAndDropIntoText.Dragbox(text, group, infinite)
  }

  /**
   * Declares a dragbox reading [text], to put into gaps with [DragTextBuilder.drop] -- e.g. into several of them
   * (then usually [infinite], so it can be dragged as often as needed). Declared dragboxes are numbered after
   * the ones [DragTextBuilder.drag] adds, in the order they are declared.
   */
  fun dragbox(text: String, group: Int = 1, infinite: Boolean = false): DragboxRef {
    declared += DragAndDropIntoText.Dragbox(text, group, infinite)
    return DragboxRef(declared.size - 1)
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  internal fun checkDeclared(box: DragboxRef) {
    check(box.index in declared.indices) { "the dragbox wasn't declared by this question" }
  }

  internal fun registerDrag(text: String, group: Int, infinite: Boolean): Int {
    drags += DragAndDropIntoText.Dragbox(text, group, infinite)
    return drags.size
  }

  override fun build(): DragAndDropIntoText {
    val question = DragTextBuilder(this).apply(questionBlock).toSimpleText()
    // Declared dragboxes are only numbered now, after the inline drags the text registered.
    val text = DROP_MARKER.replace(question.text) { "[[${drags.size + it.groupValues[1].toInt() + 1}]]" }
    val dragboxes = drags + declared + distractors
    check(dragboxes.isNotEmpty()) { "dragAndDropIntoText needs at least one drag" }
    return DragAndDropIntoText(
      buildName(),
      SimpleText(text, question.files, question.format),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      idNumber,
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

  /** Inserts a dropzone at this point in the text, whose correct dragbox is [box] (see [DragAndDropIntoTextDsl.dragbox]). */
  fun drop(box: DragboxRef) {
    dsl.checkDeclared(box)
    raw("$DROP_MARKER_START${box.index}$DROP_MARKER_END")
  }

}

/** A dragbox declared with [DragAndDropIntoTextDsl.dragbox], to [DragTextBuilder.drop] into gaps. */
class DragboxRef internal constructor(internal val index: Int)

// Stands for a declared dragbox's [[n]] until its number is known.
private const val DROP_MARKER_START = "\u0000drop:"
private const val DROP_MARKER_END = "\u0000"
private val DROP_MARKER = Regex("$DROP_MARKER_START(\\d+)$DROP_MARKER_END")
