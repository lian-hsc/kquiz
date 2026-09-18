package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Ordering
import me.lian.hsc.kquiz.data.Text

/**
 * DSL for an [Ordering] question. Items are added, in the correct order, via [item].
 */
@KQuizMarker
class OrderingDsl : QuestionDsl<Ordering>() {

  var defaultGrade: Double by Required()
  var layout: Ordering.Layout = Ordering.Layout.Vertical
  var selection: Ordering.Selection = Ordering.Selection.All
  var selectionCount: Int? = null
  var grading: Ordering.Grading = Ordering.Grading.RelativeNextExcludeLast
  var showGrading: Ordering.ShowGrading = Ordering.ShowGrading.Show
  var numberingStyle: Ordering.NumberingStyle = Ordering.NumberingStyle.None

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val items = mutableListOf<Ordering.Answer>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds an item to the end of the correct order.
   */
  fun item(text: String, block: OrderingItemDsl.() -> Unit = {}) {
    items += OrderingItemDsl(text, items.size + 1).apply(block).build()
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): Ordering {
    check(items.size >= 2) { "ordering needs at least 2 items" }
    if (selection != Ordering.Selection.All) {
      checkNotNull(selectionCount) { "selectionCount is required when selection is $selection" }
    }

    val question = HtmlBuilder().apply(questionBlock)
    return Ordering(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      layout,
      selection,
      selectionCount,
      grading,
      showGrading,
      numberingStyle,
      items.toList(),
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}

/**
 * DSL for a single item of an [Ordering] question.
 */
@KQuizMarker
class OrderingItemDsl(private val text: String, private val position: Int) {

  private var feedbackBlock: (HtmlBuilder.() -> Unit)? = null

  fun feedback(block: HtmlBuilder.() -> Unit) {
    feedbackBlock = block
  }

  internal fun build() = Ordering.Answer(
    text,
    emptyList(),
    Text.Format.HTML,
    position,
    feedbackBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
  )

}
