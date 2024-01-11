package com.bobocode.connectionpool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;
import javax.sql.DataSource;
import lombok.SneakyThrows;

public class FiliDataSource implements DataSource {

  private final BlockingDeque<Connection> connectionPool = new LinkedBlockingDeque<>();

  @SneakyThrows
  public FiliDataSource(String username, String password, String url) {
    Properties properties = new Properties();
    properties.setProperty("user", username);
    properties.setProperty("password", password);
    Driver driver = DriverManager.getDriver(url);
    for (int i = 0; i < 20; i++) {
      Connection connection = driver.connect(url, properties);
      connectionPool.add(new FiliConnection(connection, this));
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    return connectionPool.poll();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return null;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return null;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {

  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {

  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return 0;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  public void returnConnection(FiliConnection filiConnection) {
    connectionPool.add(filiConnection);
  }
}
