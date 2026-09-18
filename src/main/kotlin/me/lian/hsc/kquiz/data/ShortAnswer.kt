package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * A question that is answered by entering a short piece of text, matched literally (optionally ignoring case)
 * against the [answers].
 *
 * @property answers the answers to the question
 * @property caseSensitive whether the answers must match the case of the student's response
 * @property multipleTries handling of multiple tries for the question
 * @see Answer
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("shortanswer")
class ShortAnswer(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<Answer>,
  @JsonProperty("usecase") @JsonSerialize(using = NumericBooleanSerializer::class) val caseSensitive: Boolean,
  @JsonUnwrapped val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * An answer to a short-answer question.
   * @property fraction the fraction of the total points that the answer is worth
   * @property feedback the feedback for the answer
   * @see Text
   */
  class Answer(
    text: String,
    files: List<File>,
    format: Format,
    @JacksonXmlProperty(isAttribute = true) val fraction: Fraction,
    val feedback: Text?,
  ) : Text(text, files, format)

}