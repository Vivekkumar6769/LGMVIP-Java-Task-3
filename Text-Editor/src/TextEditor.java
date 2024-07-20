import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

public class TextEditor extends JFrame implements ActionListener {

    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu;
    private JMenuItem newFileItem, openFileItem, saveFileItem, closeItem, cutItem, copyItem, pasteItem, printItem;
    private JLabel statusBar;

    public TextEditor() {
        setTitle("Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                updateStatusBar();
            }
        });

        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        newFileItem = new JMenuItem("New", KeyEvent.VK_N);
        openFileItem = new JMenuItem("Open", KeyEvent.VK_O);
        saveFileItem = new JMenuItem("Save", KeyEvent.VK_S);
        closeItem = new JMenuItem("Close", KeyEvent.VK_C);
        printItem = new JMenuItem("Print", KeyEvent.VK_P);

        newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        printItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));

        fileMenu.add(newFileItem);
        fileMenu.add(openFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(closeItem);
        fileMenu.addSeparator();
        fileMenu.add(printItem);

        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        cutItem = new JMenuItem("Cut", KeyEvent.VK_T);
        copyItem = new JMenuItem("Copy", KeyEvent.VK_C);
        pasteItem = new JMenuItem("Paste", KeyEvent.VK_P);

        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        add(scrollPane, BorderLayout.CENTER);

        statusBar = new JLabel("Line: 1, Column: 1");
        add(statusBar, BorderLayout.SOUTH);

        newFileItem.addActionListener(this);
        openFileItem.addActionListener(this);
        saveFileItem.addActionListener(this);
        closeItem.addActionListener(this);
        cutItem.addActionListener(this);
        copyItem.addActionListener(this);
        pasteItem.addActionListener(this);
        printItem.addActionListener(this);

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newFileItem) {
            if (confirmSave()) {
                textArea.setText("");
                setTitle("Text Editor");
            }
        } else if (e.getSource() == openFileItem) {
            if (confirmSave()) {
                openFile();
            }
        } else if (e.getSource() == saveFileItem) {
            saveFile();
        } else if (e.getSource() == closeItem) {
            if (confirmSave()) {
                System.exit(0);
            }
        } else if (e.getSource() == cutItem) {
            textArea.cut();
        } else if (e.getSource() == copyItem) {
            textArea.copy();
        } else if (e.getSource() == pasteItem) {
            textArea.paste();
        } else if (e.getSource() == printItem) {
            printFile();
        }
    }

    private boolean confirmSave() {
        int option = JOptionPane.showConfirmDialog(this, "Do you want to save changes?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            return saveFile();
        } else if (option == JOptionPane.NO_OPTION) {
            return true;
        }
        return false;
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                textArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n");
                }
                setTitle("Text Editor - " + selectedFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(textArea.getText());
                setTitle("Text Editor - " + selectedFile.getName());
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    private void printFile() {
        try {
            textArea.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error printing file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusBar() {
        int caretPos = textArea.getCaretPosition();
        int lineNum = 1;
        int columnNum = 1;

        try {
            lineNum = textArea.getLineOfOffset(caretPos) + 1;
            columnNum = caretPos - textArea.getLineStartOffset(lineNum - 1) + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        statusBar.setText("Line: " + lineNum + ", Column: " + columnNum);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TextEditor editor = new TextEditor();
            editor.setVisible(true);
        });
    }
}
