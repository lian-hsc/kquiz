package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.DragAndDropMarkers
import me.lian.hsc.kquiz.data.File

/**
 * DSL for a [DragAndDropMarkers] question.
 */
@KQuizMarker
class DragAndDropMarkersDsl : QuestionDsl<DragAndDropMarkers>() {

  var defaultGrade: Double = 1.0
  var shuffle: Boolean = true

  /**
   * The background image; build one with [image].
   */
  var background: File by Required()

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private val markers = mutableListOf<DragAndDropMarkers.Marker>()
  private val dropzones = mutableListOf<DragAndDropMarkers.Dropzone<*>>()
  private val combinedFeedback = CombinedFeedbackDsl()
  private val multipleTries = MultipleTriesDsl()

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Adds a marker and returns its number for use in [circle], [rectangle] and [polygon].
   */
  fun marker(text: String, infinite: Boolean = false, numberOfMarkers: Int = 1): Int {
    val number = markers.size + 1
    markers += DragAndDropMarkers.Marker(number, text, infinite, numberOfMarkers)
    return number
  }

  /**
   * Adds a circular dropzone whose correct [marker] is the number returned by [marker].
   */
  fun circle(marker: Int, centerX: Double, centerY: Double, radius: Double) {
    dropzones += DragAndDropMarkers.Dropzone(
      dropzones.size + 1,
      DragAndDropMarkers.Shape.Circle,
      DragAndDropMarkers.Coordinates.Circle(centerX, centerY, radius),
      marker,
    )
  }

  /**
   * Adds a rectangular dropzone whose correct [marker] is the number returned by [marker].
   */
  fun rectangle(marker: Int, x: Double, y: Double, width: Double, height: Double) {
    dropzones += DragAndDropMarkers.Dropzone(
      dropzones.size + 1,
      DragAndDropMarkers.Shape.Rectangle,
      DragAndDropMarkers.Coordinates.Rectangle(x, y, width, height),
      marker,
    )
  }

  /**
   * Adds a polygonal dropzone whose correct [marker] is the number returned by [marker].
   */
  fun polygon(marker: Int, vararg points: Pair<Double, Double>) {
    require(points.size >= 3) { "polygon needs at least 3 points" }
    dropzones += DragAndDropMarkers.Dropzone(
      dropzones.size + 1,
      DragAndDropMarkers.Shape.Polygon,
      DragAndDropMarkers.Coordinates.Polygon(points.toList()),
      marker,
    )
  }

  fun combinedFeedback(block: CombinedFeedbackDsl.() -> Unit) {
    combinedFeedback.apply(block)
  }

  fun multipleTries(block: MultipleTriesDsl.() -> Unit) {
    multipleTries.apply(block)
  }

  override fun build(): DragAndDropMarkers {
    check(markers.isNotEmpty()) { "dragAndDropMarkers needs at least one marker" }
    check(dropzones.isNotEmpty()) { "dragAndDropMarkers needs at least one dropzone" }
    val question = HtmlBuilder().apply(questionBlock)
    return DragAndDropMarkers(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      background,
      markers.toList(),
      dropzones.toList(),
      shuffle,
      combinedFeedback.build(),
      multipleTries.build(),
    )
  }

}
