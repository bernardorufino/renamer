package gui;

import java.awt.Component;


import static gui.MainWindow.FILE_MATCHED_COLOR;
import static gui.MainWindow.FILE_NOT_MATCHED_COLOR;
import static model.Renamer.Replacement;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;


public class MatchesListItemRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
        boolean isSelected, boolean hasFocus) {
        Replacement replacement = (Replacement) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(list, replacement.originalName, index,
            isSelected, hasFocus);
        label.setForeground(
                replacement.matched
                        ? FILE_MATCHED_COLOR
                        : FILE_NOT_MATCHED_COLOR
        );
        return label;
    }

}