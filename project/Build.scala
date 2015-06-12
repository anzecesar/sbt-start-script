import sbt._

import Keys._
import Project.Initialize
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

object StartScriptBuild extends Build {
    def formatPrefs = {
        import scalariform.formatter.preferences._
        FormattingPreferences()
           .setPreference(IndentSpaces, 4)
    }

    lazy val root =
        Project("root", file("."), settings = rootSettings)

    lazy val rootSettings = Defaults.defaultSettings ++
        ScriptedPlugin.scriptedSettings ++
        // formatting
        SbtScalariform.scalariformSettings ++ Seq(
            ScalariformKeys.preferences in Compile := formatPrefs,
            ScalariformKeys.preferences in Test    := formatPrefs) ++
        Seq(sbtPlugin := true,
            organization := "org.slackoverflow.sbt",
            name := "sbt-start-script",
            scalacOptions := Seq("-unchecked", "-deprecation"),

            // to release, bump major/minor/micro as appropriate,
            // drop SNAPSHOT, tag and publish.
            // add snapshot back so git master is a SNAPSHOT.
            // when releasing a SNAPSHOT to the repo, bump the micro
            // version at least.
            // Also, change the version number in the README.md
            // Versions and git tags should follow: http://semver.org/
            // except using -SNAPSHOT instead of without hyphen.

            version := "0.10.1",
            libraryDependencies <++= sbtVersion { (version) =>
		          Seq("org.scala-sbt" % "io" % version % "provided",
			            "org.scala-sbt" % "logging" % version % "provided",
			            "org.scala-sbt" % "process" % version % "provided")
            },

            // publish stuff
            publishTo <<= (version) { v =>
              def trycom(repo: String) = ("trycom " + repo, s"http://trycom.artifactoryonline.com/trycom/plugins-$repo-local")
              val (name, repo) = if (v.endsWith("-SNAPSHOT")) trycom("snapshots") else trycom("release")
              Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
            },
            publishMavenStyle := false,
            credentials += Credentials(Path.userHome / ".ivy2" / ".sbt-credentials"))
}
