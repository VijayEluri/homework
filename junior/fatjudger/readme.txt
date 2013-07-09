things you need to modify:
1. test.conf:
	a configure file for the judger.
	(1) you need to specify the absolute path to your db.
		example: Fatworm.Url = jdbc:fatworm://D:/fatjudger/tmp
	(2 optional) specify the path to answers. 
		In your case, set it to the path of 'basic_answer'
		(the answers to 'basic' test set which I shared to you).
		example: directory.answer = ./tests/basic_answers
	(3 optional) specify the output path of the judger.
		example: directory.output = ./tests/outputs

2. run.bat:
   we will test your project using similar command.
   example: java -cp path_to_fatworm.jar;bin/;  org.acmclass.fatworm.test.TestTool -ts arno -tm fat

usage for the TestTool:
Usage: java -cp [THIS_JAR] org.acmclass.fatworm.test.TestTool [options]
The options are:
		-h, --help          Show this help message
		--conf              Load specified configuration file instead of test.conf
		-g, --gen-mode      Call MySQL to generate answer when true
		-d, --driver        Specify driver class name in arguments
		-ts, --testsuit     Specify testsuit in arguments
		-tm, --teamname     Specify team name, default MySQL or Fatworm
		--cases             Control the testcases in the testsuit, split the cases with COMMA(,),
							and disabled cases are prepended with EXCALMATORY MARK(!)
		--outSubDir         Write output file in a sub directory
		--timeMultiplier    A multiplier(double) of time limit
							
