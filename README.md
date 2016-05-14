# Findbugs plugin annotation

This is an Annotation Processor to generate XML files for Findbugs Plugin.
Currently this Annotation Processor can generate `findbugs.xml`, and will support `messages.xml` in near future.


## Motivation

Findbugs plugin is awesome: it can add more detectors to find problem by static analysis.

But it's little troublesome to develop, because we need to make complex XML files and package them to .jar file. I hope that annotation processing can be help to reduce this trouble.


# How to use

Depend on this plugin, and annotate your custom detectors with `@Detector` annotation.
Refer [SampleDetector.java](src/test/resources/SampleDetector.java) as example.

To use this annotation processor by Maven, please add following XML snippet to your `pom.xml`:

```xml
  <dependency>
    <groupId>jp.skypencil.findbugs</groupId>
    <artifactId>findbugs-plugin-annotation</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <optional>true</optional>
  </dependency>
```


# Changelog

## v0.1.0

* First release


# Copyright and license

    Copyright 2016 Kengo TODA
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
