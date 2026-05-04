package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * A multiple-choice question that is answered by selecting one or multiple answers.
 *
 * If [single] is set to true, the question expects that at least one answer has a [Answer.fraction] of [Fraction.Positive.One].
 * The question can have other answers that have full or partial points.
 * If no answer has a [Answer.fraction] of [Fraction.Positive.One], the question can only be answered as partially correct.
 *
 * If [single] is set to false, the question expects that all postive [Answer.fraction]s add up to [Fraction.Positive.One].
 * If the sum of all postive [Answer.fraction]s is less than [Fraction.Positive.One], the question can only be answered as partially correct.
 * It the sum of all postive [Answer.fraction]s is greater than [Fraction.Positive.One], Moodle will grade the question with a fraction of `max(Fraction.Postive.One, actualFraction)`.
 *
 * A negative [Answer.fraction] will reduce points for the answer.
 * In total, a negative score for the whole question can be reached.
 *
 * @property answers the answers to the question
 * @property single whether the question is single- or multi-answered
 * @property shuffle whether the answers are shuffled before being displayed
 * @property showStandardInstructions whether the standard instructions are shown (i.e., "Select one:" or "Select one or more:")
 * @property numbering the numbering of the answers
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @see Answer
 * @see Numbering
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("multichoice")
class MultipleChoice(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<Answer>,
  val single: Boolean,
  @JsonProperty("shuffleansers") @JsonSerialize(using = NumericBooleanSerializer::class) val shuffle: Boolean,
  @JsonProperty("showstandardinstructions") @JsonSerialize(using = NumericBooleanSerializer::class) val showStandardInstructions: Boolean,
  @JsonProperty("answernumbering") val numbering: Numbering,
  @JsonUnwrapped val combinedFeedback: CombinedFeedback,
  @JsonUnwrapped val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * How the answers are numbered.
   */
  enum class Numbering(@get:JsonValue val value: String) {

    /**
     * No numbering is shown.
     */
    None("none"),

    /**
     * The answers are numbered with lowercase letters.
     */
    AbcLowercase("abc"),

    /**
     * The answers are numbered with uppercase letters.
     */
    AbcUppercase("ABCD"),

    /**
     * The answers are numbered with numbers.
     */
    Numerical("123"),

  }

  /**
   * An answer to a multiple-choice question.
   *
   * @property fraction the fraction of the total points that the answer is worth
   * @property feedback the feedback for the answer if it is selected
   * @see Text
   */
  class Answer(
    text: String,
    files: List<File>,
    format: Format,
    @JacksonXmlProperty(isAttribute = true) val fraction: Fraction,
    val feedback: SimpleText?,
  ) : Text(text, files, format)

}
