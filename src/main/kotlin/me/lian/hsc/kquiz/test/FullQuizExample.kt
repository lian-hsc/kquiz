package me.lian.hsc.kquiz.test

import me.lian.hsc.ktypst.data.output.Artifact
import me.lian.hsc.kquiz.dsl.*
import me.lian.hsc.kquiz.dsl.ClozeMultipleChoiceDsl.Type.Dropdown
import me.lian.hsc.kquiz.dsl.ClozeMultipleChoiceDsl.Type.RadioHorizontal
import me.lian.hsc.kquiz.data.Ordering
import me.lian.hsc.kquiz.serialization.toXml

fun main() {
  val quiz = kquiz {
    category {
      name = "Testing things"

      embeddedAnswer {
        name = "Match the things"

        question {
          p {
            +"Match the things"
          }
          p {
            image("raw.png", byteArrayOf(1, 2, 3, 4))
            image("artifact.png", Artifact(byteArrayOf(5, 6, 7, 8)))
          }
          p {
            +"A "
            multipleChoice {
              type = Dropdown
              score = 1.0
              option("Something")
              option("Correct") { score = Score.Correct }
              option("Half correct") { score = Score.Percentage(0.5) }
            }
          }
          p {
            +"B "
            multipleChoice {
              type = RadioHorizontal
              shuffle = true
              option("Wrong")
              option("Right") { score = Score.Correct }
            }
          }
          p {
            +"C "
            multiResponse {
              layout = ClozeMultiResponseDsl.Layout.Horizontal
              shuffle = true
              option("Red") { score = Score.Correct }
              option("Blue") { score = Score.Correct }
              option("Green")
            }
          }
          p {
            +"The capital of France is "
            shortAnswer {
              answer("Paris") { score = Score.Correct }
              answer("paris") { score = Score.Percentage(0.5) }
            }
            +"."
          }
          p {
            +"Pi is approximately "
            numeric {
              answer(3.14, tolerance = 0.01)
            }
          }
        }
      }

      selectMissingWords {
        name = "Select Missing Words"
        defaultGrade = 1.0

        question {
          p {
            +"The "
            gap("cat")
            +" "
            gap("sat")
            +" on the "
            gap("mat")
            +"."
          }
        }

        option("dog")
        option("table")
      }

      ordering {
        name = "Ordering"
        defaultGrade = 1.0
        grading = Ordering.Grading.RelativeOnePreviousAndNext

        question {
          p {
            +"Order the following steps."
          }
        }

        item("Boil the water")
        item("Add the pasta")
        item("Wait ten minutes") {
          feedback { +"Roughly, depending on the pasta." }
        }
        item("Drain the water")
      }
    }
  }

  println(quiz.toXml())
}
