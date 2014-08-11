import sbt._
import scalariform.formatter.preferences._

// direct formatter to deal with a small number of domain objects
// if we had to do this for general objects, it would make sense
// to create a series of implicit convertors to an SExp hierarchy
object SExpFormatter {

  def toSExp(s: String): String =
    "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

  def toSExp(f: File): String = toSExp(f.getAbsolutePath)

  def fsToSExp(ss: Iterable[File]): String =
    if (ss.isEmpty) "nil"
    else ss.map(toSExp).mkString("(", " ", ")")

  def ssToSExp(ss: Iterable[String]): String =
    if (ss.isEmpty) "nil"
    else ss.map(toSExp).mkString("(", " ", ")")

  def msToSExp(ss: Iterable[EnsimeModule]): String =
    if (ss.isEmpty) "nil"
    else ss.map(toSExp).mkString("(", " ", ")")

  def fToSExp(key: String, op: Option[File]): String =
    op.map { f => s":$key ${toSExp(f)}" }.getOrElse("")

  def sToSExp(key: String, op: Option[String]): String =
    op.map { f => s":$key ${toSExp(f)}" }.getOrElse("")

  def toSExp(b: Boolean): String = if (b) "t" else "nil"

  def toSExp(f: IFormattingPreferences): String =
    if (f.preferencesMap.isEmpty) "nil"
    else f.preferencesMap.map {
      case (desc, v: Boolean) =>
        s":${desc.key} ${toSExp(v)}"
      case (desc, v: Int) =>
        s":${desc.key} $v"
    }.mkString(" ")

  // a lot of legacy key names and conventions
  def toSExp(c: EnsimeConfig): String = s"""(
 :root ${toSExp(c.root)}
 :cacheDir ${toSExp(c.cacheDir)}
 :name "${c.name}"
 ${fToSExp("java-home", c.javaHome)}
 :java-flags ${ssToSExp(c.javaFlags)}
 :reference-source-roots ${fsToSExp(c.javaSrc.toIterable)}
 :scala-version ${toSExp(c.scalaVersion)}
 :compiler-args ${ssToSExp(c.compilerArgs)}
 :formatting-prefs (${toSExp(c.formatting)})
 :subprojects ${msToSExp(c.modules.values)}
 ${c.raw}
)"""

  // a lot of legacy key names and conventions
  def toSExp(m: EnsimeModule): String = s"""(
   :name ${toSExp(m.name)}
   :module-name ${toSExp(m.name)}
   :source-roots ${fsToSExp((m.mainRoots ++ m.testRoots))}
   :target ${toSExp(m.target)}
   :test-target ${toSExp(m.testTarget)}
   :depends-on-modules ${ssToSExp(m.dependsOnNames)}
   :compile-deps ${fsToSExp(m.compileJars)}
   :runtime-deps ${fsToSExp(m.runtimeJars)}
   :test-deps ${fsToSExp(m.testJars)}
   :reference-source-roots ${fsToSExp(m.sourceJars)})"""
}
