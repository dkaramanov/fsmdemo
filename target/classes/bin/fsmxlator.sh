#!/bin/sh

# Directory containing Scribble jars
LIB=lib

PRG=`basename "$0"`
DIR=`dirname "$0"`   # Non Cygwin..

usage() {
  echo Usage:  'fsmxlator.sh <fsm-dot-file> [option] <role> <package>...'
  cat <<EOF
  
 <SCRFILE>     Source Scribble module (.scr file) 
  
 Options: 
  -h,                                         Show this info and exit
  -easyFSM 				      Generate easyFSM config file
  -dot 					      Generate easy to read dot file
EOF
}

fixpath() {
  windows=0

  if [ `uname | grep -c CYGWIN` -ne 0 ]; then
    windows=1
  fi

  cp="$1"
  if [ "$windows" = 1 ]; then
      cygpath -pw "$cp"
  else
      echo "$cp"
  fi
}

ARGS=

CLASSPATH=$CLASSPATH':'$DIR'/../'$LIB'/antlr-complete-4.4.jar
CLASSPATH=$CLASSPATH':'$DIR'/../'$LIB'/log4j-1.2.8.jar
CLASSPATH=$CLASSPATH':'$DIR'/../'$LIB'/fsmxlator-0.1.jar
CLASSPATH="'"`fixpath "$CLASSPATH"`"'"

usage=0
verbose=0
while true; do
    case "$1" in
        "")
            break
            ;;
        -h)
            usage=1
            break
            ;;
        --help)
            usage=1
            break
            ;;
        *)
            ARGS="$ARGS '$1'"
            shift
            ;;
    esac
done

if [ "$usage" = 1 ]; then
  usage
  exit 0
fi

CMD='java -cp '$CLASSPATH' com.fsm2Java.grammar.grammarhandler.MyDOT2FSM'

fsmxlatec() {
  eval $CMD "$@"
}

if [ "$verbose" = 1 ]; then
  echo $CMD "$ARGS"
fi

fsmxlatec "$ARGS"

