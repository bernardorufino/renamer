package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import model.Renamer;

import static gui.MainWindow.*;

public class ResultListItemRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
        boolean isSelected, boolean hasFocus) {
        Renamer.Replacement result = (Renamer.Replacement) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(list, result.name, index,
            isSelected, hasFocus);
        Color color = 
            result.triedToRename
                ? result.renamed
                    ? FILE_RENAMED_COLOR 
                    : FILE_RENAMED_ERROR_COLOR 
                : result.matched
                    ? FILE_MATCHED_COLOR
                    : FILE_NOT_MATCHED_COLOR;
        label.setForeground(color);
        return label;
    }

}