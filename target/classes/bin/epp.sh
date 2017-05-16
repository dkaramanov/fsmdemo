SCRIBBLE=$1;shift
PROTOCOL=$1;shift
echo $@
pwd

for i in $@
do
	echo projecting out $i from the $PROTOCOL protocol in $SCRIBBLE into an fsm
	$SCRIBBLEDIR/bin/scribblec.sh $SCRIBBLE  -fsm $PROTOCOL $i >$SCRIBBLEDIR/bin/generated/$i.fsm
	echo transforming the fsm for $i into an easyFSM config file
	$SCRIBBLEDIR/bin/fsmxlator.sh $SCRIBBLEDIR/bin/generated/${i}.fsm -easyFSM ${i} org.estafet.scribble.example >$SCRIBBLEDIR/bin/generated/${i}_config.txt
done
