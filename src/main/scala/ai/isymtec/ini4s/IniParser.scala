package ai.isymtec.ini4s

import java.nio.file.{Path, Paths}
import scala.io.Source

trait AbstractIniParser {

  /**
    * Loads and parses ini file
    * @param pathString Path string
    * @return
    */
  def load(pathString: String): Map[String, Map[String, String]]

  /**
    * Loads and parses ini file
    * @param path java.nio.Path
    * @return
    */
  def load(path: Path): Map[String, Map[String, String]]

  /**
    * Parses a list of strings to a map of sections and parameters
    * @param lines List of strings
    * @return
    */
  def parse(lines: List[String]): Map[String, Map[String, String]]

}

/**
  * Simple ini parser
  */
class IniParser extends AbstractIniParser {

  def load(pathString: String): Map[String, Map[String, String]] =
    load(Paths.get(pathString))

  def load(path: Path): Map[String, Map[String, String]] = {
    val source = Source.fromFile(path.toFile)
    val lines = source.getLines().toList
    source.close
    parse(lines)
  }

  def parse(lines: List[String]): Map[String, Map[String, String]] = {

    def innerParse(lines: List[String], section: String, acc: Map[String, Map[String, String]]): Map[String, Map[String, String]] = {
      def isComment(s: String): Boolean = s.startsWith(";") || s.startsWith("#")

      def isDataStart(s: String): Boolean = s.startsWith("<")

      def isSection(s: String): Boolean = s.startsWith("[")

      def trimSection(s: String): String = s.replaceAll("^\\[", "").replaceAll("\\]$", "")

      def keyValue(s: String): Option[(String, String)] = {
        val Pattern = "(.*)=(.*)".r
        s.trim() match {
          case Pattern(k, v) => Some(k.trim, v.trim)
          case f => None //TODO: ignoring everything which is not a key-value pair
        }
      }

      lines match {
        case Nil =>
          acc
        case x :: xs =>
          /* Comment, do nothing */
          //TODO: Comment, there is a section
          //TODO: Comment, there is no section

          val newAcc: Map[String, Map[String, String]] = if (acc.isEmpty) acc + ("" -> Map.empty) else acc

          if (isDataStart(x)) acc //TODO: ignoring <data2_start> etc.

          else if (isComment(x)) innerParse(xs, section, newAcc + (section -> (newAcc(section) ++ Map(x -> "#"))))
          else if (x.isEmpty) innerParse(xs, section, newAcc)

          //TODO: Section -> start a new section
          else if (isSection(x))
          /* Section [...] found, pass further, add section to acc*/
            innerParse(xs, trimSection(x), newAcc + (trimSection(x) -> Map.empty))
          else {
            /* Value found, add to previous section and add to acc */
            keyValue(x) match {
              case Some((key, value)) => innerParse(xs, section, newAcc + (section -> (newAcc(section) ++ Map(key -> value))))
              case _ => innerParse(xs, section, newAcc)
            }

          }
      }
    }

    innerParse(lines, "", Map.empty)

  }

}