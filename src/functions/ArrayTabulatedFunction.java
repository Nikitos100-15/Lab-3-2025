package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {
    // создаем поле в виде массива
    private FunctionPoint[] points;
    // поле количестов точек
    private int pointsCount;

    // первый конструктор по заданию
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        // добавляем исключения по 3 заданию
        if (leftX>= rightX)
            throw new IllegalArgumentException(
                    "Левая граница является правой в данном случае."
            );
        if (pointsCount<2)
            throw new IllegalArgumentException(
                    "Количество точек должно быть >= 2."
            );
        this.points = new FunctionPoint[pointsCount]; // создаем объект для массива точек
        this.pointsCount = pointsCount; // инициализируем количество точек
        double step = (rightX - leftX) / (pointsCount - 1); // считаем шаг между точками
        for (int i = 0; i < pointsCount; i++) {
            double x_c = leftX + i * step;// считаем координату x
            points[i] = new FunctionPoint(x_c, 0);// записываем наши точки в объект вида массива
        }
    }
    // второй конструктор по заданию
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        // аналогичные проверки исключений
        if (leftX >= rightX) {
            throw new IllegalArgumentException(
                    "Левая граница является правой в данном случае."
            );
        }
        if (values == null) {
            throw new IllegalArgumentException("Массив  не может быть равен null");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException(
                    "Количество точек должно быть >= 2 "
            );
        }
        this.points = new FunctionPoint[values.length]; // создаем объект для массива точек
        this.pointsCount = values.length; // инициализируем количество точек
        double step = (rightX - leftX) / (values.length - 1); // считаем шаг между точками
        for (int i = 0; i < values.length; i++) {
            double x_c = leftX + i * step; // считаем координату x
            points[i] = new FunctionPoint(x_c, values[i]); // записываем наши точки в объект вида массива
        }
    }

    // метод для возращения самой левой границы области определения
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    // метод для возращения самой правой границы области определения
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    // метод для получения значение функции в точке x, если эта точка лежит в области определения функции
    public double getFunctionValue(double x) {
        // значение эпсилон как константа
        final double EPSILON = 1e-10;

        // проверка на пустоту и на существование(без нее у меня ломалось)
        if (points == null || pointsCount == 0) {
            return Double.NaN;
        }

        // получаем промежутки для проверки границ
        double leftX = points[0].getX();
        double rightX = points[pointsCount - 1].getX();

        // проверка границ с переменной эпсилон
        if ((x < (leftX - EPSILON)) || (x > (rightX + EPSILON))) {
            return Double.NaN;
        }

        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(x - points[i].getX()) < EPSILON) {
                return points[i].getY(); // возвращаем соответствующий y (также у меня не было вроде)
            }
        }

        // ищем интервал с учетом эпсилон для интерполяции
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            // ищем значения x с учетом эпсилон
            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        return Double.NaN; // возвращаем на всякий случай
    }



    // метод для получения количества точек
    public int getPointsCount() {
        return pointsCount;
    }

    // метод для вброса исключения FunctionPointIndexOutOfBoundsException(идея была взяла с сайта metanit.com)
    private void checkIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Значение" + index + " находится вне  заданного диапазона [0, " + (pointsCount - 1) + "]"
            );
        }
    }
    // метод для получения копии точки
    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]);
    }
    // измененный методы с исключениями
    // метод для получения значения абциссы
    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    // метод для получения значения ординаты
    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }

    // метод для изменения значения ординаты точки с указанным номером
    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }

    // метод для замены точки на табулированную
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        // проверка на null
        if (point == null) {
            //исключение
            throw new IllegalArgumentException("Точка не может быть null");
        }
        // проверяем, что новый x между соседними точками
        if (index > 0 && point.getX() <= points[index - 1].getX()) {
            throw new InappropriateFunctionPointException(
                    "значение x " + point.getX() + " должен быть > " + points[index - 1].getX()
            );
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX()) {
            //исключение
            throw new InappropriateFunctionPointException(
                    "значение x " + point.getX() + " должен быть < " + points[index + 1].getX()
            );
        }

        points[index] = new FunctionPoint(point);  // сохраняем копию
    }

    // метод для замены абциссы точки с указанным номером ( c добавленными исключениями)
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);

        // проверка, что x между соседними точками
        if (index > 0 && x <= points[index - 1].getX()) {
            //исключение
            throw new InappropriateFunctionPointException(
                    "значение x " + x + " должен быть > " + points[index - 1].getX()
            );
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX()) {
            throw new InappropriateFunctionPointException(
                    "значение x " + x + " должен быть < " + points[index + 1].getX()
            );
        }

        points[index].setX(x);
    }

    public void deletePoint(int index) {
        checkIndex(index);

        // проверка, что останется минимум 2 точки
        if (pointsCount <= 2) {
            throw new IllegalStateException( "Ошибка! Должно остаться минимум 2 точки" );
        }

        for (int i = index; i < pointsCount - 1; i++) {
            points[i] = points[i + 1];
        }
        pointsCount--;
    }
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // проверка на null
        if (point == null) {
            throw new IllegalArgumentException("Точка не может быть null");
        }

        // проверяем, что такой x еще не существует
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException(
                        "Точка с X=" + point.getX() + " уже существует"
                );
            }
        }
        // проверяем, заполнен ли массив
        if (pointsCount >= points.length) {
            // увеличиваем массив в 2 раза
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }
        // ищем позицию для вставки x
        int pos = 0;
        while (pos < pointsCount && points[pos].getX() < point.getX()) {
            pos++;
        }
        // сдвигаем точки вправо, чтобы освободить место для новой точки
        System.arraycopy(points, pos, points, pos + 1, pointsCount - pos);
        // вставляем новую точку
        points[pos] = new FunctionPoint(point);
        pointsCount++;
    }
}