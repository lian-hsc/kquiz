package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped

/**
 * A question, also known as "Cloze", that embeds its sub-questions directly within the [question] text.
 *
 * Sub-questions are embedded using the syntax `{weight:type:answers}`, e.g. `{1:MC:test~=test5}`.
 * See the [Embedded Answers (Cloze) documentation](https://docs.moodle.org/en/Embedded_Answers_(Cloze)_question_type)
 * for the syntax of the individual sub-question types.
 * This class does not parse or validate that syntax; the [question] text is passed through to Moodle as is.
 *
 * As the sub-questions carry their own weight, feedback and correct answers within the [question] text,
 * this question has, in contrast to every other [Question], neither a [Question.defaultGrade] nor a [CombinedFeedback].
 * Moodle calculates the grade of the question from the sum of the weights of the embedded sub-questions.
 *
 * @property multipleTries handling of multiple tries for the question
 * @see MultipleTries
 * @see Question
 */
@JsonTypeName("cloze")
class EmbeddedAnswer(
  name: WrappedText<String>,
  question: SimpleText,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JsonUnwrapped val multipleTries: MultipleTries,
) : Question(name, question, null, tags, generalFeedback, hidden)
