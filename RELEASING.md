# Releasing to Maven Central

The library publishes as `net.hebrewcalendar:hebrewcalendar` through the
[Central Portal](https://central.sonatype.com). Ordinary builds are unchanged;
everything below happens under the `release` profile.

## One-time setup

1. **Claim the namespace.** On the Central Portal, add the namespace
   `net.hebrewcalendar`. It is verified by a DNS TXT record on
   `hebrewcalendar.net` containing the key the Portal gives you.
2. **Generate a user token** on the Portal and put it in `~/.m2/settings.xml`
   as the server `central` — the id the pom's publishing plugin names:

   ```xml
   <server>
     <id>central</id>
     <username>TOKEN-USERNAME</username>
     <password>TOKEN-PASSWORD</password>
   </server>
   ```

3. **Have a GPG key** and publish it to a keyserver, so the signatures can be
   checked:

   ```bash
   gpg --gen-key
   gpg --keyserver keyserver.ubuntu.com --send-keys <key id>
   ```

   Maven finds the key through `gpg-agent`; pass `-Dgpg.keyname=<key id>` if
   there is more than one.

## Each release

1. Set the version in `pom.xml` — released versions are plain, without
   `-SNAPSHOT`, and the tag matches: `2.1.0` and `v2.1.0`.
2. `mvn clean test` — the whole suite, including the comparisons against
   opentorah.
3. `mvn -Prelease clean deploy`
4. The Portal holds the upload as a draft. Check the contents there and
   release it. (Set `autoPublish` to `true` in the pom to skip that step once
   the process is familiar.)
5. Tag and push: `git tag v2.1.0 && git push origin v2.1.0`, then draft the
   GitHub release from that tag.

## What gets published

The jar, a sources jar and a javadoc jar, each with a `.asc` signature, plus
the pom. The data classes generated from `hebrewcalendar-data` are compiled
into the jar, so a consumer needs nothing else.
