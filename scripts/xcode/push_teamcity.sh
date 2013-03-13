#!/bin/bash
curl https://testflightapp.com/api/builds.json  -F file=@$1.ipa  -F dsym=@%1.app.dSYM.zip -F api_token=$2 -F team_token=$3  -F notify=True -F notes="Built by Swarm"  -F distribution_lists=$3;

