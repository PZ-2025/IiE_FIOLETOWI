package com.example.projekt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TaskDB {
    private static final String URL = "jdbc:mysql://localhost:3306/HurtPolSan";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password"; // <- Zmień na swoje hasło

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static ObservableList<Task> getAllTasksForEmployee(int employeeId) {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        String sql = """
                SELECT z.id_zadania, z.nazwa, s.nazwa AS status, p.nazwa AS priorytet, z.data_rozpoczecia
                FROM zadania z
                JOIN statusy s ON z.id_statusu = s.id_statusu
                JOIN priorytety p ON z.id_priorytetu = p.id_priorytetu
                WHERE z.id_pracownika = ?
                ORDER BY z.data_rozpoczecia DESC
                """;
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id_zadania"),
                        rs.getString("nazwa"),
                        rs.getString("status"),
                        rs.getString("priorytet"),
                        rs.getString("data_rozpoczecia"),
                        rs.getString("produkt"),
                        rs.getString("kierunek")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static void markTaskAsCompleted(int taskId) {
        String updateSql = "UPDATE zadania SET id_statusu = 4 WHERE id_zadania = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTask(int taskId) {
        String deleteSql = "DELETE FROM zadania WHERE id_zadania = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTask(Task task, int employeeId) {
        String insertSql = """
                INSERT INTO zadania (id_pracownika, nazwa, id_statusu, id_priorytetu, data_rozpoczecia)
                VALUES (?, ?, 1, ?, ?)
                """;
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, task.nazwaProperty().get());
            stmt.setInt(3, getPriorityIdByName(task.priorytetProperty().get(), conn));
            stmt.setString(4, task.dataProperty().get());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getPriorityIdByName(String priority, Connection conn) throws SQLException {
        String sql = "SELECT id_priorytetu FROM priorytety WHERE nazwa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, priority);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_priorytetu");
        }
        return 1; // domyślnie "Niski"
    }
}
