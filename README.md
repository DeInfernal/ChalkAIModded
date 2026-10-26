<div align=center>
<a href="https://www.curseforge.com/minecraft/mc-mods/chalk"><img src="https://raw.githubusercontent.com/mortuusars/resources/main/media/chalk/main.png" width="256"><a>
  
Mod for drawing with chalk in Minecraft

### [Changelog](Changelog.md) | [Tracelog](Tracelog.md)
  
[Curseforge](https://www.curseforge.com/minecraft/mc-mods/chalk)
 | 
[Modrinth](https://modrinth.com/mod/chalk-mod)
  
</div>


## Builds and releases

Use JDK 21 and the repository Gradle wrapper:

```sh
bash gradlew :fabric:build :neoforge:build
```

The [Build and release workflow](https://github.com/DeInfernal/ChalkAIModded/actions/workflows/build-release.yml)
builds both Minecraft 1.21.1 loaders on pushes and pull requests. Download each
loader's installable JAR and ZIP from the run's artifacts (retained for 14 days).

To publish both loaders to GitHub Releases, push a tag matching
`v<mod_version>` from `gradle.properties`, or run the workflow manually and
enable `publish`. Manual publication creates the same version tag at the selected
commit when it does not exist. An existing tag must point to that exact commit.
Update the latest entry in [Changelog.md](Changelog.md) before publishing.
The workflow uses its built-in GitHub token; no additional secrets are needed.
