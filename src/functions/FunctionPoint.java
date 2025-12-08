package functions;

public  class FunctionPoint {
    // поле нашего класса
    private double x;
    private double y;
    // конструктор, который создаёт объект с двумя точками координат
    public  FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    // конструктор, который создаёт объект с двумя нулевыми точками
    public FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }
    // конструктор, который создает объект с двумя точно такими же точками(т.е. копии какой-либо другой точкой)
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;

    }
    // геттер получение переменной X
    public double getX(){
        return x;
    }
    // геттер полечения переменной Y
    public  double getY(){
        return y;
    }
    // сеттеры для переменных x и y
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

}