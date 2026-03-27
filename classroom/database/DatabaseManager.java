package classroom.database;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/classroom_db?useUnicode=true&characterEncoding=UTF8";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // เปลี่ยนจาก DATE เป็น DATETIME
            stmt.execute("CREATE TABLE IF NOT EXISTS assignments (id INT AUTO_INCREMENT PRIMARY KEY, topic VARCHAR(255) NOT NULL, details TEXT, due_date DATETIME)");
            stmt.execute("CREATE TABLE IF NOT EXISTS submissions (id INT AUTO_INCREMENT PRIMARY KEY, topic VARCHAR(255) NOT NULL, student_id VARCHAR(50) NOT NULL, file_path VARCHAR(500) NOT NULL, submit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void addAssignment(String topic, String details, String dueDate) {
        // รองรับการบันทึกเวลา ชั่วโมง:นาที
        String sql = "INSERT INTO assignments(topic, details, due_date) VALUES(?, ?, STR_TO_DATE(?, '%Y/%m/%d %H:%i'))";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topic);
            pstmt.setString(2, details);
            pstmt.setString(3, dueDate);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void submitWork(String topic, String studentId, String filePath) {
        String sql = "INSERT INTO submissions(topic, student_id, file_path) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topic);
            pstmt.setString(2, studentId);
            pstmt.setString(3, filePath);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void cancelSubmission(String topic, String studentId) {
        String sql = "DELETE FROM submissions WHERE topic = ? AND student_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topic);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}