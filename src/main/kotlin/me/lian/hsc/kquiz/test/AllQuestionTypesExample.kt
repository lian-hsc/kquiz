package me.lian.hsc.kquiz.test

import me.lian.hsc.kquiz.data.Calculated
import me.lian.hsc.kquiz.data.Fraction
import me.lian.hsc.kquiz.dsl.image
import me.lian.hsc.kquiz.dsl.kquiz
import me.lian.hsc.kquiz.serialization.saveTo
import me.lian.hsc.kquiz.serialization.toXml

fun main() {
  val quiz = kquiz {
    category {
      name = "All question types"

      multipleChoice {
        name = "Capitals"
        single = true
        shuffle = true

        question { +"What is the capital of France?" }

        option("Paris") { fraction = Fraction.Positive.One }
        option("Berlin")
        option("Madrid")
      }

//      trueFalse {
//        name = "Sky"
//        correctAnswer = true
//
//        question { +"The sky is blue." }
//        trueFeedback { +"Correct!" }
//        falseFeedback { +"Not quite." }
//      }
//
//      shortAnswer {
//        name = "Greeting"
//
//        question { +"Say hello in French." }
//
//        answer("Bonjour") { fraction = Fraction.Positive.One }
//        answer("bonjour") { fraction = Fraction.Positive.OneHalf }
//      }
//
//      numerical {
//        name = "Pi"
//
//        question { +"What is pi, to two decimal places?" }
//
//        answer(3.14, tolerance = 0.005)
//      }
//
//      matching {
//        name = "Capitals matching"
//
//        question { +"Match the country to its capital." }
//
//        pair("France", "Paris")
//        pair("Germany", "Berlin")
//        distractor("Rome")
//      }
//
//      calculated {
//        name = "Rectangle area"
//
//        question { +"A rectangle is {a} by {b}. What is its area?" }
//
//        answer("{a} * {b}")
//
//        dataset("a") {
//          minimum = 1.0
//          maximum = 10.0
//          item(2.0)
//        }
//        dataset("b") {
//          minimum = 1.0
//          maximum = 10.0
//          item(3.0)
//        }
//      }
//
//      calculatedMultipleChoice {
//        name = "Rectangle area choice"
//
//        question { +"A rectangle is {a} by {b}. What is its area?" }
//
//        option("{=({a} * {b})}") { fraction = Fraction.Positive.One }
//        option("{a} + {b}")
//
//        dataset("a") {
//          minimum = 1.0
//          maximum = 10.0
//          status = Calculated.Dataset.Status.Shared
//          item(2.0)
//        }
//        dataset("b") {
//          minimum = 1.0
//          maximum = 10.0
//          status = Calculated.Dataset.Status.Shared
//          item(3.0)
//        }
//      }
    }
//    category {
//      name = "Drag and drop"
//      dragAndDropOntoImage {
//        name = "Onto image"
//        background = image("bg.png", byteArrayOf(1, 2, 3))
//
//        question { +"Drag the labels onto the picture." }
//
//        val cat = item(text = "Cat")
//        val dog = item(text = "Dog")
//        dropzone(cat, x = 10, y = 10)
//        dropzone(dog, x = 100, y = 100)
//      }
//
//      dragAndDropIntoText {
//        name = "Into text"
//
//        question {
//          +"The "
//          drag("cat")
//          +" sat on the mat."
//        }
//
//        distractor("dog")
//      }
//
//      dragAndDropMarkers {
//        name = "Markers"
//        background = image("map.png", byteArrayOf(4, 5, 6))
//
//        question { +"Mark the capital." }
//
//        val paris = marker("Paris")
//        circle(paris, centerX = 50.0, centerY = 50.0, radius = 10.0)
//      }
//    }
  }

  quiz.saveTo(OsFile("example.xml").toPath())
  println(quiz.toXml())
}
