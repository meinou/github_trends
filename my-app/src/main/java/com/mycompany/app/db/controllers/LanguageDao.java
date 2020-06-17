package com.mycompany.app.db.controllers;

import com.mycompany.app.db.Dao;
import com.mycompany.app.db.JDBCConnect;
import com.mycompany.app.db.models.LangModel;

import java.sql.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LanguageDao implements Dao<LangModel, Integer> {

    private static final Logger LOGGER = Logger.getLogger(PostgresDao.class.getName());

    private final Optional<Connection> connection;

    public LanguageDao(String credentials) throws ClassNotFoundException {
        String[] db_key = credentials.split(" ");
        this.connection =  Optional.ofNullable(new JDBCConnect().getConnection(db_key[0], db_key[1], db_key[2]));
    }

    @Override
    public Optional<LangModel> get(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<LangModel> getAll() {
        return null;
    }

    @Override
    public Optional<Integer> save(LangModel lang) {
        final String sql = "INSERT INTO langs (repo_name, lang, bytes) VALUES(?, ?, ?)";
        String message = "The customer to be added should not be null";
        LangModel nonNullExtention = Objects.requireNonNull(lang, message);

        return connection.flatMap(conn -> {
            Optional<Integer> generatedId = Optional.empty();

            try (PreparedStatement statement =
                         conn.prepareStatement(
                                 sql,
                                 Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, nonNullExtention.getRepo());
                statement.setString(2, nonNullExtention.getLang());
                statement.setLong(3, nonNullExtention.getBytes());
                int numberOfInsertedRows = statement.executeUpdate();

                // Retrieve the auto-generated id
                if (numberOfInsertedRows > 0) {
                    try (ResultSet resultSet = statement.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            generatedId = Optional.of(resultSet.getInt(1));
                        }
                    }
                }
                // Too much of info
                LOGGER.log(
                        Level.INFO,
                        "{0} created successfully? {1}",
                        new Object[]{nonNullExtention,
                                (numberOfInsertedRows > 0)});
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                System.out.println(ex);
            }

            return generatedId;
        });
    }

    @Override
    public void update(LangModel langModel) {

    }

    @Override
    public void delete(LangModel langModel) {

    }
}
