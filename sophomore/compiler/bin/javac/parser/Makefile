all: 
	# make lexer or make parser

lexer: javac.jflex
	jflex javac.jflex

parser: javac.cup
	java -jar /home/mrain/source/homework/sophomore/compiler/lib/java-cup-11a.jar -parser Parser -symbols Symbols -interface < javac.cup
