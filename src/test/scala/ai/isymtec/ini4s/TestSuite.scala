package ai.isymtec.ini4s

import com.google.inject.{Guice, Injector}
import org.scalatest._
import scala.io.Source

class TestSuite extends WordSpec {

  val injector: Injector = Guice.createInjector(new TestDependencyModule)
  val parser: AbstractIniParser = injector.getInstance(classOf[AbstractIniParser])

  "IniParser.parse()" should {
    "return correct value of the first parameter" in {

      val lines: List[String] = Source.fromResource("test1.ini").getLines().toList
      val parsed = parser.parse(lines)
      assert(parsed("Basic")("code_form") == "5")
    }
  }

  it should {
    "return correct value of the last parameter" in {
      val lines: List[String] = Source.fromResource("test1.ini").getLines().toList
      val parsed = parser.parse(lines)
      assert(parsed("Results")("results_autosave_2") == "1")
    }
  }

  "IniParser.load()" should {
    "throw" in {
      val NonExistinPath = "Z:/NonExisting.ini"
      assertThrows[java.io.FileNotFoundException](parser.load(NonExistinPath))
    }
  }

}
