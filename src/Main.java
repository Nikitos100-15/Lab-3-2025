import functions.*;
// переделанный меин 7 Задание
public class Main {
    public static void main(String[] args) {
        // оборачиваем в try-catch для обработки исключений
        try {
            //создаем функцию f(x) = 2x + 1
            double[] xValues = {-2, -1, 0, 1, 2, 3, 4};
            double[] yValues = {-3, -1, 1, 3, 5, 7, 9};
            double leftX = xValues[0];
            double rightX = xValues[xValues.length - 1];

            // используем интерфейс TabulatedFunction и ArrayTabulatedFunction
            TabulatedFunction linearFunc = new ArrayTabulatedFunction(leftX, rightX, yValues);

            // небольшая вводная часть
            System.out.println("f(x) = 2x + 1 на [" + leftX + ", " + rightX + "]");
            System.out.println("Точек: " + linearFunc.getPointsCount());

            // вывод всех точек
            System.out.println("Точки после создания функции:");
            printAllPoints(linearFunc);

            // тестируем значения
            System.out.println("Тест самих значений");
            double[] testPoints = {-4, -2, 0, 2, 4, 6};
            for (double x : testPoints) {
                double y = linearFunc.getFunctionValue(x);
                System.out.println("f(" + x + ") = " + y);
            }

            // меняем точки
            linearFunc.setPointY(3, 10);
            // точки после изменения y
            System.out.println("Точки после setPointY(3, 10):");
            printAllPoints(linearFunc);

            // оборачиваем setPointX в try-catch (может выбросить InappropriateFunctionPointException)
            try {
                linearFunc.setPointX(1, -0.7);
                System.out.println("setPointX выполнен успешно");
            } catch (InappropriateFunctionPointException e) {
                System.out.println("Ошибка setPointX: " + e.getMessage());
            }

            // вывод точки после изменения x
            System.out.println("Точки после setPointX(1, -0.7):");
            printAllPoints(linearFunc);

            // добавляем и удаляем
            //  оборачиваем addPoint также в try-catch
            try {
                linearFunc.addPoint(new FunctionPoint(2.5, 6));
                System.out.println("addPoint выполнен успешно");
            } catch (InappropriateFunctionPointException e) {
                System.out.println("Ошибка addPoint: " + e.getMessage());
            }

            // точки после добавления
            System.out.println("Точки после addPoint(2.5, 6):");
            printAllPoints(linearFunc);

            // оборачиваем  также deletePoint в try-catch
            try {
                linearFunc.deletePoint(2);
                System.out.println("deletePoint выполнен успешно");
            } catch (Exception e) {
                System.out.println("Ошибка deletePoint: " + e.getMessage());
            }

            // точки после удаления
            System.out.println("Точки после deletePoint(2):");
            printAllPoints(linearFunc);

            // финальная проверка
            System.out.println("Финальный результат:");
            System.out.println("Всего точек:" + linearFunc.getPointsCount());
            System.out.printf("f(2.5)=" + linearFunc.getFunctionValue(2.5));

        } catch (Exception e) {  // ДОБАВЛЕНО: общий catch для всех исключений
            System.out.println("Программа завершилась с ошибкой: " + e.getMessage());
            e.printStackTrace();
        }

        // тестирование LinkedListTabulatedFunction - заменяем один класс на другой
        System.out.println("\n=== тест класса LinkedListTabulatedFunction ===");
        try {
            double[] xValues = {-2, -1, 0, 1, 2, 3, 4};
            double[] yValues = {-3, -1, 1, 3, 5, 7, 9};
            double leftX = xValues[0];
            double rightX = xValues[xValues.length - 1];

            //  используем LinkedListTabulatedFunction
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(leftX, rightX, yValues);

            System.out.println("LinkedListTabulatedFunction создан успешно");
            System.out.println("Точек: " + listFunc.getPointsCount());

            // проверка исключения FunctionPointIndexOutOfBoundsException
            try {
                listFunc.getPointX(100); // неверный индекс
            } catch (FunctionPointIndexOutOfBoundsException e) {
                System.out.println("Поймано FunctionPointIndexOutOfBoundsException: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        //  проверка исключений в конструкторах
        System.out.println("\n=== проверка исключений ===");

        try {
            // IllegalArgumentException: левая граница >= правой
            new ArrayTabulatedFunction(10, 5, 3);
        } catch (IllegalArgumentException e) {
            System.out.println("поймано IllegalArgumentException (границы): " + e.getMessage());
        }

        try {
            // IllegalArgumentException: точек < 2
            new LinkedListTabulatedFunction(0, 10, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано IllegalArgumentException (точек < 2): " + e.getMessage());
        }
    }

    // метод для вывода всех точек через геттеры
    public static void printAllPoints(TabulatedFunction linearFunc) {
        for (int i = 0; i < linearFunc.getPointsCount(); i++) {
            double x = linearFunc.getPointX(i);  // геттер для x
            double y = linearFunc.getPointY(i);  // геттер для y
            System.out.println("Точка "+ i +":" + "(" +x + "," + y +")");
        }
    }
}