# SpongeMixins Library Mod
Mod that allows you to use mixins in 1.7.10

## How to setup library in dev workspace:
1. Put `SpongeMixins-dev.jar` in the %project%/libs folder and refresh gradle.<br><br>
1.1. Add this string to the dependencies in your main class of mod: `required-after:spongemixins;`<br><br>
2. Add this stuff to your `build.gradle`:<p>
2.1. Add Shadow Plugin to convert all your future `org.spongepowered.*` imports to `shaded.org.spongepowered.*`

```groovy
plugins {
       id "com.github.johnrengelman.shadow" version "1.2.3"
}
```

&emsp;&emsp;2.2 Add SpongeMixins library to dependencies and provide maven in repositories:
```groovy
repositories {
    maven {
        name = "sponge"
        url = "http://repo.spongepowered.org/maven/"
    }
}

dependencies {
    compile('org.spongepowered:mixin:0.7.11-SNAPSHOT') {
        // Mixin includes a lot of dependencies that are too up-to-date
        exclude module: 'launchwrapper'
        exclude module: 'guava'
        exclude module: 'gson'
        exclude module: 'commons-io'
        exclude module: 'log4j-core'
    }
}
```
&emsp;&emsp;2.3 Add these lines to your manifest attributes and **replace** all `yourmodid` here with the modid that you use in `mixins.modid.json` file.
```groovy
jar {
    manifest {
        attributes([
                "TweakClass": "shaded.org.spongepowered.asm.launch.MixinTweaker",
                'FMLCorePluginContainsFMLMod': 'true',
                "ForceLoadAsMod": true,
                'MixinConfigs': 'mixins.yourmodid.json'
        ])
    }
}
```
&emsp;&emsp;2.4 Copy this script to the end of your `build.gradle`. Here you should **replace** all `yourmodid` with your mod id too.
This script is needed to: 
* Relocate all your `org.spongepowered.*` imports to `shaded.org.spongepowered.*`
* Obfuscate minecraft methods, that are used in Mixin classes
* Add refmap file that is used by SpongeMixins in runtime


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

## How to launch in dev-workspace

Add following Program Agruments:<br> `--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.yourmodid.json`<br> and replace `yourmodid` as mentioned above.

## FAQ
1. Q: `project\build\tmp\reobf\mixins.srg (Can't find given file)` while build<br>
A: You should run minecraft after you created or added new mixins to generate this file
