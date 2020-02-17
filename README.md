# SpongeMixins Library Mod
Mod that allows you to use mixins in 1.7.10

## How to setup library in dev workspace:
1. Put `SpongeMixins-dev.jar` in the %project%/libs folder and refresh gradle.
2. Add to your `build.gradle` this stuff:<p>
2.1. Add Shadow Plugin to convert all your future `org.spongepowered.*` imports to `shaded.org.spongepowered.*`

```groovy
plugins {
       id "com.github.johnrengelman.shadow" version "1.2.3"
}
```

&emsp;&emsp;2.2 Copy this script to the end of your `build.gradle` and **replace** all `yourmodid` here with the modid that you use in `mixins.modid.json` file.
This script is needed to: 
* Relocate all your `org.spongepowered.*` imports to `shaded.org.spongepowered.*`
* Obfuscate minecraft methods, that are used in Mixin classes
* Add refmap file, that is used by SpongeMixins in runtime


```groovy
def refMap = "${tasks.compileJava.temporaryDir}" + File.separator + "mixins.yourmodid.refmap.json"
shadowJar {
    classifier = ""
    configurations = []
    relocate "org.spongepowered", "shaded.org.spongepowered"

    from refMap
}
tasks.jar.finalizedBy(shadowJar)

def mixinSrg = "${tasks.reobf.temporaryDir}" + File.separator + "mixins.srg"

reobf {
    addExtraSrgFile mixinSrg
}

afterEvaluate {
    tasks.compileJava.options.compilerArgs += ["-AreobfSrgFile=${tasks.reobf.srg}", "-AoutSrgFile=${mixinSrg}", "-AoutRefMapFile=${refMap}"]
}

sourceSets {
    main {
        output.resourcesDir = output.classesDir
        ext.refMap = "mixins.yourmodid.refmap.json"
    }
}
```

&emsp;&emsp;2.3 Add this lines to your manifest attributes. Here you should **replace** all `yourmodid` here with your mod id too.
```groovy
jar {
    manifest {
        attributes([
                "TweakClass"                 : "shaded.org.spongepowered.asm.launch.MixinTweaker",
                "MixinConfigs"               : "mixins.yourmodid.json",
                "FMLCorePluginContainsFMLMod": "true",
        ])
    }
}
```

## Starting magic with mixins or how to add its config
Create `mixins.yourmodid.json` file directly in resources folder (replace `yourmodid` with your modid). 
Here is an example of how to fill it (**remember about changing `yourmodid`**): 
```json
{
  "required": true,
  "minVersion": "0.7.11",
  "package": "com.company.mypackage.mixins",
  "refmap": "mixins.yourmodid.refmap.json",
  "target": "@env(DEFAULT)",
  "compatibilityLevel": "JAVA_8",
  "mixins": [
    "MixinTest1",
    "MixinTest2"
  ],
  "client": [
    "MixinMinecraft"
  ]
}
```
More info here: https://github.com/SpongePowered/Mixin

##How to launch in dev-worspace

Add following Program Agruments: `--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.yourmodid.json` and replace `yourmodid` as mentioned above.

##FAQ
1. Q: `project\build\tmp\reobf\mixins.srg (Can't find given file)` while build<br>
A: Your should run minecraft after you created or added new mixins to generate this file
