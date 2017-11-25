package com.seven.compiler;

import javax.lang.model.element.VariableElement;

/**
 * Created by sunyuanming on 17-11-25.
 */

public class VariableInfo {


    int viewId;
    VariableElement variableElement;

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public void setVariableElement(VariableElement variableElement) {
        this.variableElement = variableElement;
    }
}
