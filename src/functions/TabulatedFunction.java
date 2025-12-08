package functions;

public interface TabulatedFunction {
    // методы области определения
    double getLeftDomainBorder();
    double getRightDomainBorder();
    double getFunctionValue(double x);
    // методы работы с точками
    int getPointsCount();
    FunctionPoint getPoint(int index);
    double getPointX(int index);
    double getPointY(int index);
    void setPointY(int index, double y);
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;
    void setPointX(int index, double x) throws InappropriateFunctionPointException;
    void deletePoint(int index);
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
}