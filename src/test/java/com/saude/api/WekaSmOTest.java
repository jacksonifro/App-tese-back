package com.saude.api;

import weka.classifiers.functions.SMO;

public class WekaSmOTest {
    public static void test() {
        SMO smo = new SMO();
        smo.setBuildCalibrationModels(true);
    }
}
