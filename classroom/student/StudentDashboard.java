package classroom.student;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import classroom.database.DatabaseManager;
import classroom.shared.AssignmentDetailView;

public class StudentDashboard extends JFrame {

private JPanel contentPanel;
private String currentStudentId;
private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

public StudentDashboard(String studentId) {
    currentStudentId = studentId;
    setTitle("Student Dashboard");
    setSize(1280, 720);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setPreferredSize(new Dimension(1280, 60));
    topBar.setBackground(new Color(40, 40, 40));

    JLabel title = new JLabel("Student Dashboard");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("Leelawadee UI", Font.BOLD, 20));
    title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

    JLabel userLabel = new JLabel("ID: " + studentId);
    userLabel.setForeground(Color.LIGHT_GRAY);
    userLabel.setFont(new Font("Leelawadee UI", Font.PLAIN, 16));
    userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

    topBar.add(title, BorderLayout.WEST);
    topBar.add(userLabel, BorderLayout.EAST);
    add(topBar, BorderLayout.NORTH);

    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(new Color(24, 24, 27));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setBackground(new Color(24, 24, 27));

    add(scrollPane, BorderLayout.CENTER);

    refreshAssignments();
}

private void refreshAssignments() {
    contentPanel.removeAll();

    List<String> submitted = new ArrayList<>();
    try {
        Connection conn = DatabaseManager.connect();
        PreparedStatement pstmt = conn.prepareStatement("SELECT topic FROM submissions WHERE student_id = ?");
        pstmt.setString(1, currentStudentId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            submitted.add(rs.getString("topic"));
        }
        conn.close();
    } catch (Exception e) { e.printStackTrace(); }

    List<String> pendingRaw = new ArrayList<>();
    List<String> pendingDisp = new ArrayList<>();
    List<String> pendingDet = new ArrayList<>();

    List<String> lateRaw = new ArrayList<>();
    List<String> lateDisp = new ArrayList<>();
    List<String> lateDet = new ArrayList<>();

    List<String> doneRaw = new ArrayList<>();
    List<String> doneDisp = new ArrayList<>();
    List<String> doneDet = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();

    try {
        Connection conn = DatabaseManager.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT topic, details, DATE_FORMAT(due_date, '%Y/%m/%d %H:%i') AS d_date FROM assignments ORDER BY id DESC");
        
        while (rs.next()) {
            String topic = rs.getString("topic");
            String details = rs.getString("details");
            String dueDateStr = rs.getString("d_date");
            
            LocalDateTime dueDateTime = LocalDateTime.parse(dueDateStr, dtf);
            String timeLeft = calculateTimeLeft(now, dueDateTime);
            String displayTopic = topic + " [" + timeLeft + "]";

            if (submitted.contains(topic)) {
                doneRaw.add(topic);
                doneDisp.add(displayTopic);
                doneDet.add(details);
            } else if (now.isAfter(dueDateTime)) {
                lateRaw.add(topic);
                lateDisp.add(displayTopic);
                lateDet.add(details);
            } else {
                pendingRaw.add(topic);
                pendingDisp.add(displayTopic);
                pendingDet.add(details);
            }
        }
        conn.close();
    } catch (Exception e) { e.printStackTrace(); }

    contentPanel.add(createCategory("Pending", pendingRaw, pendingDisp, pendingDet, new Color(59, 130, 246), "PENDING", false));
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(createCategory("Submitted", doneRaw, doneDisp, doneDet, new Color(16, 185, 129), "SUBMITTED", false));
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(createCategory("Late", lateRaw, lateDisp, lateDet, new Color(239, 68, 68), "LATE", true));

    contentPanel.revalidate();
    contentPanel.repaint();
}

private String calculateTimeLeft(LocalDateTime now, LocalDateTime due) {
    if (now.isAfter(due)) {
        return "Expired";
    }
    Duration d = Duration.between(now, due);
    long days = d.toDays();
    long hours = d.toHours() % 24;
    long minutes = d.toMinutes() % 60;
    return days + "d " + hours + "h " + minutes + "m left";
}

private JPanel createCategory(final String title, final List<String> rawTopics, final List<String> displayTopics, final List<String> details, Color accentColor, final String status, final boolean isPastDue) {
    final JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setBackground(new Color(24, 24, 27));
    container.setAlignmentX(Component.CENTER_ALIGNMENT);
    container.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(40, 40, 40));
    header.setPreferredSize(new Dimension(900, 50));
    header.setMaximumSize(new Dimension(900, 50));
    header.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor));
    header.setCursor(new Cursor(Cursor.HAND_CURSOR));

    JLabel titleLabel = new JLabel(title + " (" + rawTopics.size() + ")");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Leelawadee UI", Font.BOLD, 18));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

    final JLabel toggleText = new JLabel("SHOW ");
    toggleText.setForeground(Color.LIGHT_GRAY);
    toggleText.setFont(new Font("Leelawadee UI", Font.BOLD, 12));
    toggleText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

    header.add(titleLabel, BorderLayout.WEST);
    header.add(toggleText, BorderLayout.EAST);

    final JPanel itemList = new JPanel();
    itemList.setLayout(new BoxLayout(itemList, BoxLayout.Y_AXIS));
    itemList.setBackground(new Color(30, 30, 30));
    itemList.setVisible(false);

    for (int i = 0; i < rawTopics.size(); i++) {
        final String currentRawTopic = rawTopics.get(i);
        final String currentDisplayTopic = displayTopics.get(i);
        final String currentDetail = details.get(i);

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(30, 30, 30));
        row.setMaximumSize(new Dimension(900, 60));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)));

        JLabel itemLabel = new JLabel("  " + currentDisplayTopic);
        itemLabel.setForeground(Color.WHITE);
        itemLabel.setFont(new Font("Leelawadee UI", Font.PLAIN, 16));

        JButton actionBtn = new JButton("View Details");
        actionBtn.setFont(new Font("Leelawadee UI", Font.BOLD, 12));
        actionBtn.setBackground(new Color(59, 130, 246));
        actionBtn.setForeground(Color.WHITE);

        actionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AssignmentDetailView detailView = new AssignmentDetailView(currentRawTopic, currentDetail, currentStudentId, status, isPastDue);
                detailView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshAssignments();
                    }
                });
                detailView.setVisible(true);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(30, 30, 30));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        btnPanel.add(actionBtn);

        row.add(itemLabel, BorderLayout.WEST);
        row.add(btnPanel, BorderLayout.EAST);
        itemList.add(row);
    }

    header.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            boolean isVisible = itemList.isVisible();
            itemList.setVisible(!isVisible);
            toggleText.setText(isVisible ? "SHOW " : "HIDE ");
            container.revalidate();
        }
    });

    container.add(header);
    container.add(itemList);
    return container;
}

public static void main(String[] args) {
    DatabaseManager.initialize();
    new StudentDashboard("66012345").setVisible(true);
}
}