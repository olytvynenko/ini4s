package ai.isymtec.ini4s

import com.google.inject.{AbstractModule, Guice, Injector}
import net.codingwell.scalaguice.ScalaModule

class TestDependencyModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    bind[AbstractIniParser].to[IniParser]

  }
}
