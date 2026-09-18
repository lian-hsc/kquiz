package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A question that is answered by selecting the correct word for a gap from a dropdown menu.
 * Gaps are referenced in the [question] text by using the format `[[number]]`,
 * where `number` is the index of the option in the [options] list.
 * The question text can thus not contain any other text wrapped in `[[` and `]]`.
 *
 * Every gap offers all [options] that share its [SelectOption.group] as choices in its dropdown menu.
 * As an option is always available for selection in every gap of its group, an option can be selected in multiple gaps at once,
 * and is not "used up" by being selected once, in contrast to a [DragAndDropIntoText.Dragbox].
 *
 * The option group should be less than or equal to 8.
 * Any further group will be displayed without any format.
 * When editing the question in Moodle, all groups that are not within range will be set to the first group.
 *
 * @property options the options that can be selected in the gaps of the question
 * @property shuffle whether the options are shuffled or not
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @see SelectOption
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("gapselect")
class SelectMissingWords(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("selectoption") val options: List<SelectOption>,
  @JsonSerialize(using = NumericBooleanSerializer::class) @JsonProperty("shuffleanswers") val shuffle: Boolean,
  @JsonUnwrapped val combinedFeedback: CombinedFeedback,
  @JsonUnwrapped val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * An option that can be selected from the dropdown menu of a gap (a `[[number]]` in the question text).
   *
   * @property text the text of the option
   * @property group the group of the option
   */
  data class SelectOption(
    val text: String,
    val group: Int,
  )

}
