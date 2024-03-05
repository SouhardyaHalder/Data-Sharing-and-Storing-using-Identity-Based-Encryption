package com.souhardya.DocumentManager.IBE;

import it.unisa.dia.gas.jpbc.Element;

public class ElementBytePair {
    private Element element;
    private byte[] byteArray;

    public ElementBytePair(Element element, byte[] byteArray) {
        this.element = element;
        this.byteArray = byteArray;
    }

    public Element getElement() {
        return element;
    }

    public byte[] getByteArray() {
        return byteArray;
    }
}
