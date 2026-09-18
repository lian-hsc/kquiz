package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * A question that is answered by arranging a list of items into the correct order.
 *
 * This question type is provided by the contributed "Ordering" plugin (`qtype_ordering`) rather than Moodle core.
 * It must be installed on the target Moodle instance for questions of this type to be importable.
 *
 * The correct order of the [answers] is defined by their position within the [answers] list.
 * Each [Answer.position] should match the 1-based index of its answer within [answers].
 * Moodle recalculates the position from the list order on import regardless of the value of [Answer.position],
 * so a mismatching value is not an error, but should be avoided for clarity.
 *
 * If [selection] is [Selection.Random] or [Selection.Contiguous], only [selectionCount] many of the [answers]
 * are shown to the student on a given attempt; [selectionCount] is ignored if [selection] is [Selection.All].
 *
 * @property layout whether the items are laid out vertically or horizontally
 * @property selection which items are shown to the student
 * @property selectionCount the number of items that are shown to the student, ignored if [selection] is [Selection.All]
 * @property grading how the question is graded
 * @property showGrading whether the details of the grading are shown to the student
 * @property numberingStyle how the items are numbered
 * @property answers the items to be put into the correct order
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @see Selection
 * @see Grading
 * @see NumberingStyle
 * @see Answer
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("ordering")
class Ordering(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JsonProperty("layouttype") val layout: Layout,
  @JsonProperty("selecttype") val selection: Selection,
  @JsonProperty("selectcount") val selectionCount: Int?,
  @JsonProperty("gradingtype") val grading: Grading,
  @JsonProperty("showgrading") val showGrading: ShowGrading,
  @JsonProperty("numberingstyle") val numberingStyle: NumberingStyle,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<Answer>,
  @JsonUnwrapped val combinedFeedback: CombinedFeedback,
  @JsonUnwrapped val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * Whether the items are laid out vertically or horizontally.
   */
  enum class Layout(@get:JsonValue val value: String) {
    Vertical("VERTICAL"),
    Horizontal("HORIZONTAL"),
  }

  /**
   * Which items of the question are shown to the student on a given attempt.
   */
  enum class Selection(@get:JsonValue val value: String) {
    /**
     * All items are shown to the student.
     */
    All("ALL"),

    /**
     * A random subset of the items is shown to the student.
     * @see Ordering.selectionCount
     */
    Random("RANDOM"),

    /**
     * A random contiguous subset of the items is shown to the student.
     * @see Ordering.selectionCount
     */
    Contiguous("CONTIGUOUS"),
  }

  /**
   * How the question is graded.
   */
  enum class Grading(@get:JsonValue val value: String) {
    /**
     * Full marks if every item is in the correct order, no marks otherwise.
     */
    AllOrNothing("ALL_OR_NOTHING"),

    /**
     * Each item is compared to the position it should be in.
     */
    AbsolutePosition("ABSOLUTE_POSITION"),

    /**
     * Each item, except for the last, is compared to the item that follows it.
     */
    RelativeNextExcludeLast("RELATIVE_NEXT_EXCLUDE_LAST"),

    /**
     * Each item, including the last, is compared to the item that follows it (the last item is compared to being last).
     */
    RelativeNextIncludeLast("RELATIVE_NEXT_INCLUDE_LAST"),

    /**
     * Each item is compared to the item that precedes and the item that follows it.
     */
    RelativeOnePreviousAndNext("RELATIVE_ONE_PREVIOUS_AND_NEXT"),

    /**
     * Each item is compared to all items that precede and all items that follow it.
     */
    RelativeAllPreviousAndNext("RELATIVE_ALL_PREVIOUS_AND_NEXT"),

    /**
     * The length of the longest ordered subset of items is used for grading.
     */
    LongestOrderedSubset("LONGEST_ORDERED_SUBSET"),

    /**
     * The length of the longest contiguous ordered subset of items is used for grading.
     */
    LongestContiguousSubset("LONGEST_CONTIGUOUS_SUBSET"),

    /**
     * Each item is compared to the item that is correct for its position.
     */
    RelativeToCorrect("RELATIVE_TO_CORRECT"),
  }

  /**
   * Whether the details of the grading are shown to the student.
   */
  enum class ShowGrading(@get:JsonValue val value: String) {
    Show("SHOW"),
    Hide("HIDE"),
  }

  /**
   * How the items are numbered.
   */
  enum class NumberingStyle(@get:JsonValue val value: String) {
    None("none"),
    AbcLowercase("abc"),
    AbcUppercase("ABCD"),
    Numerical("123"),
    RomanLowercase("iii"),
    RomanUppercase("IIII"),
  }

  /**
   * An item that has to be put into the correct order.
   *
   * @property position the 1-based position of the item in the correct order
   * @property feedback the feedback for the item, usually not used
   * @see Text
   * @see Ordering
   */
  class Answer(
    text: String,
    files: List<File>,
    format: Format,
    @JacksonXmlProperty(isAttribute = true, localName = "fraction") val position: Int,
    val feedback: SimpleText?,
  ) : Text(text, files, format)

}
