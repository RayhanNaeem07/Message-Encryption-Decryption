import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CipherMachineApp extends JFrame {
    private JTextField entry;
    private JTextField keyEntry;
    private JTextArea resultArea;
    private JRadioButton encryptRadioButton;
    private JRadioButton decryptRadioButton;

    public CipherMachineApp() {
        super("Cipher Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        panel.add(new JLabel("Enter your message:"));
        entry = new JTextField(30);
        panel.add(entry);

        panel.add(new JLabel("Choose an algorithm:"));
        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[]{
                "Caesar Cipher", "Substitution Cipher", "Reverse Cipher",
                "Atbash Cipher", "Rail Fence Cipher", "Vigenere Cipher",
                "Playfair Cipher", "Transposition Cipher", "ROT13 Cipher",
                "Simple Substitution Cipher"
        });
        panel.add(algorithmComboBox);

        panel.add(new JLabel("Enter the key:"));
        keyEntry = new JTextField(10);
        panel.add(keyEntry);

        panel.add(new JLabel("Choose Crypto Mode:"));
        encryptRadioButton = new JRadioButton("Encrypt");
        decryptRadioButton = new JRadioButton("Decrypt");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(encryptRadioButton);
        modeGroup.add(decryptRadioButton);
        encryptRadioButton.setSelected(true);
        panel.add(encryptRadioButton);
        panel.add(decryptRadioButton);

        resultArea = new JTextArea("Result: ");
        resultArea.setEditable(false);
        panel.add(resultArea);

        JButton encryptDecryptButton = new JButton("Encrypt/Decrypt");
        encryptDecryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptDecrypt();
            }
        });
        panel.add(encryptDecryptButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearText();
            }
        });
        panel.add(clearButton);

        algorithmComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateKeyVisibility();
            }
        });

        updateKeyVisibility();
    }

    private void encryptDecrypt() {
        String message = entry.getText();
        String key = keyEntry.getText();
        String mode = encryptRadioButton.isSelected() ? "E" : "D";

        Map<Integer, CipherFunction> algorithms = new HashMap<>();
        algorithms.put(0, this::caesarCipher);
        algorithms.put(1, this::substitutionCipher);
        algorithms.put(2, this::reverseCipher);
        algorithms.put(3, this::atbashCipher);
        algorithms.put(4, this::railFenceCipher);
        algorithms.put(5, this::vigenereCipher);
        algorithms.put(6, this::playfairCipher);
        algorithms.put(7, this::transpositionCipher);
        algorithms.put(8, this::rot13Cipher);
        algorithms.put(9, this::simpleSubstitutionCipher);

        int choice = ((JComboBox<?>) ((JPanel) getContentPane().getComponent(0)).getComponent(3)).getSelectedIndex();

        if (algorithms.containsKey(choice)) {
            String result = algorithms.get(choice).apply(message, key, mode);
            resultArea.setText("Result: " + result);
        } else {
            resultArea.setText("Invalid choice");
        }
    }

    private void clearText() {
        entry.setText("");
        resultArea.setText("Result: ");
    }

    private void updateKeyVisibility() {
        int selectedIndex = ((JComboBox<?>) ((JPanel) getContentPane().getComponent(0)).getComponent(3)).getSelectedIndex();
        keyEntry.setVisible(selectedIndex != 7); // Transposition Cipher does not require a key
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CipherMachineApp();
            }
        });
    }

    // Define cipher functions here

    private interface CipherFunction {
        String apply(String message, String key, String mode);
    }

    private String caesarCipher(String message, String key, String mode) {
        int shift = Integer.parseInt(key);
        StringBuilder result = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                int base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char) ((c - base + shift) % 26 + base));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String substitutionCipher(String message, String key, String mode) {
        Map<Character, Character> substitutionMap = createSubstitutionMap(key);

        StringBuilder result = new StringBuilder();

        for (char c : message.toCharArray()) {
            result.append(substitutionMap.getOrDefault(c, c));
        }

        return result.toString();
    }

    private Map<Character, Character> createSubstitutionMap(String key) {
        Map<Character, Character> substitutionMap = new HashMap<>();

        for (int i = 0; i < 26; i++) {
            char originalChar = (char) ('A' + i);
            char substituteChar = key.charAt(i % key.length());
            substitutionMap.put(originalChar, substituteChar);
        }

        return substitutionMap;
    }

    private String reverseCipher(String message, String key, String mode) {
        return new StringBuilder(message).reverse().toString();
    }

    private String atbashCipher(String message, String key, String mode) {
        StringBuilder result = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                int base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char) (base + 25 - (c - base)));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String railFenceCipher(String message, String key, String mode) {
        int numRows = Integer.parseInt(key);
        StringBuilder result = new StringBuilder();

        if (mode.equals("E")) {
            for (int i = 0; i < numRows; i++) {
                for (int j = i; j < message.length(); j += numRows) {
                    result.append(message.charAt(j));
                }
            }
        } else if (mode.equals("D")) {
            int index = 0;
            char[] resultArray = new char[message.length()];

            for (int i = 0; i < numRows; i++) {
                for (int j = i; j < message.length(); j += numRows) {
                    resultArray[j] = message.charAt(index++);
                }
            }

            result.append(resultArray);
        }

        return result.toString();
    }

    private String vigenereCipher(String message, String key, String mode) {
        StringBuilder result = new StringBuilder();

        for (int i = 0, j = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            if (Character.isLetter(c)) {
                int base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = key.charAt(j % key.length()) - 'A';
                result.append((char) ((c - base + shift) % 26 + base));
                j++;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String playfairCipher(String message, String key, String mode) {
        // Implement Playfair Cipher logic
        return "Playfair Cipher Result";
    }

    private String transpositionCipher(String message, String key, String mode) {
        // Implement Transposition Cipher logic
        return "Transposition Cipher Result";
    }

    private String rot13Cipher(String message, String key, String mode) {
        int shift = 13;
        StringBuilder result = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                int base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char) ((c - base + shift) % 26 + base));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String simpleSubstitutionCipher(String message, String key, String mode) {
        Map<Character, Character> substitutionMap = createSubstitutionMap(key);

        StringBuilder result = new StringBuilder();

        for (char c : message.toCharArray()) {
            result.append(substitutionMap.getOrDefault(c, c));
        }

        return result.toString();
    }
}