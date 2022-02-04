package booger

import bugsnag.BugsnagClient
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.coroutines.runBlocking

class Booger : CliktCommand() {

    private val bugsnagToken by option(help = "Bugsnag API token").required()

    private val bugsnagOrganizationId by option(help = "Bugsnag organization id")

    private val stabilityCenterUrl by option(help = "Bugsnag stability center URL")
        .default("https://app.bugsnag.com/organizations/trafi/stability-center")

    private val slackToken by option(help = "Slack API token").required()

    private val slackChannelId by option(help = "Slack channel id").required()

    override fun run() = runBlocking {
        val report = BugsnagClient(
            bugsnagApiToken = bugsnagToken,
        ).use {
            val organizationId = bugsnagOrganizationId ?: it.getOrganizations().first().id
            it.getBugsnagReport(organizationId)
        }
        postToSlack(
            report,
            slackToken = slackToken,
            slackChannelId = slackChannelId,
            stabilityCenterUrl = stabilityCenterUrl,
        )
    }
}
