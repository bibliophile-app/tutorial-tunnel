package com.bibliophile.utils

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Objeto responsável por gerenciar o banco de dados de teste.
 * Ele utiliza o banco de dados H2 em memória e o Flyway para gerenciar as migrações.
 */
object TestDatabaseFactory {

    // URL de conexão com o banco de dados H2 em memória
    private const val DB_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
    private const val DB_DRIVER = "org.h2.Driver"

    /**
     * Inicializa o banco de dados de teste.
     * Conecta ao banco de dados H2 e aplica as migrações definidas no Flyway.
     */
    fun init() {
        // Conecta ao banco de dados H2
        val db = Database.connect(
            url = DB_URL,
            driver = DB_DRIVER
        )

        // Configura e executa as migrações do Flyway
        val flyway = Flyway.configure()
            .dataSource(DB_URL, null, null) // Não há necessidade de usuário e senha para o H2 em memória
            .locations("classpath:migrations") // Local das migrações Flyway
            .load()
            .migrate() // Aplica as migrações
    }

    /**
     * Reseta o banco de dados de teste.
     * Limpa todas as tabelas e reaplica as migrações definidas no Flyway.
     */
    fun reset() {
        // Configura o Flyway para o banco de dados H2
        val flyway = Flyway.configure()
            .dataSource(DB_URL, null, null) // Não há necessidade de usuário e senha para o H2 em memória
            .locations("classpath:migrations") // Local das migrações Flyway
            .load()
        
        flyway.clean() // Limpa todas as tabelas do banco de dados
        flyway.migrate() // Reaplica as migrações
    }
}