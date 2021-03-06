package com.procurement.access.dao

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.access.application.exception.repository.ReadEntityException
import com.procurement.access.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.access.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.access.model.entity.TenderProcessEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class TenderProcessDao(private val session: Session) {

    companion object {
        private const val keySpace = "ocds"
        private const val tableName = "access_tender"
        private const val columnCpid = "cp_id"
        private const val columnToken = "token_entity"
        private const val columnStage = "stage"
        private const val columnCreateDate = "created_date"
        private const val columnOwner = "owner"
        private const val columnJsonData = "json_data"

        private const val FIND_AUTH_BY_CPID_CQL = """
               SELECT $columnToken,
                      $columnOwner
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
            """
    }

    private val preparedFindAuthByCpidCQL = session.prepare(FIND_AUTH_BY_CPID_CQL)

    fun save(entity: TenderProcessEntity) {
        val insert = insertInto(tableName)
        insert.value(columnCpid, entity.cpId)
            .value(columnToken, entity.token)
            .value(columnOwner, entity.owner)
            .value(columnStage, entity.stage)
            .value(columnCreateDate, entity.createdDate.toCassandraTimestamp())
            .value(columnJsonData, entity.jsonData)
        session.execute(insert)
    }

    fun getByCpIdAndStage(cpId: String, stage: String): TenderProcessEntity? {
        val query = select()
            .all()
            .from(tableName)
            .where(eq(columnCpid, cpId))
            .and(eq(columnStage, stage)).limit(1)
        val row = session.execute(query).one()
        return if (row != null) TenderProcessEntity(
            row.getString(columnCpid),
            row.getUUID(columnToken),
            row.getString(columnOwner),
            row.getString(columnStage),
            row.getTimestamp(columnCreateDate).toLocalDateTime(),
            row.getString(columnJsonData)
        ) else null
    }

    fun findAuthByCpid(cpid: String): List<Auth> {
        val query = preparedFindAuthByCpidCQL.bind()
            .apply {
                setString(columnCpid, cpid)
            }

        val resultSet = load(query)
        return resultSet.map { convertToContractEntity(it) }
    }

    protected fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw ReadEntityException(message = "Error read auth data from the database.", cause = exception)
    }

    private fun convertToContractEntity(row: Row): Auth = Auth(
        token = row.getUUID(columnToken),
        owner = row.getString(columnOwner)
    )

    class Auth(val token: UUID, val owner: String)
}
