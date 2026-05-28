/**
 * Base de dados Room da aplicação DrivePulse.
 *
 * Camada: Data
 * Feature: Run
 *
 * Instância singleton gerida pelo Hilt via [DatabaseModule].
 * Inclui todas as entidades e fornece os DAOs.
 */
package com.drivepulse.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drivepulse.data.local.dao.CoordinateDao
import com.drivepulse.data.local.dao.RunDao

/**
 * Base de dados local Room da aplicação.
 *
 * Versão: 1 (incrementar e fornecer [Migration] ao adicionar novas entidades ou colunas).
 * exportSchema: false para projetos académicos — em produção deveria ser true.
 */
@Database(
    entities = [
        RunEntity::class,
        CoordinateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DrivePulseDatabase : RoomDatabase() {

    /**
     * Fornece o DAO para operações na tabela "runs".
     */
    abstract fun runDao(): RunDao

    /**
     * Fornece o DAO para operações na tabela "run_coordinates".
     */
    abstract fun coordinateDao(): CoordinateDao

    companion object {
        /** Nome do ficheiro da base de dados no dispositivo. */
        const val DATABASE_NAME = "drivepulse.db"
    }
}
