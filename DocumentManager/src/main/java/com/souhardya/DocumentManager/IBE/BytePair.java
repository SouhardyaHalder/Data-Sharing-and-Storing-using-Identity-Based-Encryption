package com.souhardya.DocumentManager.IBE;

import it.unisa.dia.gas.jpbc.Element;

public class BytePair {

    private Element element1;
    private Element element2;

    public BytePair(Element element1, Element element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public Element getElement1() {
        return element1;
    }

    public Element getElement2() {
        return element2;
    }

}
