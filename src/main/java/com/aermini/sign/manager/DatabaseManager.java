package com.aermini.sign.manager;

import com.aermini.sign.AerSign;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseManager {
    private Connection connection;
    public void initialize() {
        String address = AerSign.getInstance().getConfig().getString("database.address");
        String database = AerSign.getInstance().getConfig().getString("database.database");
        String username = AerSign.getInstance().getConfig().getString("database.username");
        String password = AerSign.getInstance().getConfig().getString("database.password");
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + address + "/" + database + "?useSSL=false&autoReconnect=true",
                username,
                password
            );
            createTables();
            AerSign.getInstance().getLogger().info("数据库连接成功");
        } catch (SQLException e) {
            AerSign.getInstance().getLogger().severe("数据库连接失败");
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS aersign_data (" +
                "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                "total_sign_count INT NOT NULL DEFAULT 0, " +
                "last_sign_date DATE" +
                ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPlayerData(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT uuid FROM aersign_data WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayerData(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO aersign_data (uuid, total_sign_count, last_sign_date) VALUES (?, 0, NULL)")) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalSignCount(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT total_sign_count FROM aersign_data WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_sign_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void incrementSignCount(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "UPDATE aersign_data SET total_sign_count = total_sign_count + 1, last_sign_date = CURDATE() WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Date getLastSignDate(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT last_sign_date FROM aersign_data WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("last_sign_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasSignedToday(String uuid) {
        Date lastSignDate = getLastSignDate(uuid);
        if (lastSignDate == null) {
            return false;
        }
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastSignDateStr = sdf.format(lastSignDate);
        String todayStr = sdf.format(today);
        return lastSignDateStr.equals(todayStr);
    }

    public void resetTodaySignStatus(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "UPDATE aersign_data SET last_sign_date = NULL WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetAllSignData(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "UPDATE aersign_data SET total_sign_count = 0, last_sign_date = NULL WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
