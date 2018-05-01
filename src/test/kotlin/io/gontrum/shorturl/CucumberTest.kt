package io.gontrum.shorturl

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
/*
There seems to be a bug in the intellij runner https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000609904-java-lang-IllegalArgumentException-Not-a-file-or-directory-xxxxxx-idea-modules-src-test-resources-features
For running in intelliJ idea, please use the following line:
 */
//@CucumberOptions(features = ["../../src/test/resources/cucumber/features"])
@CucumberOptions(features = ["src/test/resources/cucumber/features"])
class CucumberTest