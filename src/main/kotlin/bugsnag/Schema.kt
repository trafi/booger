package bugsnag

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://bugsnagapiv2.docs.apiary.io/#reference/organizations/organizations/view-an-organization
@Serializable
data class Organization(
    val id: String,
    val name: String,
    val slug: String,
)

// https://bugsnagapiv2.docs.apiary.io/#reference/projects/projects/list-an-organization's-projects
@Serializable
data class Project(
    val id: String,
    val name: String,
    val type: ProjectType,
    @SerialName("open_error_count")
    val openErrorCount: Long,
    @SerialName("for_review_error_count")
    val forReviewErrorCount: Long,
    @SerialName("html_url")
    val htmlUrl: String,
    val slug: String,
)

@Serializable
enum class ProjectType {
    @SerialName("android")
    Android,
    @Suppress("EnumEntryName")
    @SerialName("ios")
    iOS,
}

// https://bugsnagapiv2.docs.apiary.io/#reference/projects/stability-trend/view-the-stability-trend-for-a-project
@Serializable
data class ProjectStabilityTrend(
    @SerialName("project_id")
    val projectId: String,
    @SerialName("release_stage_name")
    val releaseStageName: String,
    @SerialName("timeline_points")
    val timelinePoints: List<TimelinePoint>,
)

@Serializable
data class TimelinePoint(
    @SerialName("bucket_start")
    val bucketStart: Instant,
    @SerialName("bucket_end")
    val bucketEnd: Instant,
    @SerialName("total_sessions_count")
    val totalSessionCount: Long,
    @SerialName("unhandled_sessions_count")
    val unhandledSessionCount: Long,
    @SerialName("users_seen")
    val usersSeen: Long,
    @SerialName("users_with_unhandled")
    val usersWithUnhandled: Long,
)
