package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import me.lian.hsc.kquiz.serialization.PresenceBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A drag and drop question that is answered by dropping a text value into a dropbox.
 * Dragboxes are referenced in the [question] text by using the format `[[number]]`,
 * where `number` is the index of the dragbox in the [dragboxes] list.
 * The question text can thus not contain any other text wrapped in `[[` and `]]`.
 *
 * The question should contain at most as many different drop zones as there are dragboxes.
 * Any further drop zone will be displayed without any formating as a very small drop zone.
 * As those drop zones always stay empty, the question will be shown as "Incomplete answer" at beast.
 * The drop zones which do not have a corresponding dragbox are always marked as incorrect and will deduce points.
 * When editing the question in Moodle, the drop zones which do not have a corresponding dragbox must be removed.
 * If there is an open or finished attempt that contains such a question, the "Question Bank" page on Moodle will break.
 *
 * A dragbox that has [Dragbox.infinite] set to false should only be referenced at most once.
 * If it is referenced more than once, only one dropbox can be filled.
 * The other one will be marked as incorrect and will deduce points.
 *
 * Each dragbox can only be placed in a dropzone of which the group is equal to the correct dragbox group.
 * A dragbox, which is not referenced in the question text, is shown along with the correct answers but is expected to not be used.
 * A dragbox can be used multiple times in the question text.
 * When using HTML format, a dragbox should only use
 * - `<sub>` and `<sup>` tags;
 * - `<b>`, `<i>`, `<em>` and `<strong>` tags; and
 * - `$$` TeX math mode (seems broken).
 * A dragbox can use any HTML content, but Moodle will display dragboxes that contain other tags weirdly.
 *
 * The dragbox group should be less than or equal to 8.
 * Any further group will be displayed without any format.
 * Thus, the ninetieth and any further dragbox group will be displayed without any format.
 * When editing the question in Moodle, all dragbox groups that are not within range will be set to the first group.
 *
 * @property dragboxes the dragboxes that can be dropped into
 * @property shuffle whether the dragboxes are shuffled or not
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @see Dragbox
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("ddwtos")
class DragAndDropIntoText(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("dragbox") val dragboxes: List<Dragbox>,
  @JsonSerialize(using = NumericBooleanSerializer::class) @JsonProperty("shuffleanswers") val shuffle: Boolean,
  val combinedFeedback: CombinedFeedback,
  val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * A dragbox that can be dragged onto the texted and dropped into a dropzone (a `[[number]]` in the question text).
   *
   * The [text] is, in contrast to a normal Moodle [Text] always displayed as HTML.
   *
   * @property text the text of the dragbox
   * @property group the group of the dragbox
   * @property infinite whether the dragbox can be dragged infinitely
   */
  class Dragbox(
    val text: String,
    val group: Int,
    @JsonSerialize(using = PresenceBooleanSerializer::class) val infinite: Boolean
  )

}