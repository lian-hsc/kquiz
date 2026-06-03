package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonValue
import me.lian.hsc.kquiz.serialization.PresenceBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A drag and drop question that is answered by dragging markers onto drop-zones on a background image.
 * Dropzone are not visible to the student.
 *
 * Markers and Dropzone should use consecutive numbers but are not required to do so.
 * If non-consecutive numbers are used, Moodle will still display them at their position and
 * show all previous items as empty items.
 *
 * If a marker is set to [Marker.infinite] the [Marker.numberOfMarkers] property is ignored.
 * If [Marker.numberOfMarkers] is higher than 6 Moodle will display them as infinite markers,
 * even though they are not infite.
 * If a question with such a marker is edited, it is set to infinity.
 *
 * The [Dropzone.choice] of each dropzone must match the id of a marker.
 * If not, Moodle will fail to display the question preview in addition to
 * the entier quiz and the "Questions" page if added to a quiz.
 * This can be fixed by editing the question to be valid via the "Question Bank" page.
 *
 * The [Dropzone.coordinates] of each dropzone must be inbounds:
 * - Its left-most point must not be smaller than 0;
 * - Its right-most point must not be bigger than the [background] width;
 * - Its top-most point must not be smaller than 0;
 * - Its bottom-most point must not be bigger than the [background] height.
 * If a Dropzone is entirely outside the [background] it cannot successfully be dropped onto
 * and will thus always reduce points.
 *
 * A marker that is reference more often than its [Marker.numberOfMarkers]
 * can only be dropped in at most [Marker.numberOfMarkers] dropzones.
 * Thus, the other drop zones will be marked as incorrect and will deduce points.
 *
 * @property background the background image on which the markers are dragged and dropped
 * @property markers the markers that can be dragged and dropped
 * @property dropzones the drop zones on which the markers can be dropped
 * @property shuffle whether the markers are shuffled or not
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @see Marker
 * @see Dropzone
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("ddmarker")
class DragAndDropMarkers(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JsonProperty("file") val background: File,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("drag") val markers: List<Marker>,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("drop") val dropzones: List<Dropzone<*>>,
  @JsonSerialize(using = PresenceBooleanSerializer::class) @JsonProperty("shuffleanswers") val shuffle: Boolean,
  val combinedFeedback: CombinedFeedback,
  val multipleTries: MultipleTries
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * A marker that can be dragged and dropped onto a dropzone.
   *
   * @property number the number of the marker
   * @property text the text of the marker
   * @property infinite whether the marker can be dragged infinitely
   * @property numberOfMarkers the number of markers that can be dragged onto the background image
   */
  data class Marker(
    @JsonProperty("no") val number: Int,
    val text: String,
    @JsonSerialize(using = PresenceBooleanSerializer::class) val infinite: Boolean,
    @JsonProperty("noofdrags") val numberOfMarkers: Int,
  )

  /**
   * A dropzone on which a marker can be dropped.
   * Each dropzone can have a different shape.
   *
   * @property number the number of the dropzone
   * @property shape the shape of the dropzone
   * @property coordinates the coordinates of the dropzone
   * @property choice the number of the marker that can be dropped onto the dropzone
   */
  data class Dropzone<T : Shape<T>>(
    @JsonProperty("no") val number: Int,
    val shape: Shape<T>,
    @JsonProperty("coords") val coordinates: Coordinates<T>,
    val choice: Int
  )

  /**
   * The shape of a dropzone.
   */
  interface Shape<T : Shape<T>> {

    @get:JsonValue
    val value: String

    /**
     * A circle.
     */
    object Circle : Shape<Circle> {
      override val value: String = "circle"
    }

    /**
     * A polygon.
     */
    object Polygon : Shape<Polygon> {
      override val value: String = "polygon"
    }

    /**
     * A rectangle.
     */
    object Rectangle : Shape<Rectangle> {
      override val value: String = "rectangle"
    }

  }

  /**
   * The coordinates of a dropzone.
   */
  interface Coordinates<T : Shape<T>> {

    @get:JsonValue
    val value: String

    /**
     * Coordinates of a circle.
     *
     * @property centerX the x-coordinate of the center of the circle
     * @property centerY the y-coordinate of the center of the circle
     * @property radius the radius of the circle
     */
    data class Circle(val centerX: Double, val centerY: Double, val radius: Double) : Coordinates<Shape.Circle> {

      override val value: String = "$centerX,$centerY;$radius"

    }

    /**
     * Coordinates of a polygon.
     *
     * @property points the coordinates of the points of the polygon
     */
    data class Polygon(val points: List<Pair<Double, Double>>) : Coordinates<Shape.Polygon> {

      override val value: String = points.joinToString(";") { "${it.first},${it.second}" }

    }

    /**
     * Coordinates of a rectangle.
     *
     * @property topLeftX the x-coordinate of the top left corner of the rectangle
     * @property topLeftY the y-coordinate of the top left corner of the rectangle
     * @property width the width of the rectangle
     * @property height the height of the rectangle
     */
    data class Rectangle(val topLeftX: Double, val topLeftY: Double, val width: Double, val height: Double) :
      Coordinates<Shape.Rectangle> {

      override val value: String = "$topLeftX,$topLeftY;$width,$height"

    }

  }

}