package controllers.oms.custom;

import controllers.oms.custom.impl.unitTest.UnitTestCustomImpl;

public class CustomManager {
    public static CustomInterface getInstance(){
        return new UnitTestCustomImpl();
    }
}
