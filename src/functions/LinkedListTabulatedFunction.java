package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    private class FunctionNode {
        // информационное поле для хранения данных типа FunctionPoint
        private FunctionPoint point;

        // поля для хранения ссылок на предыдущий и следующий элемент
        private FunctionNode prev;
        private FunctionNode next;

        // КОНСТРУКТОРЫ
        public FunctionNode(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }

        // геттеры и сеттеры
        public FunctionPoint getPoint() { return point; }
        public void setPoint(FunctionPoint point) { this.point = point; }
        public FunctionNode getPrev() { return prev; }
        public void setPrev(FunctionNode prev) { this.prev = prev; }
        public FunctionNode getNext() { return next; }
        public void setNext(FunctionNode next) { this.next = next; }
    }
    // голова списка
    private FunctionNode head;

    // количество точек
    private int pointsCount;
    private FunctionNode lastAccessedNode;  // последний доступный узел
    private int lastAccessedIndex;          // индекс последнего доступного узла


    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        // проверка параметров
        if (leftX >= rightX) {
            throw new IllegalArgumentException(
                    "Левая граница (" + leftX + ") должна быть < правой (" + rightX + ")"
            );
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException(
                    "Количество точек должно быть >= 2"
            );
        }
        // инициализация списка: создаем голову
        head = new FunctionNode(null);
        head.setNext(head);  // цикличность: ссылается сам на себя
        head.setPrev(head);
        this.pointsCount = 0;

        // создаем точки с равномерным распределением
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            // используем метод addNodeToTail()
            addNodeToTail().setPoint(new FunctionPoint(x, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        // аналогичная проверка
        if (values == null) {
            throw new IllegalArgumentException("Массив  не может быть null");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException(
                    "Количество точек должно быть >= 2"
            );
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException(
                    "Левая граница (" + leftX + ") должна быть < правой (" + rightX + ")"
            );
        }

        // используем первый конструктор для создания структуры списка
        this(leftX, rightX, values.length);

        // заполняем значения y(посмотрел как сделать в интернете)
        FunctionNode current = head.getNext();
        for (int i = 0; i < values.length; i++) {
            double x = current.getPoint().getX();
            current.setPoint(new FunctionPoint(x, values[i]));
            current = current.getNext();
        }
    }
    public double getLeftDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.getNext().getPoint().getX();  // первая точка
    }
    public double getRightDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.getPrev().getPoint().getX();  // последняя точка
    }

    public double getFunctionValue(double x) {
        final double EPSILON = 1e-10;

        if (pointsCount == 0) return Double.NaN;

        double leftX = getLeftDomainBorder();
        double rightX = getRightDomainBorder();

        // проверка границ (с учетом EPSILON)
        if (x < leftX - EPSILON || x > rightX + EPSILON) {
            return Double.NaN;
        }

        // поиск точного совпадения x
        FunctionNode current = head.getNext();
        while (current != head) {
            if (Math.abs(current.getPoint().getX() - x) < EPSILON) {
                return current.getPoint().getY();
            }
            current = current.getNext();
        }

        // pobcr интервала для линейной интерполяции
        FunctionNode node1 = head.getNext();
        FunctionNode node2 = node1.getNext();

        while (node2 != head) {
            if (x >= node1.getPoint().getX() - EPSILON &&
                    x <= node2.getPoint().getX() + EPSILON) {

                double x1 = node1.getPoint().getX();
                double y1 = node1.getPoint().getY();
                double x2 = node2.getPoint().getX();
                double y2 = node2.getPoint().getY();

                //  интерполяция
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            node1 = node2;
            node2 = node2.getNext();
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }
    public FunctionPoint getPoint(int index) {
        // getNodeByIndex() сам проверяет индекс и бросает исключение
        return new FunctionPoint(getNodeByIndex(index).getPoint());
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).getPoint().getX();
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).getPoint().getY();
    }
    public void setPointY(int index, double y) {
        FunctionNode node = getNodeByIndex(index);
        double x = node.getPoint().getX();
        node.setPoint(new FunctionPoint(x, y));
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        // проверка индекса (внутри getNodeByIndex)
        FunctionNode node = getNodeByIndex(index);

        // проверка на null
        if (point == null) {
            throw new IllegalArgumentException("Точка не может быть null");
        }

        // проверка порядка x с соседними точками
        if (index > 0) {
            FunctionNode prevNode = node.getPrev();
            if (point.getX() <= prevNode.getPoint().getX()) {
                throw new InappropriateFunctionPointException(
                        "x=" + point.getX() + " должен быть > " + prevNode.getPoint().getX()
                );
            }
        }
        if (index < pointsCount - 1) {
            FunctionNode nextNode = node.getNext();
            if (point.getX() >= nextNode.getPoint().getX()) {
                throw new InappropriateFunctionPointException(
                        "x=" + point.getX() + " должен быть < " + nextNode.getPoint().getX()
                );
            }
        }
    }
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double oldY = node.getPoint().getY();

        // проверка порядка X (как в setPoint)
        if (index > 0) {
            FunctionNode prevNode = node.getPrev();
            if (x <= prevNode.getPoint().getX()) {
                throw new InappropriateFunctionPointException(
                        "x=" + x + " должен быть > " + prevNode.getPoint().getX()
                );
            }
        }
        if (index < pointsCount - 1) {
            FunctionNode nextNode = node.getNext();
            if (x >= nextNode.getPoint().getX()) {
                throw new InappropriateFunctionPointException(
                        "x=" + x + " должен быть < " + nextNode.getPoint().getX()
                );
            }
        }

    }
    public void deletePoint(int index) {
        // проверка минимального количества точек
        if (pointsCount <= 2) {
            throw new IllegalStateException(
                    "Нельзя удалить точку! Должно остаться минимум 2 точки"
            );
        }

        // удаление узла
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // проверка на null
        if (point == null) {
            throw new IllegalArgumentException("Точка не может быть null");
        }

        double newX = point.getX();

        // поиск позиции для вставки и проверка уникальности X
        int insertIndex = 0;
        FunctionNode current = head.getNext();

        while (current != head) {
            double currentX = current.getPoint().getX();

            // проверка на существующий
            if (Math.abs(currentX - newX) < 1e-10) {
                throw new InappropriateFunctionPointException(
                        "Точка с x=" + newX + " уже существует"
                );
            }
            // поиск позиции (точки отсортированы по возрастанию X)
            if (newX > currentX) {
                insertIndex++;
                current = current.getNext();
            } else {
                break;
            }
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        // проверка индекса
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, pointsCount);
        }
        if (lastAccessedNode != null && lastAccessedIndex == index) {
            return lastAccessedNode;
        }
        FunctionNode node;
        if (index < pointsCount / 2) {
            // ищем с начала (если индекс в первой половине)
            node = head.getNext();  // первый значащий элемент
            for (int i = 0; i < index; i++) {
                node = node.getNext();
            }
        } else {
            node = head.getPrev();  // последний значащий элемент
            for (int i = pointsCount - 1; i > index; i--) {
                node = node.getPrev();
            }
        }
        lastAccessedNode = node;
        lastAccessedIndex = index;

        return node;
    }
    //
    private FunctionNode addNodeToTail() {
        // создаем новый узел
        FunctionNode newNode = new FunctionNode(null);
        newNode.setNext(head);
        newNode.setPrev(head.getPrev());

        head.getPrev().setNext(newNode);
        head.setPrev(newNode);

        pointsCount++;  // увеличиваем счетчик

        return newNode;
    }
    private FunctionNode addNodeByIndex(int index) {
        // проверка индекса
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, pointsCount);
        }

        // eсли добавляем в конец - используем существующий метод
        if (index == pointsCount) {
            return addNodeToTail();
        }
        FunctionNode currentNode = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null);
        // устанавливаем связи нового узла
        newNode.setNext(currentNode);
        newNode.setPrev(currentNode.getPrev());
        // обновляем связи соседних узлов
        currentNode.getPrev().setNext(newNode);
        currentNode.setPrev(newNode);
        pointsCount++;  // Увеличиваем счетчик

        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        // проверка индекса
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index, pointsCount);
        }

        // находим узел для удаления
        FunctionNode nodeToDelete = getNodeByIndex(index);

        nodeToDelete.getPrev().setNext(nodeToDelete.getNext());
        nodeToDelete.getNext().setPrev(nodeToDelete.getPrev());

        pointsCount--;  // уменьшаем счетчик

        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = null;
            lastAccessedIndex = -1;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }

        return nodeToDelete;
    }

}