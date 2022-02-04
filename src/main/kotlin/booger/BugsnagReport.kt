package booger

import bugsnag.BugsnagClient
import bugsnag.Project
import bugsnag.TimelinePoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class BugsnagReport(
    val overallSessionStability: Float,
    val overallUserStability: Float,
    val projectReports: List<ProjectReport>,
)

data class ProjectReport(
    val name: String,
    val url: String,
    val sessionStability: Float,
    val userStability: Float,
)

suspend fun BugsnagClient.getBugsnagReport(organizationId: String): BugsnagReport {
    val projects: List<Project> = getProjects(organizationId).sortedBy { it.slug + it.type }
    val stabilityTrends = coroutineScope {
        projects.map { project ->
            async {
                val stability = getStabilityTrend(project.id)
                project to stability
            }
        }.awaitAll()
    }
    val projectReports = stabilityTrends
        .filter { (_, trend) -> trend.releaseStageName == "production" }
        .map { (project, trend) ->
            ProjectReport(
                name = project.name,
                url = project.htmlUrl,
                sessionStability = trend.timelinePoints.sessionStability(),
                userStability = trend.timelinePoints.userStability(),
            )
        }

    return BugsnagReport(
        overallSessionStability = stabilityTrends.flatMap { (_, t) -> t.timelinePoints }.sessionStability(),
        overallUserStability = stabilityTrends.flatMap { (_, t) -> t.timelinePoints }.userStability(),
        projectReports = projectReports,
    )
}

private val List<TimelinePoint>.totalSessionCount get() = sumOf { it.totalSessionCount }
private val List<TimelinePoint>.unhandledSessionCount get() = sumOf { it.unhandledSessionCount }
private val List<TimelinePoint>.totalUserCount get() = sumOf { it.usersSeen }
private val List<TimelinePoint>.userWithUnhandledCount get() = sumOf { it.usersWithUnhandled }

private fun List<TimelinePoint>.sessionStability(): Float {
    val totalSessionCount = totalSessionCount
    if (totalSessionCount < 1) return 1f
    return (totalSessionCount - unhandledSessionCount).toFloat() / totalSessionCount
}

private fun List<TimelinePoint>.userStability(): Float {
    val totalUserCount = totalUserCount
    if (totalUserCount < 1) return 1f
    return (totalUserCount - userWithUnhandledCount).toFloat() / totalUserCount
}
