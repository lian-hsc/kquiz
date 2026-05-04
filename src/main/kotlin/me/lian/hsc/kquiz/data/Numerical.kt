package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * A question that is answered by entering a numerical value.
 * It has similair probabilites to a [ShortAnswer] question, but the answer is checked by comparing the numerical value of the answer to the correct answer.
 *
 * The question should have at least one [Answer] which has a [Answer.fraction] of [Fraction.Positive.One].
 * If no answer has a [Answer.fraction] of [Fraction.Positive.One], the question can only be answered as partially correct.
 * Multiple answers can have a [Answer.fraction] of [Fraction.Positive.One].
 * If an answer is entered, which is not listed in the [answers] list, the question will recive a grade of [Fraction.Zero].
 *
 * The [Answer.tolerance] property can be used to specify a tolerance for the answer.
 * If the answer is within the tolerance, the question will recive the grade specified by the [Answer.fraction] property.
 * If the answer is outside the tolerance, the question will recive a grade of [Fraction.Zero].
 * If the answer is within multiple [answers], the [answer][Answer] with the highest [Answer.tolerance] will be used.
 *
 * @property answers the answers to the question
 * @property units the units of the question
 * @property multipleTries handling of multiple tries for the question
 * @see Answer
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("numerical")
class Numerical(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<Answer>,
  @JsonUnwrapped val units: Units,
  val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * An answer to a numerical question.
   * @property fraction the fraction of the total points that the answer is worth
   * @property tolerance the tolerance of the answer
   * @property feedback the feedback for the answer
   * @see Text
   */
  class Answer(
    text: String,
    files: List<File>,
    format: Format,
    @JacksonXmlProperty(isAttribute = true) val fraction: Fraction.Positive,
    val feedback: SimpleText?,
    val tolerance: Double?,
  ) : Text(text, files, format)

}