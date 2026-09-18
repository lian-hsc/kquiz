package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.DragAndDropOntoImage
import me.lian.hsc.kquiz.data.File

/**
 * DSL for a [DragAndDropOntoImage] question.
 */
@KQuizMarker
class DragAndDropOntoImageDsl : QuestionDsl<DragAndDropOntoImage>() {

  var defaultGrade: Double = 1.0
  var transparentDropzones: Boolean = false

  /**
   * The background image; build one with [image].
   */
  var background: File by Required()

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val items = mutableListOf<DragAndDropOntoImage.DraggableItem>()
  private val dropzones = mutableListOf<DragAndDropOntoImage.Dropzone>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds a draggable item, either [text] or a [file] (build one with [image]), and returns its number
   * for use in [dropzone].
   */
  fun item(group: Int = 1, text: String? = null, file: File? = null): Int {
    require(text != null || file != null) { "item needs text or a file" }
    val number = items.size + 1
    items += DragAndDropOntoImage.DraggableItem(number, group, text, file)
    return number
  }

  /**
   * Adds a dropzone at `(x, y)` whose correct [item] is the number returned by [item].
   */
  fun dropzone(item: Int, x: Int, y: Int, text: String? = null) {
    dropzones += DragAndDropOntoImage.Dropzone(dropzones.size + 1, text, item, x, y)
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): DragAndDropOntoImage {
    check(items.isNotEmpty()) { "dragAndDropOntoImage needs at least one item" }
    check(dropzones.isNotEmpty()) { "dragAndDropOntoImage needs at least one dropzone" }
    val question = HtmlBuilder().apply(questionBlock)
    return DragAndDropOntoImage(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      background,
      items.toList(),
      transparentDropzones,
      dropzones.toList(),
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}
