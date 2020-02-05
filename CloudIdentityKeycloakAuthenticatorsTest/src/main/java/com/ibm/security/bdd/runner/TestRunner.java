package com.ibm.security.bdd.runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
	tags = { "@TEST" }
)

public class TestRunner extends BaseTestOptions {

}
