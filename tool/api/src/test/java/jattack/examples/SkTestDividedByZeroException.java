package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestDividedByZeroException {

    @Entry
    static void m() {
        int x = arithmetic(intVal(), asInt(0), DIV, MOD).eval();
        int y = intVal().eval();
    }
}
