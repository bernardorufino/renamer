package gui;

import model.Renamer;
import model.Renamer.PathNotDirectoryException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import static model.Renamer.Replacement;

public class MainWindow implements Notifier {

    private static final String DEFAULT_SEARCH = "^(.*)\\.(.*)$";
    private static final String DEFAULT_REPLACEMENT = "[$1].$2";
    private static final String DEFAULT_DIR = "C:\\";
    private static final Font TEXT_FIELD_FONT = new Font("Courier New", Font.PLAIN, 12);
    /* package */ static final Color FILE_MATCHED_COLOR       = new Color(35, 140, 0);
    /* package */ static final Color FILE_NOT_MATCHED_COLOR   = new Color(147, 50, 53);
    /* package */ static final Color FILE_RENAMED_COLOR       = new Color(35, 140, 0);
    /* package */ static final Color FILE_RENAMED_ERROR_COLOR = new Color(147, 50, 53);

    private JFrame frame;
    private JTextField patternInput;
    private JTextField replacementInput;
    private JTextField pathField;
    private JList<Replacement> matchesList;
    private DefaultListModel<Replacement> matchesListModel;
    private JList<Replacement> replacementList;
    private DefaultListModel<Replacement> replacementListModel;
    private Renamer renamer;
    private Notifier notifier;
    private JButton fetchButton;
    private JButton renameButton;
    
    private JFileChooser fileChooser;

    public MainWindow() {
        renamer = new Renamer("C:\\");
        
        matchesListModel = new DefaultListModel<>();
        matchesList = new JList<>(matchesListModel);
//        matchesList.setEnabled(false);
        matchesList.setCellRenderer(new MatchesListItemRenderer());
        
        replacementListModel = new DefaultListModel<>();
        replacementList = new JList<>(replacementListModel);
//        replacementList.setEnabled(false);
        replacementList.setCellRenderer(new ResultListItemRenderer());
        
        notifier = this;
        build();
        fetchLists();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void fetchLists() {
        matchesListModel.clear();
        replacementListModel.clear();
        renamer.set(patternInput.getText(), replacementInput.getText());
        fetchMatchesList();
        fetchReplacementsList();
    }

    private void fetchMatchesList() {
        for (Replacement replacement : renamer.getReplacements().values()) {
            matchesListModel.addElement(replacement);
        }
    }

    private void fetchReplacementsList() {
        for (Replacement replacement : renamer.getReplacements().values()) {
            replacementListModel.addElement(replacement);
        }
    }

    private class FileChooserButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setCurrentDirectory(new File(pathField.getText()));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                fetchButton.doClick();
            }
        }
    }

    private class FetchButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                renamer.setPath(pathField.getText());
                renameButton.setEnabled(true);
            } catch (PathNotDirectoryException err) {
                notifier.alert(Messages.MESSAGE_PATH_ERROR);
                renameButton.setEnabled(false);
            } finally {
                fetchLists();
            }
        }
    }

    private class RenameButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            renamer.apply();
            renamer.reload();
            fetchLists();
        }
    }

    private class ReplacementInputHandler extends KeyAdapter {
        public void keyReleased(KeyEvent e) { fetchLists(); }
    }

    private class PatternInputHandler extends KeyAdapter {
        public void keyReleased(KeyEvent e) { fetchLists(); }
    }

    public void notify(String message) {
        System.out.println(message);
    }

    public void alert(String message) {
        System.out.println(message);
    }

    private void build() {
        frame = new JFrame();
        frame.setTitle(Messages.WINDOW_TITLE);
        frame.setMinimumSize(new Dimension(640, 480));
        frame.setBounds(100, 100, 680, 381);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JPanel operationsPanel = new JPanel();
        operationsPanel.setMaximumSize(new Dimension(32767, operationsPanel.getSize().height));
        operationsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        FlowLayout flowLayout = (FlowLayout) operationsPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setHgap(10);
        frame.getContentPane().add(operationsPanel);

        JLabel pathLabel = new JLabel(Messages.LABEL_DIR);
        operationsPanel.add(pathLabel);

        pathField = new JTextField();
        pathField.setText(DEFAULT_DIR);
        pathField.setBorder(new EmptyBorder(5, 5, 5, 5));
        operationsPanel.add(pathField);
        pathField.setColumns(30);

        JButton fileChooserButton = new JButton("..");
        fileChooser = new JFileChooser(DEFAULT_DIR);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooserButton.addActionListener(new FileChooserButtonHandler());
        operationsPanel.add(fileChooserButton);

        fetchButton = new JButton(Messages.BUTTON_FETCH);
        fetchButton.addActionListener(new FetchButtonHandler());
        operationsPanel.add(fetchButton);

        renameButton = new JButton(Messages.BUTTON_RENAME);
        renameButton.addActionListener(new RenameButtonHandler());
        operationsPanel.add(renameButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(statusPanel);
        GridLayout statusPanelLayout = new GridLayout(1, 2);
        statusPanelLayout.setHgap(10);
        statusPanel.setLayout(statusPanelLayout);

        JPanel currentPanel = new JPanel();
        statusPanel.add(currentPanel);
        currentPanel.setLayout(new BorderLayout(0, 10));

        patternInput = new JTextField();
        patternInput.setFont(TEXT_FIELD_FONT);
        patternInput.addKeyListener(new PatternInputHandler());
        patternInput.setBorder(new EmptyBorder(5, 5, 5, 5));
        patternInput.setText(DEFAULT_SEARCH);
        currentPanel.add(patternInput, BorderLayout.NORTH);
        patternInput.setColumns(10);

        currentPanel.add(matchesList, BorderLayout.CENTER);

        JPanel previewPanel = new JPanel();
        statusPanel.add(previewPanel);
        previewPanel.setLayout(new BorderLayout(0, 10));

        replacementInput = new JTextField();
        replacementInput.setFont(TEXT_FIELD_FONT);
        replacementInput.setText(DEFAULT_REPLACEMENT);
        replacementInput.addKeyListener(new ReplacementInputHandler());
        replacementInput.setBorder(new EmptyBorder(5, 5, 5, 0));
        previewPanel.add(replacementInput, BorderLayout.NORTH);
        replacementInput.setColumns(10);

        previewPanel.add(replacementList, BorderLayout.CENTER);
    }

}
