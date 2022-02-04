# Booger

Generates an aggregate stability report of multiple Bugsnag projects and posts it to Slack.

### Develop
In the Run / Debug configuration, specify under Configuration > Program arguments:

- `--bugsnag-token` - the Bugsnag API token
- `--slack-token` - the Slack API token
- `--slack-channel-id` - the Slack channel to post to

#### Get a Bugsnag token
1. [My Account][bugsnag-my-account] > Personal auth tokens

#### Get a Slack token
1. [Create a Slack app][slack-apps]
2. OAuth & Permissions > Scopes > `chat:write`
3. Install app into workspace, add to channel
4. OAuth & Permissions > Bot User OAuth Token

[Read more on Slack token types][slack-token-types].

#### Get a Slack channel id
1. Open channel details
2. Find Channel ID in the bottom

### Run

To build and run the program using Gradle, [use `run`][gradle-app-plugin]:
```
./gradlew run --args="--bugsnag-token=TOKEN --slack-token=TOKEN --slack-channel-id=CHANNEL_ID"
```

[bugsnag-my-account]: https://app.bugsnag.com/settings/my-account
[slack-apps]: https://api.slack.com/apps
[slack-token-types]: https://api.slack.com/authentication/token-types
[gradle-app-plugin]: https://docs.gradle.org/current/userguide/application_plugin.html#sec:application_usage
