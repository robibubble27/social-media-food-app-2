package com.example.application.dao;

import com.example.application.connection.ConnectionFactory;
import com.example.application.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO {
    private String createSelectNameQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT user.username " +
                  "FROM social_media.user " +
                  "WHERE ID = ?;");
        return sb.toString();
    }

    private String createGetUserQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT firstName, lastName, username, password, description " +
                "FROM user " +
                "WHERE ID = ?;");
        return sb.toString();
    }

    public User getUser(String userID) {
        Connection con = null;
        PreparedStatement selectStatement = null;
        String query = createGetUserQuery();
        ResultSet resultSet = null;

        try {
            con = ConnectionFactory.getConnection();
            selectStatement = con.prepareStatement(query);
            selectStatement.setString(1, userID);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                User u = new User(Integer.parseInt(userID), resultSet.getString(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));

                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(selectStatement);
            ConnectionFactory.close(con);
        }
        return null;
    }


    public String getUsername(String userID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectNameQuery();

        try {
            connection = (Connection) ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, userID);
            resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultSet.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return "";
    }

    private String createFollowedUsersQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT followed_ID " +
                "FROM follow " +
                "WHERE follower_ID = ?;");
        return sb.toString();
    }
    public ArrayList<Integer> getFollowedUsers(int userID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createFollowedUsersQuery();

        try {
            connection = (Connection) ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(userID));
            resultSet = statement.executeQuery();

            ArrayList<Integer> followedUsers = new ArrayList<>();
            while (resultSet.next()){
                followedUsers.add(resultSet.getInt(1));
            }
            return followedUsers;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    public boolean getFollowStatus(int followedID, int followerID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = new String(
                 "SELECT followed_ID " +
                        "FROM follow " +
                        "WHERE follower_ID = ? AND followed_ID = ?;");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(followerID));
            statement.setString(2, String.valueOf(followedID));
            resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return false;
    }

    public boolean unfollow(int followedID, int followerID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = new String(
                "DELETE FROM follow " +
                "WHERE followed_ID = ? AND follower_ID = ?");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(followedID));
            statement.setString(2, String.valueOf(followerID));
            statement.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return false;
    }

    public boolean follow(int followedID, int followerID) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = new String(
                "INSERT INTO follow (followed_ID, follower_ID)" +
                "VALUES (?,?)");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(followedID));
            statement.setString(2, String.valueOf(followerID));
            int result = statement.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return false;
    }

    public boolean editUser(User u) {
        Connection con = null;
        PreparedStatement insertStatement = null;
        String query =
                "UPDATE user " +
                "SET firstName = ?, lastName = ?, username = ?, password = ?," +
                "description = ?, image = ? " +
                "WHERE ID = ? ";
        ResultSet resultSet = null;

        try {
            con = ConnectionFactory.getConnection();
            insertStatement= con.prepareStatement(query);
            insertStatement.setString(1, u.getFirstName());
            insertStatement.setString(2, u.getLastName());
            insertStatement.setString(3, u.getUsername());
            insertStatement.setString(4, u.getPassword());
            insertStatement.setString(5, u.getDescription());
            insertStatement.setString(6, u.getImage());
            insertStatement.setString(7, String.valueOf(u.getUserID()));
            insertStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeUser(int u) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String queryPre1 =
                "DELETE FROM comment " +
                "WHERE user_id = ?";
        String queryPre2 =
                "DELETE FROM follow " +
                "WHERE followed_ID = ? OR follower_ID = ?";
        String queryPre3 =
                "DELETE FROM liked_posts " +
                "WHERE user_id = ? ";
        String queryPre4 =
                "DELETE FROM post " +
                "WHERE user_id = ? ";
        String query =
                "DELETE FROM user " +
                "WHERE ID = ?";


        try {
            connection = (Connection) ConnectionFactory.getConnection();
            statement = connection.prepareStatement(queryPre1);
            statement.setInt(1, u);
            statement.executeUpdate();

            statement = connection.prepareStatement(queryPre2);
            statement.setInt(1, u);
            statement.executeUpdate();

            statement = connection.prepareStatement(queryPre3);
            statement.setInt(1, u);
            statement.executeUpdate();

            statement = connection.prepareStatement(queryPre4);
            statement.setInt(1, u);
            statement.executeUpdate();

            statement = connection.prepareStatement(query);
            statement.setInt(1, u);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
}
