package classroom.professor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import classroom.database.DatabaseManager;
import classroom.shared.AssignmentDetailView;

public class TeacherDashboard extends JFrame {

private JPanel contentPanel;

public TeacherDashboard() {
    setTitle("Teacher Dashboard");
    setSize(1280, 720);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setPreferredSize(new Dimension(1280, 60));
    topBar.setBackground(new Color(40, 40, 40));

    JLabel title = new JLabel("Teacher Dashboard");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("Tahoma", Font.BOLD, 20));
    title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

    topBar.add(title, BorderLayout.WEST);
    add(topBar, BorderLayout.NORTH);

    JPanel mainArea = new JPanel(new BorderLayout());
    mainArea.setBackground(new Color(24, 24, 27));

    JPanel topSection = new JPanel();
    topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
    topSection.setBackground(new Color(24, 24, 27));
    topSection.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

    JButton createBtn = new JButton("+ Create New Assignment");
    styleButton(createBtn, new Color(16, 185, 129), new Color(5, 150, 105));
    createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    createBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            openCreateDialog();
        }
    });

    topSection.add(createBtn);

    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(new Color(24, 24, 27));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));

    mainArea.add(topSection, BorderLayout.NORTH);
    mainArea.add(contentPanel, BorderLayout.CENTER);

    JScrollPane scrollPane = new JScrollPane(mainArea);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setBackground(new Color(24, 24, 27));

    add(scrollPane, BorderLayout.CENTER);

    loadExistingAssignments();
}

private void loadExistingAssignments() {
    contentPanel.removeAll();
    try (Connection conn = DatabaseManager.connect()) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT topic, details, DATE_FORMAT(due_date, '%Y/%m/%d') AS d_date FROM assignments");

        while (rs.next()) {
            String topic = rs.getString("topic");
            String details = rs.getString("details");
            String due = rs.getString("d_date");
            addAssignmentRow(topic, details, due);
        }
        stmt.close();
        rs.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    contentPanel.revalidate();
    contentPanel.repaint();
}

private void openCreateDialog() {
    final JDialog dialog = new JDialog(this, true);
    dialog.setUndecorated(true);
    dialog.setSize(500, 420);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(40, 40, 40));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel topicLabel = new JLabel("Topic:");
    topicLabel.setForeground(Color.WHITE);
    topicLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
    final JTextField topicField = new JTextField();
    topicField.setMaximumSize(new Dimension(460, 30));

    JLabel dueLabel = new JLabel("Due Date (YYYY/MM/DD):");
    dueLabel.setForeground(Color.WHITE);
    dueLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
    final JTextField dueField = new JTextField("2026/12/31");
    dueField.setMaximumSize(new Dimension(460, 30));

    JLabel detailLabel = new JLabel("Details:");
    detailLabel.setForeground(Color.WHITE);
    detailLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
    final JTextArea detailArea = new JTextArea(5, 20);
    detailArea.setLineWrap(true);
    detailArea.setWrapStyleWord(true);
    JScrollPane detailScroll = new JScrollPane(detailArea);

    panel.add(topicLabel);
    panel.add(topicField);
    panel.add(Box.createVerticalStrut(10));
    panel.add(dueLabel);
    panel.add(dueField);
    panel.add(Box.createVerticalStrut(10));
    panel.add(detailLabel);
    panel.add(detailScroll);

    JButton saveBtn = new JButton("Save");
    styleButton(saveBtn, new Color(16, 185, 129), new Color(5, 150, 105));
    saveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            DatabaseManager.addAssignment(topicField.getText(), detailArea.getText(), dueField.getText());
            loadExistingAssignments();
            dialog.dispose();
        }
    });

    panel.add(Box.createVerticalStrut(15));
    panel.add(saveBtn);

    JButton cancelBtn = new JButton("Cancel");
    styleButton(cancelBtn, new Color(70, 70, 70), new Color(90, 90, 90));
    cancelBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    });
    panel.add(Box.createVerticalStrut(5));
    panel.add(cancelBtn);

    dialog.add(panel);
    dialog.setVisible(true);
}

private void addAssignmentRow(final String title, final String details, String dueDate) {
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setBackground(new Color(24, 24, 27));

    JPanel row = new JPanel(new BorderLayout());
    row.setBackground(new Color(30, 30, 30));
    row.setMaximumSize(new Dimension(900, 70));
    row.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
        BorderFactory.createEmptyBorder(15, 20, 15, 20)
    ));

    JLabel titleLabel = new JLabel(title + " [Due: " + dueDate + "]");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    btnPanel.setBackground(new Color(30, 30, 30));

    JButton viewBtn = new JButton("View");
    styleButton(viewBtn, new Color(59, 130, 246), new Color(37, 99, 235));
    viewBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            new AssignmentDetailView(title, details, "", "TEACHER", false).setVisible(true);
        }
    });

    JButton subsBtn = new JButton("Submissions");
    styleButton(subsBtn, new Color(139, 92, 246), new Color(109, 40, 217));
    subsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            new SubmissionListView(title).setVisible(true);
        }
    });

    JButton deleteBtn = new JButton("Delete");
    styleButton(deleteBtn, new Color(239, 68, 68), new Color(220, 38, 38));
    deleteBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            confirmDelete(title);
        }
    });

    btnPanel.add(viewBtn);
    btnPanel.add(subsBtn);
    btnPanel.add(deleteBtn);

    row.add(titleLabel, BorderLayout.WEST);
    row.add(btnPanel, BorderLayout.EAST);

    container.add(row);
    container.add(Box.createVerticalStrut(15));

    contentPanel.add(container);
}

private void confirmDelete(final String title) {
    final JDialog dialog = new JDialog(this, true);
    dialog.setUndecorated(true);
    dialog.setSize(350, 150);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(40, 40, 40));
    panel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));

    JLabel textLabel = new JLabel("Delete " + title + "?", SwingConstants.CENTER);
    textLabel.setForeground(Color.WHITE);
    textLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
    panel.add(textLabel, BorderLayout.CENTER);

    JButton yesBtn = new JButton("Yes");
    styleButton(yesBtn, new Color(239, 68, 68), new Color(220, 38, 38));
    yesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            deleteAssignmentFromDb(title);
            loadExistingAssignments();
            dialog.dispose();
        }
    });

    JButton noBtn = new JButton("No");
    styleButton(noBtn, new Color(70, 70, 70), new Color(90, 90, 90));
    noBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    });

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    btnPanel.setBackground(new Color(40, 40, 40));
    btnPanel.add(yesBtn);
    btnPanel.add(noBtn);

    panel.add(btnPanel, BorderLayout.SOUTH);
    dialog.add(panel);
    dialog.setVisible(true);
}

private void deleteAssignmentFromDb(String title) {
    try (Connection conn = DatabaseManager.connect()) {
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM assignments WHERE topic = ?");
        pstmt.setString(1, title);
        pstmt.executeUpdate();
        pstmt.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void styleButton(final JButton button, final Color normal, final Color hover) {
    button.setBackground(normal);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setFont(new Font("Tahoma", Font.BOLD, 14));

    button.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
        public void mouseExited(MouseEvent e) { button.setBackground(normal); }
    });
}

public static void main(String[] args) {
    DatabaseManager.initialize();
    new TeacherDashboard().setVisible(true);
}
}