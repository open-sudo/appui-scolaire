package org.acme.funqy;

import io.quarkus.funqy.Funq;

public class PrimitiveFunctions {

    @Funq("v1/tag/hello")
    public String hello() {
        return "Hello World";
    }

    @Funq()
    public String toLowerCase(String val) {
        return val.toLowerCase();
    }

    @Funq("double")
    public int doubleIt(int val) {
        return val + val;
    }


}
