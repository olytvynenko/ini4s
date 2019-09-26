package ai.isymtec.ini4s

import java.io.{File, FileOutputStream, OutputStreamWriter}

object IniParser {

  def load(path: String): Map[String, Map[String, String]] = load(new File(path))

  def load(file: File): Map[String, Map[String, String]] = {
    val lines: List[String] = scala.io.Source.fromFile(file, "8859_1").getLines().toList
    parse(lines, "", Map.empty)
  }

  def save(data: Map[String, Map[String, String]], path: String): Unit = {
    val outputStreamWriter = new OutputStreamWriter(new FileOutputStream(path), "8859_1")
    outputStreamWriter.write(write(data))
    outputStreamWriter.close()
  }

  def write(data: Map[String, Map[String, String]]): String = {
    val list = data.map{case (k: String, v: Map[String, String]) => (k, v.toList)}.toList
    def writeSection(data: List[(String, List[(String, String)])], dataString: String): String = {
      def addBrackets(s: String): String = "[" + s + "]"
      val start = if (dataString.isEmpty) "" else dataString + "\n\n"
      def writeValue(values: List[(String, String)], valueString: String): String = {
        def writePair(keyValue: (String, String)) = {
          if (keyValue._2 == "#") keyValue._1
          else keyValue._1 + " = " + keyValue._2
        }
        values match {
          case x :: Nil => valueString + '\n' + writePair(x)
          case x :: xs => writeValue(xs, valueString + '\n' + writePair(x))
          case Nil => valueString
        }
      }
      data match {
        case x :: Nil =>
          start + addBrackets(x._1) + writeValue(x._2, "")
        case x :: xs =>
          val s = start + addBrackets(x._1) + writeValue(x._2, "")
          writeSection(xs, s)
        case Nil => dataString
      }
    }
    writeSection(list, "")
  }

  def parse(lines: List[String], section: String, acc: Map[String, Map[String, String]]): Map[String, Map[String, String]] = {
    def isComment(s: String): Boolean = s.startsWith(";") || s.startsWith("#")
    def isSection(s: String): Boolean = s.startsWith("[")
    def trimSection(s: String): String = s.replaceAll("^\\[", "").replaceAll("\\]$", "")
    def keyValue(s: String): (String, String) = {
      val Pattern = "(.*)=(.*)".r
      s match {
        case Pattern(k, v) => (k.trim, v.trim)
        case _ => throw new IllegalArgumentException(s)
      }
    }
    lines match {
      case x :: Nil =>
        /* Comment, do nothing */
        //TODO: comment
        if (isComment(x) || x.isEmpty) acc
        else if (isSection(x)) acc + (trimSection(x) -> Map.empty)
        else {
          val (key, value) = keyValue(x)
          acc + (section -> (acc(section) ++ Map(key -> value)))
        }
      case x :: xs =>
        /* Comment, do nothing */
        if (isComment(x)) parse(xs, section, acc + (section -> (acc(section) ++ Map(x -> "#"))))
        else if (x.isEmpty) parse(xs, section, acc)
        else if (isSection(x))
        /* Section [...] found, pass further, add section to acc*/
          parse(xs, trimSection(x), acc + (trimSection(x) -> Map.empty))
        else {
          /* Value found, add to previous section and add to acc */
          val (key, value) = keyValue(x)
          parse(xs, section, acc + (section -> (acc(section) ++ Map(key -> value))))
        }
      case Nil =>
        acc
    }


  }

}