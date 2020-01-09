package com.linkedlogics.flow;

public class LogicExpression {
    private String expression ;
    private String defaultValue;

    public LogicExpression(String expression) {
        expression = expression.trim() ;
        if (expression.startsWith("${") && expression.endsWith("}")) {
            this.expression = expression.substring(0, expression.length() - 1).substring(2) ;
        } else {
            this.expression = expression;
        }

        if (this.expression.lastIndexOf(":") > -1) {
            this.defaultValue = this.expression.substring(this.expression.lastIndexOf(":") + 1) ;
            this.expression = this.expression.substring(0, this.expression.lastIndexOf(":")) ;
        }
    }

    public String getExpression() {
        return expression;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String toString() {
        return expression ;
    }

    public static boolean isExpression(Object expression) {
        return expression != null && expression.toString().startsWith("${") && expression.toString().endsWith("}") ;
    }
}
