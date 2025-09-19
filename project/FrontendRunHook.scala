import play.sbt.PlayRunHook
import sbt.*

import scala.sys.process.Process

object FrontendRunHook {
  def apply(base: File): PlayRunHook = {
    object UIBuildHook extends PlayRunHook {

      var install: String = FrontendCommands.dependencyInstall
      var run: String = FrontendCommands.serve
      var kill: String = FrontendCommands.kill

      // Windows requires npm commands prefixed with cmd /c
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        install = "cmd /c" + install
        run = "cmd /c" + run
        kill = "cmd /c" + kill
      }

      /** Executed before play run start. Run npm install if node modules are
       * not installed.
       */
      override def beforeStarted(): Unit = {
        if (!(base / "ui" / "node_modules").exists())
          Process(install, base / "ui").!
      }

      /** Executed after play run start. Run npm start
       */
      override def afterStarted(): Unit = {
        Process(run, base / "ui", "CI" -> "true").run(false)
      }

      /** Executed after play run stop. Cleanup frontend execution processes.
       */
      override def afterStopped(): Unit = {
        println("Stopping frontend")
        Process(kill, base / "ui").!
      }
    }
    UIBuildHook
  }
}
