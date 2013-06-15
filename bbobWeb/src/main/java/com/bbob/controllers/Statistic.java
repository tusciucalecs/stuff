package com.bbob.controllers;

public class Statistic {
    private String name;

    private Object[][] matrix = new Object[4][5];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Object[][] matrix) {
        this.matrix = matrix;
    }

    /*
     * Used to print the matrix in a javascript variable
     */
    public String printMatrix() {
        String buffer = "[";
        boolean first = true;
        for (Object[] array : matrix) {
            if (!first) {
                buffer += ", ";
            } else {
                first = false;
            }
            buffer += printArray(array);
        }
        buffer += "]";
        return buffer;
    }

    private String printArray(Object[] array) {
        String buffer = "[";
        boolean first = true;
        for (Object object : array) {
            if (!first) {
                buffer += ", ";
            } else {
                first = false;
            }
            if (object.getClass().equals(String.class)) {
                buffer += "\"" + object + "\"";
            } else {
                buffer += object;
            }
        }
        buffer += "]";
        return buffer;
    }
}
