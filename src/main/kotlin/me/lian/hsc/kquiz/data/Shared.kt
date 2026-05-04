package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import me.lian.hsc.kquiz.serialization.PresenceBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlCData
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty
import tools.jackson.dataformat.xml.annotation.JacksonXmlText

/**
 * A text that can be formatted.
 * @property text the text
 * @property files additional files that are referenced in the text
 * @property format the format of the text
 * @see Format
 */
sealed class Text(
  @JacksonXmlCData val text: String,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("file") val files: List<File>,
  @JacksonXmlProperty(isAttribute = true) val format: Format
) {

  /**
   * The format of a text.
   */
  enum class Format(@get:JsonValue val value: String) {

    /**
     * The text is formatted as HTML.
     *
     * It can use almost all HTML tags.
     * JavaScript or VB Script are not allowed.
     *
     * The text is usually placed within a table cell, thus it must not contain any head or body tags.
     * It must be valid HTML (i.e., tags must be properly closed and not nested, etc.).
     *
     * Every value that starts with `www.` or `https://` is automatically converted to a link,
     * given that the feature is enabled within Moodle.
     */
    HTML("html"),

    /**
     * The text uses the Moodle auto-format.
     *
     * Moodle will automatically format the text:
     * - It will make links (i.e., text that starts with `www.` or `https://`) clickable.
     * - Emoticons will be displayed as graphics.
     * - Line breaks are interpreted as `<br>` tags.
     * - Empty lines will start a new paragraph.
     * - HTML elements are interpreted as HTML tags.
     *
     * The following emojicons are available:
     *
     * | Code         | Emoticons         |
     * |--------------|-------------------|
     * | :-), :)      | simling           |
     * | :-D          | grinning          |
     * | ;-)          | winking           |
     * | :-/          | mixed feelings    |
     * | V-.          | thinking          |
     * | :-P, :-p     | tongue out        |
     * | B-)          | cool              |
     * | ^-)          | agreed            |
     * | 8-)          | big eyes          |
     * | :o)          | clown             |
     * | :-(, :(      | sad               |
     * | 8-.          | shy               |
     * | :-&#124;     | blushed           |
     * | :-X          | kiss              |
     * | 8-o          | surprised         |
     * | P-&#124;     | blue eye          |
     * | 8-[, (grr)   | angry             |
     * | xx-P         | dead              |
     * | &#124;-.     | sleepy            |
     * | }-]          | devil             |
     * | (h), (heart) | heart             |
     * | (y)          | thumbs up         |
     * | (n)          | thumbs down       |
     * | (martin)     | martin            |
     */
    MoodleAutoFormat("moodle_auto_format"),

    /**
     * The text is plain text.
     * It is not formatted.
     */
    PlainText("plain_text"),

    /**
     * The text is formatted as Markdown.
     * See the [Markdown cheatsheet](https://www.markdownguide.org/cheat-sheet/) for more information.
     */
    Markdown("markdown"),

  }

}

/**
 * A simple that that has no additional properties.
 * @see Text
 */
class SimpleText(text: String, files: List<File>, format: Format) : Text(text, files, format)

/**
 * A file that is referenced in the text or question
 * @property name the name of the file
 * @property content the content of the file
 * @property path the path of the file
 * @property encoding the encoding of the file
 */
class File(
  @JacksonXmlProperty(isAttribute = true) val name: String,
  @JacksonXmlText val content: String,
  @JacksonXmlProperty(isAttribute = true) val path: String,
  @JacksonXmlProperty(isAttribute = true) val encoding: Encoding,
) {

  /**
   * The encoding of a file.
   */
  enum class Encoding(@get:JsonValue val value: String) {

    /**
     * The file is encoded in base84.
     */
    Base64("base64"),
  }

}

/**
 * How the question should behave if the Quiz settings allow multiple tries.
 *
 * The [penalty] behaves the same way for adaptive mode and interactive with multiple tries.
 * It is the factor of the total points of the quiz that get reduced after each wrong try.
 * If, for example, the [penalty] is [Penalty.Third] and the question has a total of 9 points, you get for a fully correct answer
 * - `9` points after the first try,
 * - `9 - (9 * 1/3) = 6` points after the second try,
 * - `9 - 2 * (9 * 1/3) = 3` points after the third try,
 * - `9 - 3 * (9 * 1/3) = 0` points after the fourth and every subsequent try.
 *
 * If the quiz is set to adaptive mode, the student can try the question unlimited times.
 * The total number of points the student receives cannot get less than the points he got on the previous try.
 *
 * If the quiz is set to adaptive mode, the [hints] are ignored.
 * If the quiz is set to interactive with multiple tries, the next [hint][Hint] is shown after each wrong try.
 * If [Hint.showCorrect] is set to true, the number of correctly selected answers is shown,
 * if [Hint.clearWrong] is set to true, the wrong answers are cleared a wrong try.
 * The student has `hints.size + 1` amount of tries.
 *
 * @property penalty the penalty for each wrong try
 * @property hints the hints for each wrong try
 * @see Penalty
 * @see Hint
 */
data class MultipleTries(
  val penalty: Penalty?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("hint") val hints: List<Hint>,
) {

  /**
   * The penalty for each wrong try.
   */
  enum class Penalty(@get:JsonValue val value: Double) {
    Full(100.0),
    Half(50.0),
    Third(33.33333),
    Quarter(25.0),
    Fifth(20.0),
    Tenth(10.0),
    None(0.0),
  }

  /**
   * A hint that contains information on what should happen if a student answers wrong.
   * @property text the text of the hint
   * @property files additional files that are referenced in the hint
   * @property format the format of the hint
   * @property showCorrect whether the number of correctly selected answers should be shown
   * @property clearWrong whether the wrong answers should be cleared after each wrong try
   * @see Text
   */
  class Hint(
    text: String,
    files: List<File>,
    format: Format,
    @JsonSerialize(using = PresenceBooleanSerializer::class) @JsonProperty("shownumcorrect") val showCorrect: Boolean,
    @JsonSerialize(using = PresenceBooleanSerializer::class) @JsonProperty("clearwrong") val clearWrong: Boolean,
  ) : Text(text, files, format)

}

/**
 * The feedback that is shown to the student as soon as they see their grade.
 * @property correctFeedback the feedback that is shown if the student got the correct answer
 * @property partiallyCorrectFeedback the feedback that is shown if the student got a partially correct answer
 * @property showCorrectChecks whether the number of correct answers should be shown to the student
 * @property incorrectFeedback the feedback that is shown if the student got the wrong answer
 * @see Text
 */
data class CombinedFeedback(
  @JsonProperty("correctfeedback") @JsonInclude(JsonInclude.Include.NON_NULL) val correctFeedback: Text?,
  @JsonProperty("partiallycorrectfeedback") val partiallyCorrectFeedback: Text?,
  @JsonProperty("shownumcorrect") @JsonSerialize(using = PresenceBooleanSerializer::class) val showCorrectChecks: Boolean,
  @JsonProperty("incorrectfeedback") val incorrectFeedback: Text?,
)

/**
 * The units for a numerical question.
 *
 * If [unitHandling] is set to something other than [UnitHandling.UnusedOrOptional], there should be at least unit provided via [units].
 * Otherwise, a student will always receive the [unitPenality].
 *
 * The [unitPenality] is deduced, is the fraction of:
 * - The points the student would recive on the question, if [unitHandling] is [UnitHandling.RequiredResponsePenalty];
 * - The points of the total achiveable points on the question, if [unitHandling] is [UnitHandling.RequiredQuestionPenalty].
 *
 * It is deduced if the student provides any (known) unit or provides the correct answer in the wrong unit.
 *
 * The first entry in [units] should have a multipler of `1.0`.
 * Otherwise, the value from the answer will be multiplier of the first unit for the correct answer.
 * In addition, the correct answer will be displayed wrong in the Moodle Web UI.
 *
 * If [unitHandling] is set to [UnitHandling.UnusedOrOptional] units are not used if [units] is empty and optional otherwise.
 * If optional, a answer without any unit is the same as an answer with the first unit.
 *
 * @property unitHandling how the unit should be handled
 * @property unitPenality the penalty for providing the wrong unit
 * @property unitLocation the location of the units
 * @property units the units for the question
 * @see UnitHandling
 * @see UnitLocation
 */
data class Units(
  @JsonProperty("unitgradingtype") val unitHandling: UnitHandling,
  @JsonProperty("unitpenalty") val unitPenality: Double?,
  @JsonProperty("showunits") val unitDisplayMode: UnitDisplayMode,
  @JsonProperty("unitsleft") val unitLocation: UnitLocation,
  @JacksonXmlElementWrapper(localName = "units") @JsonProperty("unit") val units: List<UnitEntry>,
) {


  /**
   * How the unit should be handled.
   */
  enum class UnitHandling(@get:JsonValue val value: Int) {
    /**
     * Units are not used or optional, depending on whether [units] is empty.
     */
    UnusedOrOptional(0),

    /**
     * Units are required.
     * The penality for not using units or a wrong unit is a fraction of the points the user would get on the question.
     */
    RequiredResponsePenalty(1),

    /**
     * Units are required.
     * The penality for not using units or a wrong unit is a fraction of the total achievable points on the question.
     */
    RequiredQuestionPenalty(2),
  }

  /**
   * How the units should be displayed.
   */
  enum class UnitDisplayMode(@get:JsonValue val value: Int) {
    /**
     * The student can or must enter the unit in the text field.
     */
    TextInput(0),

    /**
     * The student can or must select the unit from single choice radio buttons.
     */
    MultipleChoice(1),

    /**
     * The student can or must select the unit from a dropdown menu.
     */
    Dropdown(2),
  }

  /**
   * Where the units should be displayed.
   */
  enum class UnitLocation(@get:JsonValue val value: Int) {
    /**
     * Units are to the right of the answer.
     */
    Right(0),

    /**
     * Units are to the left of the answer.
     */
    Left(1),
  }

  /**
   * An entry in the list of units.
   * @property name the name of the unit
   * @property multiplier the multiplier of the unit, i.e., how many of the unit are in one of the first unit in [units]
   * @see Units
   */
  data class UnitEntry(
    @JsonProperty("unit_name") val name: String,
    val multiplier: Double,
  )

}

/**
 * The fraction of the total points that the answer is worth.
 */
sealed interface Fraction {

  /**
   * The fraction of the total points that the answer is worth.
   */
  @get:JsonValue val value: Double

  /**
   * Positive fractions.
   */
  enum class Positive(override val value: Double) : Fraction {
    One(100.0),
    NineTenths(90.0),
    FiveSixths(83.33333),
    FourFifths(80.0),
    ThreeFourths(75.0),
    SevenTenths(70.0),
    TwoThirds(66.66667),
    ThreeFifths(60.0),
    OneHalf(50.0),
    TwoFifths(40.0),
    OneThird(33.33333),
    ThreeTenths(30.0),
    OneFourth(25.0),
    OneFifth(20.0),
    OneSixth(16.66667),
    OneSeventh(14.28571),
    OneTenth(10.0),
    OneTwentieth(5.0),
  }

  /**
   * Zero fraction.
   */
  object Zero : Fraction {
    override val value = 0.0
  }

  /**
   * Negative fractions.
   */
  enum class Negative(positiveValue: Double) : Fraction {
    One(100.0),
    NineTenths(90.0),
    FiveSixths(83.33333),
    FourFifths(80.0),
    ThreeFourths(75.0),
    SevenTenths(70.0),
    TwoThirds(66.66667),
    ThreeFifths(60.0),
    OneHalf(50.0),
    TwoFifths(40.0),
    OneThird(33.33333),
    ThreeTenths(30.0),
    OneFourth(25.0),
    OneFifth(20.0),
    OneSixth(16.66667),
    OneSeventh(14.28571),
    OneTenth(10.0),
    OneTwentieth(5.0);

    override val value: Double = -positiveValue
  }

}

/**
 * A wrapper for a value wich is passed as `<name><text>[content]</text></name>`.
 * @property text the text of the value
 */
data class WrappedText<T>(val text: T)
