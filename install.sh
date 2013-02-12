sudo cp swarml /usr/bin/swarm
sudo chmod 755 /usr/bin/swarm 
sudo rm -rf /usr/lib/swarm
sudo mkdir /usr/lib/swarm
sudo cp libs/* /usr/lib/swarm
sudo cp build/jar/Swarm.jar /usr/lib/swarm/Swarm.jar
