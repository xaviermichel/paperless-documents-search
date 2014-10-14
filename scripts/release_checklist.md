Release
=======

- [ ] Check if mvn test success
- [ ] Check if migration works
- [ ] Update version with `update_version.sh`
- [ ] Create release with `packager.sh`
- [ ] Deploy maven artifacts with `maven_deployer.sh`
- [ ] Make the release commit
- [ ] Add git tag (`git tag -a v5.3 -m 'Version 5.3'`)
- [ ] Update pom for the next snapshot with `update_version.sh`
- [ ] Deploy maven artifacts with `maven_deployer.sh`
- [ ] Make the next snapshot commit
- [ ] Push with tags (`git push --tags`)
- [ ] Make the release on github (upload generated zip)
- [ ] Close the current milestone and open the next one

