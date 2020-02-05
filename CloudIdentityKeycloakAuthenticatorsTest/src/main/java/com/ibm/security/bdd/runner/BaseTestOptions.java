package com.ibm.security.bdd.runner;

import io.cucumber.junit.CucumberOptions;

@CucumberOptions(
	monochrome = true,
	features = { "classpath:./" },
	glue = { "com.ibm.security.bdd.steps" })
public abstract class BaseTestOptions {

}