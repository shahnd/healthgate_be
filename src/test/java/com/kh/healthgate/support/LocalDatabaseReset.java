package com.kh.healthgate.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * 데이터 손실을 감수하고 로컬 개발 DB를 현재 스키마와 개발 데이터로 복구한다.
 * 운영 애플리케이션에 포함되지 않도록 test source에서만 실행한다.
 */
public final class LocalDatabaseReset {

    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_URL = SERVER_URL + "healthgate_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "mysql";

    private LocalDatabaseReset() {
    }

    public static void main(String[] args) throws SQLException {
        confirmReset();
        recreateDatabase();
        migrateSchema();
        seedLocalData();
        printResult();
    }

    private static void confirmReset() {
        System.out.println("WARNING: localhost의 healthgate_db 데이터가 모두 삭제됩니다.");
        System.out.print("계속하려면 RESET을 입력하세요: ");

        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine() || !"RESET".equals(scanner.nextLine())) {
            throw new IllegalStateException("로컬 DB 초기화를 취소했습니다.");
        }
    }

    private static void recreateDatabase() throws SQLException {
        System.out.println("[1/4] 로컬 데이터베이스를 재생성합니다.");

        try (Connection connection = DriverManager.getConnection(
                    SERVER_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP DATABASE IF EXISTS healthgate_db");
            statement.executeUpdate("""
                    CREATE DATABASE healthgate_db
                    CHARACTER SET utf8mb4
                    COLLATE utf8mb4_0900_ai_ci
                    """);
        }
    }

    private static void migrateSchema() {
        System.out.println("[2/4] Flyway migration을 적용합니다.");

        Flyway.configure()
                .dataSource(DATABASE_URL, USERNAME, PASSWORD)
                .load()
                .migrate();
    }

    private static void seedLocalData() {
        System.out.println("[3/4] 로컬 개발 데이터를 적재합니다.");

        DataSource dataSource = new DriverManagerDataSource(
                DATABASE_URL, USERNAME, PASSWORD);
        Resource[] seedScripts = findSeedScripts();

        for (Resource seedScript : seedScripts) {
            System.out.println("  - " + seedScript.getFilename());
            new ResourceDatabasePopulator(seedScript).execute(dataSource);
        }
    }

    private static Resource[] findSeedScripts() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:db/seed/local/*.sql");
            Arrays.sort(resources, Comparator.comparing(resource ->
                    Objects.requireNonNull(resource.getFilename())));

            if (resources.length == 0) {
                throw new IllegalStateException(
                        "로컬 개발 데이터 SQL을 찾을 수 없습니다.");
            }
            return resources;
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(
                    "로컬 개발 데이터 SQL을 불러올 수 없습니다.", exception);
        }
    }

    private static void printResult() throws SQLException {
        System.out.println("[4/4] 초기화 결과를 확인합니다.");

        try (Connection connection = DriverManager.getConnection(
                    DATABASE_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement()) {
            printFlywayVersion(statement);
            printEmployeeCount(statement);
        }

        System.out.println("Local database reset completed.");
    }

    private static void printFlywayVersion(Statement statement)
            throws SQLException {
        try (ResultSet resultSet = statement.executeQuery("""
                SELECT version
                FROM flyway_schema_history
                WHERE success = TRUE
                ORDER BY installed_rank DESC
                LIMIT 1
                """)) {
            if (resultSet.next()) {
                System.out.println("Flyway version: " + resultSet.getString(1));
            }
        }
    }

    private static void printEmployeeCount(Statement statement)
            throws SQLException {
        try (ResultSet resultSet = statement.executeQuery(
                "SELECT COUNT(*) FROM employees")) {
            resultSet.next();
            System.out.println("Employee count: " + resultSet.getLong(1));
        }
    }
}
