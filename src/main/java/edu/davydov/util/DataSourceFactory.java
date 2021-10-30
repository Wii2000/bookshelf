package edu.davydov.util;

import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This util class responsible for providing DB connection.
 */
public class DataSourceFactory {
    private static final Logger log = getLogger(DataSourceFactory.class);
//TODO: add environment constant
    private static final String RESOURCE_PATH = "/Users/dmitrijdavydov/work/int_course/bookshelf/src/main/resources/db/mysql.properties";
    private static Connection connection = null;

    private DataSourceFactory() {
    }
//TODO: create pool connection
    public static Connection getConnection() {
        if (connection == null) {
            try (FileInputStream fileInputStream = new FileInputStream(RESOURCE_PATH)) {
                Properties prop = new Properties();
                prop.load(fileInputStream);
                Class.forName(prop.getProperty("driver"));
                connection = DriverManager.getConnection(
                        prop.getProperty("database.url"),
                        prop.getProperty("database.username"),
                        prop.getProperty("database.password")
                );
            } catch (SQLException ex) {
                log.error("SQL Exception", ex);
            } catch (FileNotFoundException ex) {
                log.error("Properties file don't found", ex);
            } catch (IOException ex) {
                log.error("IO Error", ex);
            } catch (ClassNotFoundException ex) {
                log.error("ClassNotFoundException", ex);
            }
        }
        return connection;
    }
}
