package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A question that is answered by selecting the correct answer from a list of answers for every subquestion.
 *
 * The question expects that for each subquestion, there is exactly one correct answer.
 * You can provide additional answers with a blank question to provide wrong answers that are never used.
 *
 * @property answers the answers to the question
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @see Answer
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("matching")
class Matching(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("subquestion") val answers: List<Answer>,
  @JsonUnwrapped val combinedFeedback: CombinedFeedback,
  @JsonUnwrapped val multipleTries: MultipleTries,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * A subquestion that can be answered by selecting the correct answer from a list of answers.
   *
   * @property answer the correct answer to the subquestion
   * @see Text
   */
  class Answer(
    text: String,
    files: List<File>,
    format: Format,
    val answer: WrappedText<String>,
  ) : Text(text, files, format)

}