import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new FlowLayout());

        String[] options = {
                "Години -> Секунди",    // час
                "Секунди -> Години",
                "Кілометри -> Милі",    // відстань
                "Милі -> Кілометри",
                "км/год -> м/с",        // швидкість
                "м/с -> км/год",
                "Кілограми -> Фунти",   // маса
                "Фунти -> Кілограми",
                "м^2 -> фут^2",         // площа
                "фут^2 -> м^2",
                "Цельсій -> Фаренгейт", // температура
                "Фаренгейт -> Цельсій",
                "Атмосфери -> Бари",    // тиск
                "Бари -> Атмосфери",
                "Літри -> Пінти",       // об'єм
                "Пінти -> Літри",
                "Джоулі -> Калорії",    // енергія
                "Калорії -> Джоулі"
        };
        JComboBox<String> comboBox = new JComboBox<>(options);
        JTextField inputField = new JTextField(10);
        JButton convertButton = new JButton("Конвертувати");
        JLabel resultLabel = new JLabel();
        frame.getRootPane().setDefaultButton(convertButton); // щоб жмать ентер для результату, прикольна фіча

        frame.add(new JLabel("Введіть значення:"));
        frame.add(inputField);
        frame.add(comboBox);
        frame.add(convertButton);
        frame.add(resultLabel);

        convertButton.addActionListener(e -> {
            try {
                double input = Double.parseDouble(inputField.getText().replace(",", "."));
                double result = getResult(comboBox, input);
                resultLabel.setText("Результат: " + String.format("%.4f", result));

            } catch (Exception ex) {
                resultLabel.setText("Помилка: введено некоректне значення.");
            }
        });

        frame.setVisible(true);
    }

    private static double getResult(JComboBox<String> comboBox, double input) {
        double result = 0;
        String selected = (String) comboBox.getSelectedItem();

        result = switch (selected) {
            case "Години -> Секунди" -> input * 3600;
            case "Секунди -> Години" -> input / 3600;
            case "Кілометри -> Милі" -> input / 1.60934;
            case "Милі -> Кілометри" -> input * 1.60934;
            case "км/год -> м/с" -> input / 3.6;
            case "м/с -> км/год" -> input * 3.6;
            case "Кілограми -> Фунти" -> input * 2.20462;
            case "Фунти -> Кілограми" -> input / 2.20462;
            case "м^2 -> фут^2" -> input * 10.7639;
            case "фут^2 -> м^2" -> input / 10.7639;
            case "Цельсій -> Фаренгейт" -> input * 1.8 + 32;
            case "Фаренгейт -> Цельсій" -> (input - 32) / 1.8;
            case "Атмосфери -> Бари" -> input * 1.01325;
            case "Бари -> Атмосфери" -> input / 1.01325;
            case "Літри -> Пінти" -> input * 2.11338;
            case "Пінти -> Літри" -> input / 2.11338;
            case "Джоулі -> Калорії" -> input / 4.184;
            case "Калорії -> Джоулі" -> input * 4.184;
            default -> result;
        };
        return result;
    }
}

/*
                case "Години -> Секунди" ->     // час
                case "Секунди -> Години" ->
                case "Кілометри -> Милі" ->     // відстань
                case "Милі -> Кілометри" ->
                case "км/год -> м/с" ->         // швидкість
                case "м/с -> км/год" ->
                case "Кілограми -> Фунти" ->    // маса
                case "Фунти -> Кілограми" ->
                case "м^2 -> фут^2" ->          // площа
                case "фут^2 -> м^2" ->
                case "Цельсій -> Фаренгейт" ->  // температура
                case "Фаренгейт -> Цельсій" ->
                case "Атмосфери -> Бари" ->     // тиск
                case "Бари -> Атмосфери" ->
                case "Літри -> Пінти" ->        // об'єм
                case "Пінти -> Літри" ->
                case "Джоулі -> Калорії" ->     // енергія
                case "Калорії -> Джоулі" ->
 */