zip -r -b . $1.app.dSYM.zip $1.app.dSYM
rm -rf Payload
rm -rf $1.ipa
mkdir Payload
cp -R $1.app Payload
zip -r -b . $1.ipa Payload
rem /Users/bpascazio/teamcitypush "%teamcity.build.branch%" %teamcity.build.id% %build.vcs.number%
cd
