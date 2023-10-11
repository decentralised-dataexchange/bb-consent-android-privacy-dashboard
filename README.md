<h1 align="center">
    GovStack Consent BB Privacy Dashboard (Android SDK)
</h1>

<p align="center">
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/decentralised-dataexchange/bb-consent-android-privacy-dashboard?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/decentralised-dataexchange/bb-consent-android-privacy-dashboard?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-yellowgreen?style=flat"></a>
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#about">Configuration</a> •
  <a href="#about">Integration</a> •
  <a href="#release-status">Release Status</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#licensing">Licensing</a>
</p>

## About

This repository hosts source code for the reference implementation of the GovStack Consent Building Block Privacy Dashboard towards individuals.

## Configuration

Gradle:
```gradle
dependencies {
  implementation 'com.github.decentralised-dataexchange:bb-consent-android-privacy-dashboard:<latest release>'
}
```

Maven:
```xml
<dependency>
  <groupId>com.github.decentralised-dataexchange</groupId>
  <artifactId>bb-consent-android-privacy-dashboard</artifactId>
  <version><latest release></version>
</dependency>
```

## Integration

We can initiate the privacy dashboard by calling the below.
```
PrivacyDashboard.showPrivacyDashboard().withApiKey(<API key>)
                .withUserId(<User ID>)
                .withOrgId(<Org ID>)
                .withBaseUrl(<Base URL>).start(this)
```

To set the language we just need to add the following before the `start(this)`
```
.withLocale(<language code>)
```

To enable user requests we just need to add the following before the `start(this)`
```
.enableUserRequest()
```

To enable Ask me we just need to add the following before the `start(this)`
```
.enableAskMe()
```

## Release Status

Refer to the [wiki page](https://github.com/decentralised-dataexchange/bb-consent-docs/wiki/wps-and-deliverables) for the latest status of the deliverables. 

## Other resources

* Wiki - https://github.com/decentralised-dataexchange/consent-dev-docs/wiki

## Contributing

Feel free to improve the plugin and send us a pull request. If you find any problems, please create an issue in this repo.

## Licensing
Copyright (c) 2023-25

Licensed under the Apache 2.0 License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the LICENSE for the specific language governing permissions and limitations under the License.