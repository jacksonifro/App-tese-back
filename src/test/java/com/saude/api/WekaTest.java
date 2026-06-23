package com.saude.api;

import weka.core.PluginManager;

public class WekaTest {
    public static void test() throws Exception {
        PluginManager.addPlugin("weka.classifiers.Classifier", "weka.classifiers.functions.Logistic", "weka.classifiers.functions.Logistic");
    }
}
