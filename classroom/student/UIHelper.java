package classroom.student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UIHelper {
public static void styleButton(JButton b, Color n, Color h) {
b.setBackground(n);
b.setForeground(Color.WHITE);
b.setFocusPainted(false);
b.addMouseListener(new MouseAdapter() {
public void mouseEntered(MouseEvent e) { b.setBackground(h); }
public void mouseExited(MouseEvent e) { b.setBackground(n); }
});
}

public static void showCustomDialog(JFrame p, String m) {
    JOptionPane.showMessageDialog(p, m);
}
}