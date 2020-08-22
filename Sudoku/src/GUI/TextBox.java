package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TextBox extends JDialog {

    private final JPanel textPanel;
    private final JTextArea textArea;
    private final JScrollPane scrollPane;

    public TextBox(final JFrame frame, final String path) {

        super(frame);
        setResizable(false);
        setVisible(true);
        this.textPanel = new JPanel();
        this.textArea = new JTextArea();
        try {
            final Scanner reader = new Scanner(new File(path));
            while (reader.hasNextLine()) {
                textArea.append(reader.nextLine() + "\n");
            }
            reader.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        this.scrollPane = new JScrollPane(textArea);
        this.scrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
        this.textArea.setEditable(false);
        this.textPanel.add(scrollPane);
        add(textPanel, BorderLayout.CENTER);
        pack();

    }

}
