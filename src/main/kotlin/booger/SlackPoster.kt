package booger

import com.slack.api.Slack
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import kotlinx.coroutines.future.await
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DecimalFormat

suspend fun postToSlack(
    report: BugsnagReport,
    slackToken: String,
    slackChannelId: String,
    stabilityCenterUrl: String,
) {
    // Slack SDK for Java https://slack.dev/java-slack-sdk/
    val slack = Slack.getInstance()
    val logger: Logger = LoggerFactory.getLogger("SlackPoster")

    val sessionEmoji = ":waves:"
    val userEmoji = ":bust_in_silhouette:"

    // https://slack.dev/java-slack-sdk/guides/composing-messages
    val mainMessageResponse = slack.methodsAsync(slackToken).chatPostMessage { req ->
        req
            .channel(slackChannelId)
            .text("Bugsnag stability report")
            .blocks {
                header { text("Bugsnag stability report :ghost:") }
                section {
                    val session = report.overallSessionStability.formatPercentage()
                    val user = report.overallUserStability.formatPercentage()
                    fields {
                        markdownText("${sessionEmoji}30d Session stability\n*$session*")
                        markdownText("${userEmoji}30d User stability\n*$user*")
                    }
                }
                context { markdownText("<$stabilityCenterUrl|View stability center>") }
            }
    }.await()
    if (!mainMessageResponse.errors.isNullOrEmpty()) {
        mainMessageResponse.errors.forEach { logger.error(it) }
    }

    val threadMessageResponse = slack.methodsAsync(slackToken).chatPostMessage { req ->
        req
            .channel(slackChannelId)
            .threadTs(mainMessageResponse.ts)
            .text("Bugsnag project stability report")
            .blocks {
                report.projectReports.forEach { project ->
                    section {
                        val session = "$sessionEmoji${project.sessionStability.formatPercentage()}"
                        val user = "$userEmoji${project.userStability.formatPercentage()}"
                        markdownText("*${project.name}*\n$session $user")
                        accessory {
                            button {
                                text("View")
                                url(project.url)
                            }
                        }
                    }
                }
            }
    }.await()
    if (!threadMessageResponse.errors.isNullOrEmpty()) {
        threadMessageResponse.errors.forEach { logger.error(it) }
    }
}

private val format = DecimalFormat("#.000%")
private fun Float.formatPercentage(): String {
    val result = format.format(this)
    if (result == "100.000%") return "100%"
    return result
}
