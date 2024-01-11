package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.connectionpool.FiliDataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class App 
{
    @SneakyThrows
    public static void main(String[] args) {
        DataSource dataSource = new FiliDataSource("postgres", "mysecretpassword", "jdbc:postgresql://localhost:5432/postgres");
//        DataSource dataSource = initDb();
        var start = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executor.submit( () -> {
                try (var connection = dataSource.getConnection()) {
                    System.out.println(connection);
                    try (var selectStatement = connection.createStatement()) {
                        selectStatement.executeQuery("select random()"); // just to call the DB
                    }
                }
                catch (Exception ignored) {}
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        var end = System.nanoTime();
        System.out.println((end - start) / 1000_000 + "ms");
    }

    private static DataSource initDb() { // todo: refactor to use custom pooled data source
        var dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("mysecretpassword");
        return dataSource;
    }
}
