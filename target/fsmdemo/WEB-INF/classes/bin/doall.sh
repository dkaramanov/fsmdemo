PROTOCOL=$1;shift
SCRIBBLE=$1;shift
echo $@

for i in $@
do
	echo projecting out $i from the $PROTOCOL protocol in $SCRIBBLE into an fsm
	#./scribblec.sh SupplierInfoNoFairBeta.scr -fsm PartnershipSupplier $i >generated/$i.fsm
	echo transforming the fsm for $i into an easyFSM config file
	#./fsmxlator.sh generated/${i}.fsm -easyFSM ${i} org.tw.scribble.example >generated/${i}_config.txt
	echo transforming the fsm for $i into prettier version
	#./fsmxlator.sh generated/${i}.fsm -dot ${i} org.tw.scribble.example >generated/${i}_gen.fsm
	echo transforming the prettier version of $i into a png
	#dot -Tpng generated/${i}_gen.fsm >generated/${i}_gen.png
done
