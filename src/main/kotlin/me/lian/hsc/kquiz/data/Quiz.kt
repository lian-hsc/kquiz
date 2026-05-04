package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A Moodle quiz that follows the Moodle XML format so that it can easily be exported to XML and imported to Moodle.
 *
 * The XML importer uses a "best effort" approach.
 * If the XML file is not valid, the importer will fail.
 * However, the importer will not validate if the imported questions are according to the requirements the Moodle Web UI has.
 * Thus, you can import very weird question that might break moodle.
 * Those restrictions are documented as KDoc comments on the respective [QuizEntries][QuizEntry].
 * In addition, the documentation contains information on what happens if you break those restrictions.
 *
 * @property entries the question entries
 */
@JsonRootName("quiz")
data class Quiz(
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("question")
  val entries: List<QuizEntry>
)