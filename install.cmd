@echo Installing swarm...
@rm -rf "c:\Program Files\swarm"
@mkdir "c:\Program Files\swarm"
@mkdir "c:\Program Files\swarm\libs"
@mkdir "c:\Program Files\swarm\bin"
@echo Setting Path.
@set path="c:\Program Files\swarm\bin";%path%
@echo Copying Libraries.
@copy libs\*.jar "c:\Program Files\swarm\libs" 1> NUL
@copy build\jar\Swarm.jar "c:\Program Files\swarm\libs" 1> NUL
@echo Copying Scripts.
@copy swarm.cmd "c:\Program Files\swarm\bin"
@echo Done! type 'swarm' to get started.