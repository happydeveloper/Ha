ECHO OFF
SET JAVA_HOME=D:\Weblogic8.1\jdk142_05
cd bin\classes
"%JAVA_HOME%\bin\jar" -cvf ..\..\org.snu.ids.parser.0.9.jar org/snu/ids/ha
cd ../..
ECHO ON