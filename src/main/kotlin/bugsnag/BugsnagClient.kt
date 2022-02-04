package bugsnag

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.Closeable

// https://bugsnagapiv2.docs.apiary.io/
class BugsnagClient(
    private val bugsnagApiToken: String,
) : Closeable {
    private val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.NONE
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            header("X-Version", "2")
            header("Authorization", "token $bugsnagApiToken")
        }
    }

    suspend fun getOrganizations(): List<Organization> {
        return client
            .get("https://api.bugsnag.com/user/organizations")
            .body()
    }

    suspend fun getProjects(organizationId: String): List<Project> {
        return client
            .get("https://api.bugsnag.com/organizations/$organizationId/projects")
            .body()
    }

    suspend fun getStabilityTrend(projectId: String): ProjectStabilityTrend {
        return client
            .get("https://api.bugsnag.com/projects/$projectId/stability_trend")
            .body()
    }

    override fun close() {
        client.close()
    }
}
