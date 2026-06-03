package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import me.lian.hsc.kquiz.serialization.PresenceBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A darg and drop question that is answered by dragging an item onto a dropzone on a background image.
 * Dropzone are visible to the student.
 *
 * Draggable items can either have an [DraggableItem.text] or an [DraggableItem.file].
 * If both are set, the [DraggableItem.text] is displayed.
 * The size of a dropzone is determined by the size of the biggest item in its group.
 *
 * Draggable items and drop zones should use consecutive numbers but are not required to do so.
 * If non-consecutive numbers are used, Moodle will still display them at their position and
 * show all previous items as empty items.
 *
 * Any drop zone that has [Dropzone.correctDraggableItem] set to a dragable item that does not exist will not be displayed.
 * It will thus always stay empty and deduce points.
 *
 * Any drop zone out of bounds will be displayed in the top left corner.
 *
 * The dragbox group should be less than or equal to 8.
 * Any further group will be displayed without any format.
 * Thus, the ninetieth and any further dragbox group will be displayed without any format.
 * When editing the question in Moodle, all dragbox groups that are not within range will be set to the first group.
 *
 * @property background the background image
 * @property draggableItems the draggable items that can be dragged and dropped
 * @property transparentDropzones whether the drop zones are transparent (i.e., you can see the background image through them or not)
 * @property dropzones the drop zones on which the draggable items can be dropped
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries whether the question can be attempted multiple times or not
 */
@JsonTypeName("ddimageortext")
class DragAndDropOntoImage(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JsonProperty("file") val background: File,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("drag") val draggableItems: List<DraggableItem>,
  @JsonSerialize(using = PresenceBooleanSerializer::class) @JsonProperty("dropzonevisibility") val transparentDropzones: Boolean,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("drop") val dropzones: List<Dropzone>,
  val combinedFeedback: CombinedFeedback,
  val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  data class DraggableItem(
    @JsonProperty("no") val number: Int,
    @JsonProperty("draggroup") val dragGroup: Int,
    val text: String?,
    val file: File?,
  )

  data class Dropzone(
    @JsonProperty("no") val number: Int,
    val text: String?,
    @JsonProperty("choice") val correctDraggableItem: Int,
    val left: Double,
    val top: Double,
  )

}